package org.example.controller.user;
import org.example.dto.response.UserResponse;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@RestController
@RequestMapping("/api/account")
public class UserController {
    private final UserRepository users;
    public UserController(UserRepository users) { this.users=users; }
    @GetMapping("/me") @Transactional(readOnly=true)
    public UserResponse me(Authentication auth) {
        if(auth==null || !auth.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required");
        return users.findByEmail(auth.getName()).map(UserMapper::toResponse).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
    }
}
