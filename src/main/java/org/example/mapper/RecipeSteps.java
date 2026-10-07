package org.example.mapper;

import java.util.Arrays;
import java.util.List;

@lombok.experimental.UtilityClass
public class RecipeSteps {


    public static List<String> fromInstructions(String instructions) {
        if (instructions == null || instructions.isBlank()) { return List.of(); }
        return Arrays.stream(instructions.split("\\R"))
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(s -> s.replaceFirst("^(?:Bước\\s+)?[0-9]+[.):]\\s*", ""))
                .toList();
    }
}
