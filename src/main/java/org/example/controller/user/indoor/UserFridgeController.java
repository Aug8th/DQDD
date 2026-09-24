package org.example.controller.user.indoor;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.service.indoor.UserFridgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fridges")
@RequiredArgsConstructor
public class UserFridgeController {

    private final UserFridgeService userFridgeService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserFridgeResponseDto>> getFridgeByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(userFridgeService.getItemsByUser(userId));
    }

    @PostMapping
    public ResponseEntity<UserFridgeResponseDto> addFridgeItem(@RequestBody UserFridgeRequestDto requestDto) {
        return ResponseEntity.ok(userFridgeService.addFridgeItem(requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFridgeItem(@PathVariable Integer id) {
        userFridgeService.deleteFridgeItem(id);
        return ResponseEntity.ok("Xóa nguyên liệu thành công!");
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserFridgeResponseDto> updateFridgeItem(
            @PathVariable Integer id,
            @RequestBody UserFridgeRequestDto requestDto) {
        UserFridgeResponseDto updatedItem = userFridgeService.updateFridgeItem(id, requestDto);
        return ResponseEntity.ok(updatedItem);
    }
}