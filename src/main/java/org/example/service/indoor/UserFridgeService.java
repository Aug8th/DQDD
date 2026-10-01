package org.example.service.indoor;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeListResponseDto;
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.entity.indoor.UserFridge;
import org.example.mapper.indoor.UserFridgeMapper;
import org.example.repository.indoor.UserFridgeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFridgeService {

    private final UserFridgeRepository userFridgeRepository;
    private final UserFridgeMapper userFridgeMapper;

    // 0. Overload: Gọi chỉ với userId
    public UserFridgeListResponseDto getItemsByUser(Integer userId) {
        return getItemsByUser(userId, null);
    }

    // 1. Hiển thị danh sách nguyên liệu (Có hỗ trợ lọc theo storageType, tự động quét và xóa đồ quá hạn)
    public UserFridgeListResponseDto getItemsByUser(Integer userId, UserFridge.StorageType storageType) {
        List<UserFridge> fridges;
        if (storageType != null) {
            fridges = userFridgeRepository.findByUserIdAndStorageType(userId, storageType);
        } else {
            fridges = userFridgeRepository.findByUserId(userId);
        }

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

    // 2. Thêm nguyên liệu (Validate hạn sử dụng > ngày mua, mặc định COOLER nếu thiếu)
    public UserFridgeResponseDto addFridgeItem(UserFridgeRequestDto requestDto) {
        LocalDate purchaseDate = requestDto.getPurchaseDate();
        LocalDate expiryDate = requestDto.getExpiryDate();

        if (expiryDate != null && purchaseDate != null) {
            if (!expiryDate.isAfter(purchaseDate)) {
                throw new RuntimeException("Hạn sử dụng phải lớn hơn ngày mua nhé!");
            }
        }

        UserFridge fridge = userFridgeMapper.toEntity(requestDto);
        if (fridge.getStorageType() == null) {
            fridge.setStorageType(UserFridge.StorageType.COOLER);
        }

        UserFridge saved = userFridgeRepository.save(fridge);
        return userFridgeMapper.toResponseDto(saved);
    }

    // 3. Xóa nguyên liệu
    public void deleteFridgeItem(Integer id) {
        UserFridge existingFridge = userFridgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu nhé!"));
        userFridgeRepository.delete(existingFridge);
    }

    // 5. Sửa nguyên liệu (Tự động xóa nếu quantity cập nhật về <= 0)
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

        // Cập nhật số lượng và kiểm tra nếu dùng hết sạch (<= 0)
        if (requestDto.getQuantity() != null) {
            if (requestDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                userFridgeRepository.delete(existingFridge);
                throw new RuntimeException("Số lượng đã hết (<= 0), nguyên liệu đã được tự động xóa khỏi tủ lạnh!");
            }
            existingFridge.setQuantity(requestDto.getQuantity());
        }

        if (requestDto.getUnit() != null) {
            existingFridge.setUnit(requestDto.getUnit());
        }
        if (requestDto.getPurchaseDate() != null) {
            existingFridge.setPurchaseDate(requestDto.getPurchaseDate());
        }
        if (requestDto.getExpiryDate() != null) {
            existingFridge.setExpiryDate(requestDto.getExpiryDate());
        }
        if (requestDto.getStorageType() != null) {
            existingFridge.setStorageType(requestDto.getStorageType());
        }
        if (requestDto.getCategoryTag() != null) {
            existingFridge.setCategoryTag(requestDto.getCategoryTag());
        }

        UserFridge updated = userFridgeRepository.save(existingFridge);
        return userFridgeMapper.toResponseDto(updated);
    }
}