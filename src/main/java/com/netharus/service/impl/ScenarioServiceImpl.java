package com.netharus.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netharus.domain.enums.Gestures;
import com.netharus.exceptions.IllegalScenarioFileFormatException;
import com.netharus.exceptions.IllegalStringFormatException;
import com.netharus.service.ScenarioService;
import com.netharus.stringConstants.ErrorMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScenarioServiceImpl implements ScenarioService {

    @Override
    public byte[] exportScenario(String scenario) throws IOException {
        if (!isValidFormat(scenario)) {
            throw new IllegalScenarioFileFormatException(ErrorMessages.ILLEGAL_SCENARIO_FILE_FORMAT_EXCEPTION);
        }

        String[] scenarioParts = scenario.split(" ");
        int[] scenarioArray = new int[scenarioParts.length];

        for (int i = 0; i < scenarioParts.length; i++) {
            scenarioArray[i] = Integer.parseInt(scenarioParts[i]);
        }

        log.info("Экспортированные жесты: {}", Arrays.toString(scenarioArray));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsBytes(scenarioArray);
    }

    @Override
    public int[] importScenario(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        int[] scenarioArray = objectMapper.readValue(file.getInputStream(), int[].class);
        if (!Gestures.isGesture(scenarioArray)) {
            throw new IllegalScenarioFileFormatException(ErrorMessages.ILLEGAL_SCENARIO_FILE_FORMAT_EXCEPTION);
        }
        log.info("Импортированные жесты: {}", Arrays.toString(scenarioArray));
        return scenarioArray;
    }

    @Override
    public String sendScenario(String scenario) {
        String scenarioString;
        if (isValidFormat(scenario)) {
            List<Integer> gestureIds = parseScenario(scenario);
            scenarioString = gestureIds.stream().map(Gestures::getGestureTitle).collect(Collectors.joining(","));
            log.info("Полученные жесты: {}", gestureIds);
        } else {
            throw new IllegalStringFormatException(ErrorMessages.ILLEGAL_STRING_FORMAT_EXCEPTION);
        }
        return scenarioString;
    }

    private List<Integer> parseScenario(String scenario) {
        return Arrays.stream(scenario.trim().split("\\s+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public static boolean isValidFormat(String scenario) {
        String regexp = String.format("^(?:[1-%d](?:\\s+[1-8]){0,4})$", Gestures.amount());
        log.info(regexp);
        return scenario.trim().matches(regexp);
    }
}
