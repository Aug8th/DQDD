package org.example.service.outdoor;

import org.example.dto.response.outdoor.OutdoorThemeResponse;
import org.example.entity.outdoor.OutdoorTheme;
import org.example.repository.OutdoorThemeRepository;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OutdoorThemeService {

    private final OutdoorThemeRepository outdoorThemeRepository;

    public OutdoorThemeService(
            OutdoorThemeRepository outdoorThemeRepository) {
        this.outdoorThemeRepository = outdoorThemeRepository;
    }

    public List<OutdoorThemeResponse> getRandomThemes() {

        List<OutdoorTheme> themes =
                outdoorThemeRepository.findAll();

        Collections.shuffle(themes);

        return themes.stream()
                .limit(6)
                .map(OutdoorThemeResponse::fromEntity)
                .toList();
    }
}