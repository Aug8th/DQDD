package org.example.service.indoor;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.entity.indoor.UserFridge;
import org.example.mapper.indoor.UserFridgeMapper;
import org.example.repository.indoor.UserFridgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFridgeService {

    private final UserFridgeRepository userFridgeRepository;
    private final UserFridgeMapper userFridgeMapper;
//    hiển thị danh sách nguyên liệu
    public List<UserFridgeResponseDto> getItemsByUser(Integer userId) {
        List<UserFridge> fridges = userFridgeRepository.findByUserId(userId);
        return fridges.stream()
                .map(userFridgeMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    //thêm
    public UserFridgeResponseDto addFridgeItem(UserFridgeRequestDto requestDto) {
        UserFridge fridge = userFridgeMapper.toEntity(requestDto);
        UserFridge saved = userFridgeRepository.save(fridge);
        return userFridgeMapper.toResponseDto(saved);
    }
    //chức năng xóa
    public void deleteFridgeItem(Integer id) {
        userFridgeRepository.deleteById(id);
    }
    //chức năng sửa
    public UserFridgeResponseDto updateFridgeItem(Integer id, UserFridgeRequestDto requestDto) {
        // 1. Tìm xem nguyên liệu cần sửa có tồn tại trong DB không
        UserFridge existingFridge = userFridgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu trong tủ với ID: " + id));
        // 2. Cập nhật các trường thông tin mới từ request
        if (requestDto.getIngredientName() != null) {
            existingFridge.setIngredientName(requestDto.getIngredientName());
        }
        if (requestDto.getPurchaseDate() != null) {
            existingFridge.setPurchaseDate(requestDto.getPurchaseDate());
        }
        if (requestDto.getExpiryDate() != null) {
            existingFridge.setExpiryDate(requestDto.getExpiryDate());
        }
        if (requestDto.getNotifiedFlag() != null) {
            existingFridge.setNotifiedFlag(requestDto.getNotifiedFlag());
        }
        // 3. Lưu lại vào Database
        UserFridge updated = userFridgeRepository.save(existingFridge);
        // 4. Trả về DTO kết quả
        return userFridgeMapper.toResponseDto(updated);
    }
}