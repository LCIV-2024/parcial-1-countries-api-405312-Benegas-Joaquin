package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.CountryRequestDTO;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
class CountryServiceTest {
    @MockBean
    private CountryRepository countryRepository;

    @MockBean
    private RestTemplate restTemplate;

    @SpyBean
    private CountryService countryService;

    @Test
    void getAllCountries() {
        Map<String, Object> country1 = new HashMap<>();
        country1.put("name", "Argentina");
        Map<String, Object> country2 = new HashMap<>();
        country2.put("name", "Brasil");
        List<Map<String, Object>> countryList = List.of(country1, country2);

        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class))
                .thenReturn(countryList);

        List<Country> countries = countryService.getAllCountries();
        assertEquals(2, countries.size());
        assertEquals("Argentina", countries.get(0).getName());
    }

    @Test
    void getCountryList() {
        Country country1 = new Country();
        country1.setName("Argentina");
        country1.setCode("ARG");
        country1.setRegion("Americas");

        Country country2 = new Country();
        country2.setName("Brasil");
        country2.setCode("BR");
        country2.setRegion("Americas");

        List<Country> countryList = List.of(country1, country2);

        doReturn(countryList).when(countryService).getAllCountries();

        List<CountryDTO> countryDTOList = countryService.getCountryList();

        assertEquals(2, countryDTOList.size());
        assertEquals("Argentina", countryDTOList.get(0).getName());
        assertEquals("Brasil", countryDTOList.get(1).getName());
    }

    @Test
    void getCountryListByName() {
        Country country1 = new Country();
        country1.setName("Argentina");
        country1.setCode("ARG");
        country1.setRegion("Americas");

        Country country2 = new Country();
        country2.setName("Brasil");
        country2.setCode("BR");
        country2.setRegion("Americas");

        List<Country> countryList = List.of(country1, country2);

        doReturn(countryList).when(countryService).getAllCountries();

        List<CountryDTO> countryResponseList = countryService.getCountryListByNameOrCode("ARG", null);

        assertEquals(1, countryResponseList.size());
        assertEquals("Argentina", countryResponseList.get(0).getName());
    }

    @Test
    void getCountryListByContinent() {
        Country country1 = new Country();
        country1.setName("Argentina");
        country1.setCode("ARG");
        country1.setRegion("Americas");

        Country country2 = new Country();
        country2.setName("Brasil");
        country2.setCode("BR");
        country2.setRegion("Americas");

        List<Country> countryList = List.of(country1, country2);

        doReturn(countryList).when(countryService).getAllCountries();

        List<CountryDTO> countryResponseList = countryService.getCountryListByContinent("Americas");

        assertEquals(2, countryResponseList.size());
        assertEquals("Argentina", countryResponseList.get(0).getName());
    }

    @Test
    void getCountryListByLanguage() {
        Country country1 = new Country();
        country1.setName("Argentina");
        country1.setCode("ARG");
        country1.setRegion("Americas");
        Map<String, String> language = new HashMap<>();
        language.put("ESP", "Spanish");
        country1.setLanguages(language);

        List<Country> countryList = List.of(country1);

        doReturn(countryList).when(countryService).getAllCountries();

        List<CountryDTO> countryResponseList = countryService.getCountryListByLanguage("Spanish");

        assertEquals(1, countryResponseList.size());
        assertEquals("Argentina", countryResponseList.get(0).getName());
        assertEquals("ARG", countryResponseList.get(0).getCode());
    }

    @Test
    void getCountryWithMostBorders() {
        Country country1 = new Country();
        country1.setName("Argentina");
        country1.setCode("ARG");
        country1.setRegion("Americas");
        List<String> borders1 = List.of("Border 1", "Border 2");
        country1.setBorders(borders1);

        Country country2 = new Country();
        country2.setName("Brasil");
        country2.setCode("BR");
        country2.setRegion("Americas");
        List<String> borders2 = List.of("Border 1", "Border 2", "Border 3", "Border 4");
        country2.setBorders(borders2);

        List<Country> countryList = List.of(country1, country2);

        doReturn(countryList).when(countryService).getAllCountries();

        CountryDTO countryResponse = countryService.getCountryWithMostBorders();

        assertEquals("Brasil", countryResponse.getName());
    }

    @Test
    void createCountryList() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.createCountryList(new CountryRequestDTO(15));
        });

        String expectedMessage = "Solo está permitido el guardado de 10 países como máximo";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createCountryListSuccess() {
        Country country1 = new Country();
        country1.setName("Argentina");
        country1.setCode("ARG");

        Country country2 = new Country();
        country2.setName("Brasil");
        country2.setCode("BR");

        List<Country> countryList = new ArrayList<>();
        countryList.add(country1);
        countryList.add(country2);

        CountryEntity countryEntity1 = new CountryEntity();
        countryEntity1.setName("Argentina");
        countryEntity1.setCode("ARG");

        CountryEntity countryEntity2 = new CountryEntity();
        countryEntity2.setName("Brasil");
        countryEntity2.setCode("BR");

        CountryEntity countryEntitySaved1 = new CountryEntity();
        countryEntity1.setId(1L);
        countryEntity1.setName("Argentina");
        countryEntity1.setCode("ARG");

        CountryEntity countryEntitySaved2 = new CountryEntity();
        countryEntity2.setId(2L);
        countryEntity2.setName("Brasil");
        countryEntity2.setCode("BR");

        doReturn(countryList).when(countryService).getAllCountries();

        when(countryRepository.save(countryEntity1)).thenReturn(countryEntitySaved1);
        when(countryRepository.save(countryEntity2)).thenReturn(countryEntitySaved2);

        List<CountryDTO> countryDTOList = countryService.createCountryList(new CountryRequestDTO(2));

        assertEquals(2, countryDTOList.size());
        assertEquals("Argentina", countryDTOList.get(0).getName());
    }
}