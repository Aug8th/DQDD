package org.example.service.outdoor;

import org.example.entity.outdoor.CustomWheel;
import org.example.entity.outdoor.WheelItem;
import org.example.enums.WheelType;
import org.example.entity.outdoor.WheelHistory;
import org.example.repository.outdoor.CustomWheelRepository;
import org.example.repository.outdoor.WheelHistoryRepository;
import org.example.repository.outdoor.WheelItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class WheelService {

    private final CustomWheelRepository wheelRepository;
    private final WheelItemRepository itemRepository;
    private final WheelHistoryRepository historyRepository;

    public WheelService(
            CustomWheelRepository wheelRepository,
            WheelItemRepository itemRepository,
            WheelHistoryRepository historyRepository
    ) {
        this.wheelRepository = wheelRepository;
        this.itemRepository = itemRepository;
        this.historyRepository = historyRepository;
    }


    // CREATE WHEEL

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


    // GET WHEEL

    public CustomWheel getWheel(Integer wheelId) {

        return wheelRepository.findById(wheelId)
                .orElseThrow(() ->
                        new RuntimeException("Wheel not found")
                );
    }


    // GET USER'S WHEELS


    public List<CustomWheel> getUserWheels(Integer userId) {

        return wheelRepository.findByUserId(userId);
    }


    // ADD ITEM

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

    // UPDATE ITEM


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


    // DELETE ITEM


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


    // RANDOM SPIN


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

        // Get Items from Wheels

    public List<WheelItem> getWheelItems(Integer wheelId) {
        // Make sure the wheel exists
        getWheel(wheelId);

        return itemRepository.findByWheelId(wheelId);
    }

    public List<CustomWheel> searchWheelsByName(String name) {
        return wheelRepository.findByNameContainingIgnoreCase(name);
    }

     // Change Wheel Item

    public WheelItem changeItem(
            Integer wheelId,
            Integer oldItemId
    ) {
        CustomWheel wheel = getWheel(wheelId);

        WheelItem oldItem = itemRepository.findById(oldItemId)
                .orElseThrow(() -> new RuntimeException("Old item not found"));

        if (!oldItem.getWheel().getId().equals(wheelId)) {
            throw new RuntimeException("Old item does not belong to this wheel");
        }

        List<WheelItem> items = itemRepository.findByWheelId(wheelId);

        List<WheelItem> availableItems = items.stream()
                .filter(item -> !item.getId().equals(oldItemId))
                .filter(item -> !Boolean.TRUE.equals(item.getIsExcluded()))
                .toList();

        if (availableItems.isEmpty()) {
            throw new RuntimeException("No other item available");
        }

        Random random = new Random();

        WheelItem newItem = availableItems.get(
                random.nextInt(availableItems.size())
        );

        WheelHistory history = new WheelHistory();
        history.setWheel(wheel);
        history.setOldItem(oldItem);
        history.setNewItem(newItem);
        history.setCreatedAt(java.time.LocalDateTime.now());

        historyRepository.save(history);

        return newItem;
    }
}