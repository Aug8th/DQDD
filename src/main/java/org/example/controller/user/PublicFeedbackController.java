package org.example.controller.user;

import java.util.List;
import org.example.dto.response.PublicFeedbackResponse;
import org.example.mapper.FeedbackMapper;
import org.example.repository.FeedbackRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedbacks/public")
public class PublicFeedbackController {
    private final FeedbackRepository feedbacks;

    public PublicFeedbackController(FeedbackRepository feedbacks) {
        this.feedbacks = feedbacks;
    }

    @GetMapping
    public List<PublicFeedbackResponse> list() {
        return feedbacks.findByModerationStatusOrderByCreatedAtDesc("approved")
                .stream()
                .map(FeedbackMapper::toPublicResponse)
                .toList();
    }
}
