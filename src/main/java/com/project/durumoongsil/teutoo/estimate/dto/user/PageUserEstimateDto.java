package com.project.durumoongsil.teutoo.estimate.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageUserEstimateDto {
    private Long estimateId;
    private Integer price;
    private String name;
    private String profileImagePath;
}
