package org.example.service.outdoor;


import org.example.entity.outdoor.CustomWheel;
import org.example.entity.outdoor.WheelItem;
import org.example.repository.outdoor.CustomWheelRepository;
import org.example.repository.outdoor.WheelItemRepository;
import org.springframework.stereotype.Service;
import org.example.service.outdoor.WheelService;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class WheelService {

    private final CustomWheelRepository customWheelRepository;
    private final WheelItemRepository wheelItemRepository;
    private final WheelService wheelService;

    private final Random random = new Random();

    public WheelService(
            CustomWheelRepository customWheelRepository,
            WheelItemRepository wheelItemRepository, WheelService wheelService) {

        this.customWheelRepository = customWheelRepository;
        this.wheelItemRepository = wheelItemRepository;
        this.wheelService = wheelService;
    }

    // RANDOM SPIN

    public WheelItem spinWheel(Integer wheelId) {

        // Find wheel
        Optional<CustomWheel> wheel =
                customWheelRepository.findById(wheelId);

        if (wheel.isEmpty()) {
            throw new RuntimeException("Wheel not found");
        }

        // Get all items of this wheel
        List<WheelItem> items =
                wheelItemRepository.findAll()
                        .stream()
                        .filter(item ->
                                item.getWheel()
                                        .getId()
                                        .equals(wheelId))
                        .filter(item ->
                                !Boolean.TRUE.equals(
                                        item.getIsExcluded()
                                ))
                        .toList();

        // No available items
        if (items.isEmpty()) {
            throw new RuntimeException(
                    "No available items in this wheel"
            );
        }

        // Random
        int randomIndex =
                random.nextInt(items.size());

        return items.get(randomIndex);
    }
}