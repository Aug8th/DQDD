package org.example.controller.admin;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.example.dto.request.UserActiveRequest;
import org.example.dto.response.FridgeStatsResponse;
import org.example.dto.response.UserResponse;
import org.example.mapper.UserMapper;
import org.example.repository.UserFridgeRepository;
import org.example.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class UserAdminController {
    private final UserRepository users;
    private final UserFridgeRepository fridges;

    public UserAdminController(UserRepository users, UserFridgeRepository fridges) {
        this.users = users;
        this.fridges = fridges;
    }

    @PatchMapping("/users/{id}/active")
    @Transactional
    public UserResponse setActive(Authentication auth, @PathVariable Integer id,
                                  @RequestBody UserActiveRequest request) {
        if (request == null || request.active() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "active is required");
        }
        var user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!request.active() && user.getEmail().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot lock your own account");
        }
        if (!request.active() && "admin".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot lock an admin account");
        }
        user.setActive(request.active());
        return UserMapper.toResponse(user);
    }

    @GetMapping("/fridge/stats")
    @Transactional(readOnly = true)
    public FridgeStatsResponse stats() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        List<FridgeStatsResponse.IngredientCount> ingredients = fridges
                .topIngredients(PageRequest.of(0, 10))
                .stream()
                .map(item -> new FridgeStatsResponse.IngredientCount(
                        item.getIngredientName(), item.getItemCount()))
                .toList();
        return new FridgeStatsResponse(
                fridges.count(),
                fridges.countByStorageType("COOLER"),
                fridges.countByStorageType("FREEZER"),
                fridges.countByExpiryDateBefore(today),
                fridges.countByExpiryDateBetween(today, today.plusDays(3)),
                ingredients
        );
    }
}
