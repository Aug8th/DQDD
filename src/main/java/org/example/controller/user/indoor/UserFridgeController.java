package org.example.controller.user.indoor;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeListResponseDto;
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.entity.indoor.UserFridge;
import org.example.service.indoor.UserFridgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fridges")
@RequiredArgsConstructor
public class UserFridgeController {

    private final UserFridgeService userFridgesService;

    // 1. API Hiển thị danh sách tủ lạnh (Có hỗ trợ lọc ?storageType=COOLER hoặc FREEZER)
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserFridgeListResponseDto> getFridgeByUser(
            @PathVariable Integer userId,
            @RequestParam(required = false) UserFridge.StorageType storageType) {
        UserFridgeListResponseDto response = userFridgesService.getItemsByUser(userId, storageType);
        return ResponseEntity.ok(response);
    }

    // 2. API Thêm nguyên liệu
    @PostMapping({ "", "/" })
    public ResponseEntity<UserFridgeResponseDto> addFridgeItem(@RequestBody UserFridgeRequestDto requestDto) {
        UserFridgeResponseDto response = userFridgesService.addFridgeItem(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 3. API Sửa nguyên liệu
    @PutMapping("/{id}")
    public ResponseEntity<UserFridgeResponseDto> updateFridgeItem(
            @PathVariable Integer id,
            @RequestBody UserFridgeRequestDto requestDto) {
        UserFridgeResponseDto response = userFridgesService.updateFridgeItem(id, requestDto);
        return ResponseEntity.ok(response);
    }

    // 4. API Xóa nguyên liệu
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFridgeItem(@PathVariable Integer id) {
        userFridgesService.deleteFridgeItem(id);
        return ResponseEntity.ok("Xóa nguyên liệu thành công!");
    }
}