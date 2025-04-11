package com.netharus.domain.enums;

import com.netharus.domain.dto.reponse.GesturesDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum Gestures {
    THUMBS_UP("Thumbs Up", "ThumbsUp", 1),
    FIST("Fist", "Fist", 2),
    PALM_UP("Palm Up", "PalmUp", 3),
    ROCK_ON("Rock On", "RockOn", 4),
    ONE_FINGER_UP("One Finger Up", "OneFingerUp", 5),
    TWO_FINGERS_UP("Two Fingers Up", "TwoFingersUp", 6),
    CALL("Call", "Call", 7),
    VICTORY("Victory", "Victory", 8);

    private final String title;
    private final String name;
    private final int id;

    public static List<GesturesDto> getAll() {
        return Arrays.stream(values())
                .map(gesture -> (GesturesDto.builder()
                        .id(gesture.id)
                        .name(gesture.name)
                        .title(gesture.title)
                        .build()))
                .toList();
    }
}
