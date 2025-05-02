package com.arambyeol.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyMenuDto {
    private Long menuId;
    private String menuName;
    private String course;
    private String imgPath;
    private double averageScore;
    private long reviewCount;
} 