package org.example.controller.admin;

import org.example.dto.response.UploadResponse;
import org.example.repository.UserRepository;
import org.example.service.upload.FileUpLoadService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/users")
public class UserAvatarAdminController {
    private final UserRepository users;
    private final FileUpLoadService files;
    public UserAvatarAdminController(UserRepository users, FileUpLoadService files) { this.users = users; this.files = files; }

    @PostMapping("/{id}/avatar")
    @Transactional
    public UploadResponse upload(Authentication auth, @PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        var user = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!user.getEmail().equalsIgnoreCase(auth.getName()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own avatar");
        String url = files.saveImage(file, "avatar");
        user.setAvatarUrl(url);
        return new UploadResponse(url);
    }
}
