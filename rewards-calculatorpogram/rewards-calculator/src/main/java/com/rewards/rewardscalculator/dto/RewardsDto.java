package com.rewards.rewardscalculator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RewardsDto {
    private Long customerId;
    private Map<String, Long> monthlyRewards;
    private Long totalRewards;
}
