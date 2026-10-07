package org.example.controller.admin;

import java.util.List;
import org.example.dto.response.FeedbackResponse;
import org.example.mapper.FeedbackMapper;
import org.example.repository.FeedbackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/feedbacks")
public class FeedbackAdminController {
    private final FeedbackRepository feedbacks;

    public FeedbackAdminController(FeedbackRepository feedbacks) {
        this.feedbacks = feedbacks;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<FeedbackResponse> list(@RequestParam(required = false) String status) {
        if (status != null && !List.of("pending", "approved").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid feedback status");
        }
        var items = status == null
                ? feedbacks.findAllByOrderByCreatedAtDesc()
                : feedbacks.findByModerationStatusOrderByCreatedAtDesc(status);
        return items.stream().map(FeedbackMapper::toResponse).toList();
    }

    @PatchMapping("/{id}/approve")
    @Transactional
    public FeedbackResponse approve(@PathVariable Integer id) {
        var feedback = find(id);
        feedback.setModerationStatus("approved");
        return FeedbackMapper.toResponse(feedback);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        feedbacks.delete(find(id));
    }

    private org.example.entity.common.Feedback find(Integer id) {
        return feedbacks.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback not found"));
    }
}
