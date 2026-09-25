package org.example.service.indoor;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeListResponseDto;
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.entity.indoor.UserFridge;
import org.example.mapper.indoor.UserFridgeMapper;
import org.example.repository.indoor.UserFridgeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFridgeService {

    private final UserFridgeRepository userFridgeRepository;
    private final UserFridgeMapper userFridgeMapper;

    // 1. Hiển thị danh sách nguyên liệu (Tự động quét, xóa đồ quá hạn và tạo thông báo)
    public UserFridgeListResponseDto getItemsByUser(Integer userId) {
        List<UserFridge> fridges = userFridgeRepository.findByUserId(userId);
        LocalDate today = LocalDate.now();

        List<UserFridge> expiredItems = new ArrayList<>();
        List<UserFridge> activeItems = new ArrayList<>();

        // Phân loại nguyên liệu còn hạn và quá hạn
        for (UserFridge item : fridges) {
            if (item.getExpiryDate() != null && item.getExpiryDate().isBefore(today)) {
                expiredItems.add(item); // Đã quá hạn
            } else {
                activeItems.add(item);  // Còn hạn dùng
            }
        }

        String notificationMessage = null;

        // Nếu phát hiện có nguyên liệu quá hạn -> Tự động xóa khỏi DB và tạo thông báo
        if (!expiredItems.isEmpty()) {
            userFridgeRepository.deleteAll(expiredItems);

            List<String> expiredNames = expiredItems.stream()
                    .map(UserFridge::getIngredientName)
                    .collect(Collectors.toList());

            notificationMessage = "⚠️ Tủ lạnh có " + expiredItems.size() +
                    " nguyên liệu đã quá hạn và bị tự động xóa: " +
                    String.join(", ", expiredNames);
        }

        // Chuyển danh sách còn hạn sang DTO
        List<UserFridgeResponseDto> itemDtos = activeItems.stream()
                .map(userFridgeMapper::toResponseDto)
                .collect(Collectors.toList());

        // Đóng gói kết quả trả về gồm danh sách sạch và thông báo
        UserFridgeListResponseDto response = new UserFridgeListResponseDto();
        response.setItems(itemDtos);
        response.setNotification(notificationMessage);

        return response;
    }

    // 2. Thêm nguyên liệu (Đã có sẵn validate hạn sử dụng > ngày mua)
    public UserFridgeResponseDto addFridgeItem(UserFridgeRequestDto requestDto) {
        LocalDate purchaseDate = requestDto.getPurchaseDate();
        LocalDate expiryDate = requestDto.getExpiryDate();

        if (expiryDate != null && purchaseDate != null) {
            if (!expiryDate.isAfter(purchaseDate)) {
                throw new RuntimeException("Hạn sử dụng phải lớn hơn ngày mua nhé!");
            }
        }

        UserFridge fridge = userFridgeMapper.toEntity(requestDto);
        UserFridge saved = userFridgeRepository.save(fridge);
        return userFridgeMapper.toResponseDto(saved);
    }

    // 3. Xóa nguyên liệu
    public void deleteFridgeItem(Integer id) {
        UserFridge existingFridge = userFridgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu nhé!"));
        userFridgeRepository.delete(existingFridge);
    }

    // 4. Sửa nguyên liệu
    public UserFridgeResponseDto updateFridgeItem(Integer id, UserFridgeRequestDto requestDto) {
        UserFridge existingFridge = userFridgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu nhé!"));

        LocalDate purchaseDate = requestDto.getPurchaseDate() != null ? requestDto.getPurchaseDate() : existingFridge.getPurchaseDate();
        LocalDate expiryDate = requestDto.getExpiryDate() != null ? requestDto.getExpiryDate() : existingFridge.getExpiryDate();

        if (expiryDate != null && purchaseDate != null) {
            if (!expiryDate.isAfter(purchaseDate)) {
                throw new RuntimeException("Hạn sử dụng phải lớn hơn ngày mua nhé!");
            }
        }

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

        UserFridge updated = userFridgeRepository.save(existingFridge);
        return userFridgeMapper.toResponseDto(updated);
    }
}