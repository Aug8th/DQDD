package org.example.controller.user;

import java.util.List;
import org.example.dto.response.NotificationResponse;
import org.example.service.notification.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationResponse> list(Authentication auth,
                                           @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return service.list(auth.getName(), unreadOnly);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(Authentication auth, @PathVariable Integer id) {
        return service.markRead(auth.getName(), id);
    }
}
