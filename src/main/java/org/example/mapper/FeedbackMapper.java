package org.example.mapper;

import org.example.dto.response.FeedbackResponse;
import org.example.dto.response.FeedbackSubmitResponse;
import org.example.dto.response.PublicFeedbackResponse;
import org.example.entity.common.Feedback;

@lombok.experimental.UtilityClass
public class FeedbackMapper {


    public static FeedbackResponse toResponse(Feedback fb) {
        return new FeedbackResponse(
                fb.getId(),
                fb.getUser() == null ? null : fb.getUser().getId(),
                fb.getRating(),
                fb.getComment(),
                fb.getModerationStatus(),
                fb.getCreatedAt()
        );
    }

    public static FeedbackSubmitResponse toSubmitResponse(Feedback fb) {
        return new FeedbackSubmitResponse(
                fb.getId(),
                fb.getRating(),
                fb.getComment()
        );
    }

    public static PublicFeedbackResponse toPublicResponse(Feedback fb) {
        return new PublicFeedbackResponse(
                fb.getId(),
                fb.getRating(),
                fb.getComment(),
                fb.getCreatedAt()
        );
    }
}
