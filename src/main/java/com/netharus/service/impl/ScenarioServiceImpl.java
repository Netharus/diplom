package com.netharus.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netharus.service.ScenarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class ScenarioServiceImpl implements ScenarioService {

    @Override
    public byte[] exportScenario(String scenario) throws IOException {
        String[] scenarioParts = scenario.split(" ");
        int[] scenarioArray = new int[scenarioParts.length];

        for (int i = 0; i < scenarioParts.length; i++) {
            scenarioArray[i] = Integer.parseInt(scenarioParts[i]);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsBytes(scenarioArray);
    }

    @Override
    public int[] importScenario(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file.getBytes(), int[].class);
    }
}
