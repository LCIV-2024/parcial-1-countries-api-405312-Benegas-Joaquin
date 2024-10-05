package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.dtos.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.CountryRequestDTO;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryController.class)
class CountryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCountryList() throws Exception {
        CountryDTO country1 = new CountryDTO();
        country1.setName("Argentina");
        country1.setCode("ARG");

        CountryDTO country2 = new CountryDTO();
        country2.setName("Brasil");
        country2.setCode("BR");

        List<CountryDTO> countryDTOList = List.of(country1, country2);

        when(countryService.getCountryListByNameOrCode(null, null)).thenReturn(countryDTOList);

        mockMvc.perform(get("/api/countries"))
                .andDo(print())
                .andExpect(status().isOk())
                // First element
                .andExpect(jsonPath("$[0].name").value("Argentina"))
                // Second element
                .andExpect(jsonPath("$[1].name").value("Brasil"));
    }

    @Test
    void getCountryListByContinent() throws Exception {
        CountryDTO country1 = new CountryDTO();
        country1.setName("Argentina");
        country1.setCode("ARG");

        CountryDTO country2 = new CountryDTO();
        country2.setName("Brasil");
        country2.setCode("BR");

        List<CountryDTO> countryDTOList = List.of(country1, country2);

        when(countryService.getCountryListByContinent("Americas")).thenReturn(countryDTOList);

        mockMvc.perform(get("/api/countries/Americas/continent"))
                .andDo(print())
                .andExpect(status().isOk())
                // First element
                .andExpect(jsonPath("$[0].name").value("Argentina"))
                // Second element
                .andExpect(jsonPath("$[1].name").value("Brasil"));
    }

    @Test
    void getCountryListByLanguage() throws Exception {
        CountryDTO country1 = new CountryDTO();
        country1.setName("Argentina");
        country1.setCode("ARG");

        List<CountryDTO> countryDTOList = List.of(country1);

        when(countryService.getCountryListByLanguage("Spanish")).thenReturn(countryDTOList);

        mockMvc.perform(get("/api/countries/Spanish/language"))
                .andDo(print())
                .andExpect(status().isOk())
                // First element
                .andExpect(jsonPath("$[0].name").value("Argentina"));
    }

    @Test
    void getCountryWithMostBorders() throws Exception {
        CountryDTO country1 = new CountryDTO();
        country1.setName("Argentina");
        country1.setCode("ARG");

        when(countryService.getCountryWithMostBorders()).thenReturn(country1);

        mockMvc.perform(get("/api/countries/most-borders"))
                .andDo(print())
                .andExpect(status().isOk())
                // First element
                .andExpect(jsonPath("$.name").value("Argentina"));
    }

    @Test
    void createCountryList() throws Exception {
        CountryDTO country1 = new CountryDTO();
        country1.setName("Argentina");
        country1.setCode("ARG");

        CountryDTO country2 = new CountryDTO();
        country2.setName("Brasil");
        country2.setCode("BR");

        List<CountryDTO> countryDTOList = List.of(country1, country2);

        CountryRequestDTO countryRequestDTO = new CountryRequestDTO(2);

        when(countryService.createCountryList(any(CountryRequestDTO.class))).thenReturn(countryDTOList);

        mockMvc.perform(post("/api/countries")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(countryRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                // First element
                .andExpect(jsonPath("$[0].name").value("Argentina"))
                // Second element
                .andExpect(jsonPath("$[1].name").value("Brasil"));
    }
}