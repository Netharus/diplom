package com.netharus.domain.enums;

import com.netharus.domain.dto.response.GesturesDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Gestures {
    THUMBS_UP("Палец вверх", "ThumbsUp", 1),
    FIST("Кулак", "Fist", 2),
    PALM_UP("Ладонь вверх", "PalmUp", 3),
    ROCK_ON("Рок-н-ролл", "RockOn", 4),
    ONE_FINGER_UP("Один палец вверх", "OneFingerUp", 5),
    TWO_FINGERS_UP("Два пальца вверх", "TwoFingersUp", 6),
    CALL("Звонок", "Call", 7),
    VICTORY("Победа", "Victory", 8);

    private final String title;
    private final String name;
    private final int id;

    public static String getGestureTitle(int id) {
        return values()[id - 1].getTitle();
    }

    public static String getGestures(String scenario) {
        List<Integer> gestureIds = Arrays.stream(scenario.trim().split("\\s+"))
                .map(Integer::parseInt)
                .toList();
        return gestureIds.stream().map(Gestures::getGestureTitle).collect(Collectors.joining(", "));
    }

    public static String getGestures(List<Integer> gestureIds) {
        return gestureIds.stream().map(Gestures::getGestureTitle).collect(Collectors.joining(", "));
    }

    public static boolean isGesture(int id) {
        return Arrays.stream(values()).anyMatch(value -> value.getId() == id);
    }

    public static boolean isGesture(int[] gesturesIds) {
        for (int gesturesId : gesturesIds) {
            if (Arrays.stream(values()).noneMatch(value -> value.getId() == gesturesId))
                return false;
        }
        return true;
    }

    public static int amount() {
        return values().length;
    }

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
