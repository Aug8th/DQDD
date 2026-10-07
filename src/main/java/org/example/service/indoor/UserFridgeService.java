package org.example.service.indoor;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeListResponseDto;
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.entity.indoor.UserFridge;
import org.example.mapper.UserFridgeMapper;
import org.example.repository.UserFridgeRepository;
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

    // Hiển thị nguyên liệu và cảnh báo; chỉ xóa khi người dùng yêu cầu.
    public UserFridgeListResponseDto getItemsByUser(Integer userId, UserFridge.StorageType storageType) {
        List<UserFridge> fridges;
        if (storageType != null) {
            fridges = userFridgeRepository.findByUserIdAndStorageType(userId, storageType);
        } else {
            fridges = userFridgeRepository.findByUserId(userId);
        }

        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));

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

        if (!expiredItems.isEmpty()) {
            notificationMessage = "⚠️ Có " + expiredItems.size()
                    + " nguyên liệu đã hết hạn. Hãy kiểm tra và xóa khi đã bỏ thực phẩm.";
        }
        activeItems.addAll(expiredItems);

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
        if (requestDto.getPurchaseDate() == null || requestDto.getIngredientName() == null
                || requestDto.getIngredientName().isBlank() || requestDto.getIngredientName().length() > 150
                || (requestDto.getQuantity() != null && requestDto.getQuantity().signum() <= 0)
                || (requestDto.getUnit() != null && requestDto.getUnit().length() > 50)
                || (requestDto.getCategoryTag() != null && requestDto.getCategoryTag().length() > 100)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Dữ liệu nguyên liệu không hợp lệ");
        }
        LocalDate purchaseDate = requestDto.getPurchaseDate();
        LocalDate expiryDate = requestDto.getExpiryDate();

        if (expiryDate != null && purchaseDate != null) {
            if (!expiryDate.isAfter(purchaseDate)) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Hạn sử dụng phải lớn hơn ngày mua nhé!");
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
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy nguyên liệu nhé!"));
        userFridgeRepository.delete(existingFridge);
    }

    // 5. Sửa nguyên liệu (Tự động xóa nếu quantity cập nhật về <= 0)
    public UserFridgeResponseDto updateFridgeItem(Integer id, UserFridgeRequestDto requestDto) {
        UserFridge existingFridge = userFridgeRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy nguyên liệu nhé!"));

        if ((requestDto.getIngredientName() != null && (requestDto.getIngredientName().isBlank() || requestDto.getIngredientName().length() > 150))
                || (requestDto.getUnit() != null && requestDto.getUnit().length() > 50)
                || (requestDto.getCategoryTag() != null && requestDto.getCategoryTag().length() > 100)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Dữ liệu nguyên liệu không hợp lệ");
        }

        LocalDate purchaseDate = requestDto.getPurchaseDate() != null ? requestDto.getPurchaseDate() : existingFridge.getPurchaseDate();
        LocalDate expiryDate = requestDto.getExpiryDate() != null ? requestDto.getExpiryDate() : existingFridge.getExpiryDate();

        if (expiryDate != null && purchaseDate != null) {
            if (!expiryDate.isAfter(purchaseDate)) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Hạn sử dụng phải lớn hơn ngày mua nhé!");
            }
        }

        if (requestDto.getIngredientName() != null) {
            existingFridge.setIngredientName(requestDto.getIngredientName());
        }

        // Cập nhật số lượng và kiểm tra nếu dùng hết sạch (<= 0)
        if (requestDto.getQuantity() != null) {
            if (requestDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0. Dùng API DELETE khi đã dùng hết.");
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
            if (!requestDto.getExpiryDate().equals(existingFridge.getExpiryDate())) {
                existingFridge.setNotifiedFlag(false);
            }
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