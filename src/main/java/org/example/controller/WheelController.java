package org.example.controller;

import org.example.entity.outdoor.CustomWheel;
import org.example.entity.outdoor.WheelItem;
import org.example.repository.outdoor.CustomWheelRepository;
import org.example.repository.outdoor.WheelItemRepository;

import org.example.service.outdoor.WheelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/custom-wheel")
public class WheelController {

    private final CustomWheelRepository customWheelRepository;
    private final WheelItemRepository wheelItemRepository;
    private final WheelService wheelService;

    public WheelController(
            CustomWheelRepository customWheelRepository,
            WheelItemRepository wheelItemRepository,
            WheelService wheelService) {

        this.customWheelRepository = customWheelRepository;
        this.wheelItemRepository = wheelItemRepository;
        this.wheelService = wheelService;
    }

    // CREATE NEW WHEEL


    @PostMapping
    public ResponseEntity<CustomWheel> createWheel(
            @RequestBody CustomWheel wheel) {

        CustomWheel savedWheel = customWheelRepository.save(wheel);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedWheel);
    }

    // GET ALL WHEELS


    @GetMapping
    public ResponseEntity<List<CustomWheel>> getAllWheels() {

        return ResponseEntity.ok(
                customWheelRepository.findAll()
        );
    }

    // GET ONE WHEEL


    @GetMapping("/{wheelId}")
    public ResponseEntity<CustomWheel> getWheel(
            @PathVariable Integer wheelId) {

        Optional<CustomWheel> wheel =
                customWheelRepository.findById(wheelId);

        if (wheel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(wheel.get());
    }

    // ADD ITEM TO WHEEL

    @PostMapping("/{wheelId}/items")
    public ResponseEntity<?> addItem(
            @PathVariable Integer wheelId,
            @RequestBody WheelItem item) {

        Optional<CustomWheel> wheel =
                customWheelRepository.findById(wheelId);

        if (wheel.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Wheel not found");
        }

        item.setWheel(wheel.get());

        WheelItem savedItem =
                wheelItemRepository.save(item);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedItem);
    }

    // GET ITEMS OF A WHEEL


    @GetMapping("/{wheelId}/items")
    public ResponseEntity<?> getWheelItems(
            @PathVariable Integer wheelId) {

        Optional<CustomWheel> wheel =
                customWheelRepository.findById(wheelId);

        if (wheel.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Wheel not found");
        }

        return ResponseEntity.ok(
                wheelItemRepository.findAll()
                        .stream()
                        .filter(item ->
                                item.getWheel()
                                        .getId()
                                        .equals(wheelId))
                        .toList()
        );
    }

    // UPDATE ITEM


    @PutMapping("/{wheelId}/items/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable Integer wheelId,
            @PathVariable Integer itemId,
            @RequestBody WheelItem itemData) {

        Optional<CustomWheel> wheel =
                customWheelRepository.findById(wheelId);

        if (wheel.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Wheel not found");
        }

        Optional<WheelItem> item =
                wheelItemRepository.findById(itemId);

        if (item.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        WheelItem existingItem = item.get();

        // Make sure the item belongs to this wheel
        if (!existingItem.getWheel()
                .getId()
                .equals(wheelId)) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("This item does not belong to this wheel");
        }

        existingItem.setItemName(itemData.getItemName());

        if (itemData.getIsExcluded() != null) {
            existingItem.setIsExcluded(
                    itemData.getIsExcluded()
            );
        }

        WheelItem updatedItem =
                wheelItemRepository.save(existingItem);

        return ResponseEntity.ok(updatedItem);
    }

    // DELETE ITEM


    @DeleteMapping("/{wheelId}/items/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Integer wheelId,
            @PathVariable Integer itemId) {

        Optional<WheelItem> item =
                wheelItemRepository.findById(itemId);

        if (item.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        WheelItem existingItem = item.get();

        // Make sure the item belongs to this wheel
        if (!existingItem.getWheel()
                .getId()
                .equals(wheelId)) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("This item does not belong to this wheel");
        }

        wheelItemRepository.delete(existingItem);

        return ResponseEntity.ok(
                "Item deleted successfully"
        );
    }

// SPIN WHEEL


    @PostMapping("/{wheelId}/spin")
    public ResponseEntity<?> spinWheel(
            @PathVariable Integer wheelId) {

        try {

            WheelItem result =
                    wheelService.spinWheel(wheelId);

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    //search

    @PostMapping("/spin")
    public ResponseEntity<?> spinCustomWheel(
            @RequestBody List<String> items) {

        if (items == null || items.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Danh sách món ăn không được để trống");
        }

        // Loại bỏ item rỗng
        List<String> validItems = items.stream()
                .filter(item -> item != null)
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .toList();

        if (validItems.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Danh sách không có món ăn hợp lệ");
        }

        Collections.shuffle(validItems);

        String result = validItems.get(0);

        return ResponseEntity.ok(result);
    }
}