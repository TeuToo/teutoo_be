package com.project.durumoongsil.teutoo.estimate.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PagedTrainerEstimateDto {
    private Long memberId;
    private Long estimateId;
    private Integer price;
    private String name;
    private String profileImagePath;
}
