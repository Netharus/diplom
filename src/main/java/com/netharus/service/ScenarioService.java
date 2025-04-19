package com.netharus.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ScenarioService {

    byte[] exportScenario(String scenario) throws IOException;

    int[] importScenario(MultipartFile file) throws IOException;

    String sendScenario(String scenario);
}
