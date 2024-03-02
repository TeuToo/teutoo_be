package com.project.durumoongsil.teutoo.estimate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateEstimateDto {
    @NotNull
    @Min(0)
    @Schema(description = "PT 가격")
    private Long price;
    @NotNull
    @Min(0)
    @Schema(description = "PT 횟수")
    private Integer ptCount;
    @NotEmpty
    @Schema(description = "PT 주소")
    private String ptAddress;
}
