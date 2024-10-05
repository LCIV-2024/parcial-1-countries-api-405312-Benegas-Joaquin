package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.dtos.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.CountryRequestDTO;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<List<CountryDTO>> getCountryList(@RequestParam(required = false) String code, @RequestParam(required = false) String name) {
        return ResponseEntity.ok(countryService.getCountryListByNameOrCode(code, name));
    }

    @GetMapping("/{continent}/continent")
    public ResponseEntity<List<CountryDTO>> getCountryListByContinent(@PathVariable String continent) {
        return ResponseEntity.ok(countryService.getCountryListByContinent(continent));
    }

    @GetMapping("/{language}/language")
    public ResponseEntity<List<CountryDTO>> getCountryListByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(countryService.getCountryListByLanguage(language));
    }

    @GetMapping("/most-borders")
    public ResponseEntity<CountryDTO> getCountryWithMostBorders() {
        return ResponseEntity.ok(countryService.getCountryWithMostBorders());
    }

    @PostMapping
    public ResponseEntity<List<CountryDTO>> createCountryList(@RequestBody CountryRequestDTO countryRequestDTO) {
        return ResponseEntity.ok(countryService.createCountryList(countryRequestDTO));
    }
}