package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.repository.UserRepository;
import org.example.repository.UserFridgeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository users;
    private final UserFridgeRepository fridges;

    public Integer id(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)).getId();
    }

    public void requireOwner(Authentication auth, Integer userId) {
        if (!id(auth).equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không được truy cập tủ lạnh của người khác");
        }
    }

    public void requireFridgeOwner(Authentication auth, Integer itemId) {
        var item = fridges.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nguyên liệu"));
        requireOwner(auth, item.getUserId());
    }
}
