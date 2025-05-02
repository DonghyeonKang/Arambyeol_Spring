package com.arambyeol.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PlanResponseDto {
    private String date;
    private List<DailyMenuDto> menus;
} 