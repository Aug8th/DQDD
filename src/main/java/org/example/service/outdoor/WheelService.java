package org.example.service.outdoor;

import org.example.entity.outdoor.CustomWheel;
import org.example.entity.outdoor.WheelItem;
import org.example.enums.WheelType;
import org.example.repository.outdoor.CustomWheelRepository;
import org.example.repository.outdoor.WheelItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class WheelService {

    private final CustomWheelRepository wheelRepository;
    private final WheelItemRepository itemRepository;

    // Constructor injection
    public WheelService(
            CustomWheelRepository wheelRepository,
            WheelItemRepository itemRepository
    ) {
        this.wheelRepository = wheelRepository;
        this.itemRepository = itemRepository;
    }

    // ==========================================
    // CREATE WHEEL
    // ==========================================

    public CustomWheel createWheel(
            Integer userId,
            String name,
            WheelType wheelType
    ) {

        CustomWheel wheel = new CustomWheel();

        wheel.setUserId(userId);
        wheel.setName(name);
        wheel.setWheelType(wheelType);

        return wheelRepository.save(wheel);
    }

    // ==========================================
    // GET WHEEL
    // ==========================================

    public CustomWheel getWheel(Integer wheelId) {

        return wheelRepository.findById(wheelId)
                .orElseThrow(() ->
                        new RuntimeException("Wheel not found")
                );
    }

    // ==========================================
    // GET USER'S WHEELS
    // ==========================================

    public List<CustomWheel> getUserWheels(Integer userId) {

        return wheelRepository.findByUserId(userId);
    }

    // ==========================================
    // ADD ITEM
    // ==========================================

    public WheelItem addItem(
            Integer wheelId,
            String itemName
    ) {

        CustomWheel wheel = getWheel(wheelId);

        WheelItem item = new WheelItem();

        item.setWheel(wheel);
        item.setItemName(itemName);
        item.setIsExcluded(false);

        return itemRepository.save(item);
    }

    // ==========================================
    // UPDATE ITEM
    // ==========================================

    public WheelItem updateItem(
            Integer wheelId,
            Integer itemId,
            String itemName,
            Boolean isExcluded
    ) {

        WheelItem item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found")
                );

        // Make sure this item belongs to this wheel
        if (!item.getWheel().getId().equals(wheelId)) {
            throw new RuntimeException(
                    "Item does not belong to this wheel"
            );
        }

        item.setItemName(itemName);

        if (isExcluded != null) {
            item.setIsExcluded(isExcluded);
        }

        return itemRepository.save(item);
    }

    // ==========================================
    // DELETE ITEM
    // ==========================================

    public void deleteItem(
            Integer wheelId,
            Integer itemId
    ) {

        WheelItem item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found")
                );

        // Make sure this item belongs to this wheel
        if (!item.getWheel().getId().equals(wheelId)) {
            throw new RuntimeException(
                    "Item does not belong to this wheel"
            );
        }

        itemRepository.delete(item);
    }

    // ==========================================
    // RANDOM SPIN
    // ==========================================

    public WheelItem spin(Integer wheelId) {

        // Get all items
        List<WheelItem> items =
                itemRepository.findByWheelId(wheelId);

        // Remove excluded items
        List<WheelItem> availableItems = items.stream()
                .filter(item ->
                        !Boolean.TRUE.equals(item.getIsExcluded())
                )
                .toList();

        // No items available
        if (availableItems.isEmpty()) {
            throw new RuntimeException(
                    "No available items in this wheel"
            );
        }

        // Random item
        Random random = new Random();

        int randomIndex =
                random.nextInt(availableItems.size());

        return availableItems.get(randomIndex);
    }
}