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
    private final org.example.service.CurrentUserService currentUser;

    // 1. API Hiển thị danh sách tủ lạnh (Có hỗ trợ lọc ?storageType=COOLER hoặc FREEZER)
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserFridgeListResponseDto> getFridgeByUser(
            org.springframework.security.core.Authentication auth,
            @PathVariable Integer userId,
            @RequestParam(required = false) UserFridge.StorageType storageType) {
        currentUser.requireOwner(auth, userId);
        UserFridgeListResponseDto response = userFridgesService.getItemsByUser(userId, storageType);
        return ResponseEntity.ok(response);
    }

    // 2. API Thêm nguyên liệu
    @PostMapping({ "", "/" })
    public ResponseEntity<UserFridgeResponseDto> addFridgeItem(org.springframework.security.core.Authentication auth, @RequestBody UserFridgeRequestDto requestDto) {
        Integer userId = currentUser.id(auth);
        if (requestDto.getUserId() != null) currentUser.requireOwner(auth, requestDto.getUserId());
        requestDto.setUserId(userId);
        UserFridgeResponseDto response = userFridgesService.addFridgeItem(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 3. API Sửa nguyên liệu
    @PutMapping("/{id}")
    public ResponseEntity<UserFridgeResponseDto> updateFridgeItem(
            org.springframework.security.core.Authentication auth,
            @PathVariable Integer id,
            @RequestBody UserFridgeRequestDto requestDto) {
        currentUser.requireFridgeOwner(auth, id);
        if (requestDto.getUserId() != null) currentUser.requireOwner(auth, requestDto.getUserId());
        UserFridgeResponseDto response = userFridgesService.updateFridgeItem(id, requestDto);
        return ResponseEntity.ok(response);
    }

    // 4. API Xóa nguyên liệu
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFridgeItem(org.springframework.security.core.Authentication auth, @PathVariable Integer id) {
        currentUser.requireFridgeOwner(auth, id);
        userFridgesService.deleteFridgeItem(id);
        return ResponseEntity.ok("Xóa nguyên liệu thành công!");
    }
}