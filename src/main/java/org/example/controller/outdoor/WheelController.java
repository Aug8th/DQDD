package org.example.controller.outdoor;

import lombok.RequiredArgsConstructor;
import org.example.entity.BeuDuong.CustomWheel;
import org.example.entity.BeuDuong.WheelItem;
import org.example.enums.WheelType;
import org.example.service.outdoor.WheelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/outdoor/wheels")
public class WheelController {

    private final WheelService wheelService;


    // Get all wheels
    @GetMapping
    public ResponseEntity<List<CustomWheel>> getAllWheels() {
        return ResponseEntity.ok(wheelService.getAllWheels());
    }


    // CREATE WHEEL=

    @PostMapping
    public ResponseEntity<CustomWheel> createWheel(
            @RequestBody Map<String, Object> request
    ) {

        Integer userId = Integer.valueOf(
                request.get("userId").toString()
        );

        String name = request.get("name").toString();

        WheelType wheelType = WheelType.valueOf(
                request.get("wheelType").toString()
        );

        CustomWheel wheel = wheelService.createWheel(
                userId,
                name,
                wheelType
        );

        return ResponseEntity.ok(wheel);
    }


    // GET WHEEL

    @GetMapping("/{wheelId}")
    public ResponseEntity<CustomWheel> getWheel(
            @PathVariable Integer wheelId
    ) {

        return ResponseEntity.ok(
                wheelService.getWheel(wheelId)
        );
    }


    // GET USER'S WHEELS


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CustomWheel>> getUserWheels(
            @PathVariable Integer userId
    ) {

        return ResponseEntity.ok(
                wheelService.getUserWheels(userId)
        );
    }


    // ADD ITEM

    @PostMapping("/{wheelId}/items")
    public ResponseEntity<WheelItem> addItem(
            @PathVariable Integer wheelId,
            @RequestBody Map<String, String> request
    ) {

        String itemName = request.get("itemName");

        WheelItem item = wheelService.addItem(
                wheelId,
                itemName
        );

        return ResponseEntity.ok(item);
    }

    // UPDATE ITEM


    @PutMapping("/{wheelId}/items/{itemId}")
    public ResponseEntity<WheelItem> updateItem(
            @PathVariable Integer wheelId,
            @PathVariable Integer itemId,
            @RequestBody Map<String, Object> request
    ) {

        String itemName = request
                .get("itemName")
                .toString();

        Boolean isExcluded = null;

        if (request.get("isExcluded") != null) {
            isExcluded = Boolean.valueOf(
                    request.get("isExcluded").toString()
            );
        }

        WheelItem item = wheelService.updateItem(
                wheelId,
                itemId,
                itemName,
                isExcluded
        );

        return ResponseEntity.ok(item);
    }


    // DELETE ITEM

    @DeleteMapping("/{wheelId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Integer wheelId,
            @PathVariable Integer itemId) {

        wheelService.deleteItem(wheelId, itemId);

        return ResponseEntity.noContent().build();
    }

    // SPIN WHEEL


    @PostMapping("/{wheelId}/spin")
    public ResponseEntity<WheelItem> spin(
            @PathVariable Integer wheelId
    ) {

        WheelItem result =
                wheelService.spin(wheelId);

        return ResponseEntity.ok(result);
    }

      //get wheel items (all)
    @GetMapping("/{wheelId}/items")
    public ResponseEntity<List<WheelItem>> getWheelItems(
            @PathVariable Integer wheelId
    ) {
        return ResponseEntity.ok(
                wheelService.getWheelItems(wheelId)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomWheel>> searchWheels(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(
                wheelService.searchWheelsByName(name)
        );
    }

    //Change wheel items
    @PostMapping("/{wheelId}/change-item/{oldItemId}")
    public ResponseEntity<WheelItem> changeItem(
            @PathVariable Integer wheelId,
            @PathVariable Integer oldItemId
    ) {
        return ResponseEntity.ok(
                wheelService.changeItem(wheelId, oldItemId)
        );
    }
}