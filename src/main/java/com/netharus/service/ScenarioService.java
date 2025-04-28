package com.netharus.service;

import com.netharus.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ScenarioService {

    byte[] exportScenario(String scenario) throws IOException;

    int[] importScenario(MultipartFile file) throws IOException;

    void sendScenario(User user, String scenario);
}
