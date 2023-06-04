package com.SocialMediaAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
@Builder
public class ActivityFeedDto {

    @NotNull
    private List<PostDto> feed;

    @NotNull
    private int page;
}
