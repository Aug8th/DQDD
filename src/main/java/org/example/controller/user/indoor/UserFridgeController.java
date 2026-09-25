package org.example.controller.user.indoor;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeListResponseDto; // Import DTO mới
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.service.indoor.UserFridgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fridges")
@RequiredArgsConstructor
public class UserFridgeController {

    private final UserFridgeService userFridgeService;

    // 1. API Hiển thị danh sách tủ lạnh (Tự động xóa đồ quá hạn và kèm thông báo)
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserFridgeListResponseDto> getFridgeByUser(@PathVariable Integer userId) {
        UserFridgeListResponseDto response = userFridgeService.getItemsByUser(userId);
        return ResponseEntity.ok(response);
    }

    // 2. API Thêm nguyên liệu
    @PostMapping
    public ResponseEntity<?> addFridgeItem(@RequestBody UserFridgeRequestDto requestDto) {
        try {
            UserFridgeResponseDto newItem = userFridgeService.addFridgeItem(requestDto);
            return ResponseEntity.ok(newItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 3. API Xóa nguyên liệu
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFridgeItem(@PathVariable Integer id) {
        try {
            userFridgeService.deleteFridgeItem(id);
            return ResponseEntity.ok("Xóa nguyên liệu thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 4. API Sửa nguyên liệu
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFridgeItem(
            @PathVariable Integer id,
            @RequestBody UserFridgeRequestDto requestDto) {
        try {
            UserFridgeResponseDto updatedItem = userFridgeService.updateFridgeItem(id, requestDto);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}