package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.CountryRequestDTO;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.exception.CountryNotFoundException;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;

    public List<Country> getAllCountries() {
        String url = "https://restcountries.com/v3.1/all";
        List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
        return response.stream().map(this::mapToCountry).collect(Collectors.toList());
    }

    /**
     * Agregar mapeo de campo cca3 (String)
     * Agregar mapeo campos borders ((List<String>))
     */
    private Country mapToCountry(Map<String, Object> countryData) {
        Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
        return Country.builder()
                .name((String) nameData.get("common"))
                .population(((Number) countryData.get("population")).longValue())
                .area(((Number) countryData.get("area")).doubleValue())
                .region((String) countryData.get("region"))
                .languages((Map<String, String>) countryData.get("languages"))
                .code((String) countryData.get("cca3"))
                .borders((List<String>) countryData.get("borders"))
                .build();
    }


    private CountryDTO mapToDTO(Country country) {
        return new CountryDTO(country.getCode(), country.getName());
    }

    public List<CountryDTO> getCountryList() {
        List<Country> countryList = this.getAllCountries();

        List<CountryDTO> countryDTOList = new ArrayList<>();
        for (Country country : countryList) {
            countryDTOList.add(this.mapToDTO(country));
        }

        return countryDTOList;
    }

    public List<CountryDTO> getCountryListByNameOrCode(String code, String name) {
        boolean mustFilterByCode = code != null;
        boolean mustFilterByName = name != null;

        if (!mustFilterByCode && !mustFilterByName) {
            return getCountryList();
        }

        List<Country> countryList = this.getAllCountries();
        List<CountryDTO> countryDTOList = new ArrayList<>();
        for (Country country : countryList) {
            boolean mustItemBeAdded = false;

            if (mustFilterByCode) {
                if (country.getCode().equals(code)) {
                    mustItemBeAdded = true;
                }
            }

            if (mustFilterByName && country.getName().equals(name)) {
                mustItemBeAdded = true;
            }

            if (mustItemBeAdded) {
                countryDTOList.add(this.mapToDTO(country));
            }
        }

        return countryDTOList;
    }

    public List<CountryDTO> getCountryListByContinent(String continent) {
        List<Country> countryList = this.getAllCountries();

        List<CountryDTO> countryDTOList = new ArrayList<>();
        for (Country country : countryList) {
            if (country.getRegion().equals(continent)) {
                countryDTOList.add(this.mapToDTO(country));
            }
        }

        return countryDTOList;
    }

    public List<CountryDTO> getCountryListByLanguage(String language) {
        List<Country> countryList = this.getAllCountries();

        List<CountryDTO> countryDTOList = new ArrayList<>();
        for (Country country : countryList) {
            if (country.getLanguages() != null && country.getLanguages().containsValue(language)) {
                countryDTOList.add(this.mapToDTO(country));
            }
        }

        return countryDTOList;
    }

    public CountryDTO getCountryWithMostBorders() {
        List<Country> countryList = this.getAllCountries();
        Country countryWithMostBorders = new Country();
        int countryCount = 0;
        for (Country country : countryList) {
            if (country.getBorders() != null && country.getBorders().size() > countryCount) {
                countryWithMostBorders = country;
                countryCount = countryWithMostBorders.getBorders().size();
            }
        }

        return this.mapToDTO(countryWithMostBorders);
    }

    public List<CountryDTO> createCountryList(CountryRequestDTO countryRequestDTO) {
        if (countryRequestDTO.getAmountOfCountryToSave() > 10) {
            throw new IllegalArgumentException("Solo está permitido el guardado de 10 países como máximo");
        }

        List<Country> countryList = this.getAllCountries();
        Collections.shuffle(countryList);

        int amountOfCountryToSave = countryRequestDTO.getAmountOfCountryToSave();

        List<CountryDTO> countryDTOList = new ArrayList<>();
        for (int i = 0; i < amountOfCountryToSave; i++) {
            Country currentCountry = countryList.get(i);
            CountryEntity countryEntity = modelMapper.map(currentCountry, CountryEntity.class);

            countryRepository.save(countryEntity);

            countryDTOList.add(this.mapToDTO(currentCountry));
        }

        return countryDTOList;
    }
}