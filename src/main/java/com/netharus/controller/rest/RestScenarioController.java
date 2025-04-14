package com.netharus.controller.rest;

import com.netharus.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RestScenarioController {

    private final ScenarioService scenarioService;

    @GetMapping("/exportScenario")
    public ResponseEntity<byte[]> exportScenario(@RequestParam String scenario) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scenario.json");

        return new ResponseEntity<>(scenarioService.exportScenario(scenario), headers, HttpStatus.OK);
    }

    @PostMapping("/importScenario")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<int[]> importScenario(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(scenarioService.importScenario(file));
    }
}
