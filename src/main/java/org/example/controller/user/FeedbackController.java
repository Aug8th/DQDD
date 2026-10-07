package org.example.controller.user;

import org.example.dto.request.FeedbackRequest;
import org.example.entity.common.Feedback;
import org.example.repository.FeedbackRepository;
import org.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    private final FeedbackRepository feedbacks;
    private final UserRepository users;

    public FeedbackController(FeedbackRepository feedbacks, UserRepository users) {
        this.feedbacks = feedbacks;
        this.users = users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResult submit(Authentication auth, @RequestBody FeedbackRequest body) {
        if (body == null || body.rating() == null || body.rating() < 1 || body.rating() > 5
                || (body.comment() != null && body.comment().length() > 5000)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid feedback");
        }

        var user = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (body.userId() != null && !body.userId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Feedback userId does not match the signed-in user");
        }

        var saved = feedbacks.saveAndFlush(new Feedback(user, body.rating(), body.comment()));
        return new FeedbackResult(saved.getId(), saved.getRating(), saved.getComment());
    }

    public record FeedbackResult(Integer id, Integer rating, String comment) {}
}
