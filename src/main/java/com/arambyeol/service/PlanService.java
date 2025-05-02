package com.arambyeol.service;

import com.arambyeol.domain.Plan;
import com.arambyeol.dto.DailyMenuDto;
import com.arambyeol.dto.PlanResponseDto;
import com.arambyeol.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<PlanResponseDto> getFuturePlans() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<Plan> plans = planRepository.findAllFuturePlansWithMenuAndReviews(today);

        // 날짜별로 그룹화
        Map<String, List<Plan>> plansByDate = plans.stream()
                .collect(Collectors.groupingBy(Plan::getDate));

        // 각 날짜별 식단 정보 생성
        return plansByDate.entrySet().stream()
                .map(entry -> createPlanResponseDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private PlanResponseDto createPlanResponseDto(String date, List<Plan> plans) {
        List<DailyMenuDto> dailyMenus = plans.stream()
                .map(this::createDailyMenuDto)
                .collect(Collectors.toList());

        return PlanResponseDto.builder()
                .date(date)
                .menus(dailyMenus)
                .build();
    }

    private DailyMenuDto createDailyMenuDto(Plan plan) {
        double averageScore = plan.getMenu().getReviews().stream()
                .mapToInt(review -> review.getScore())
                .average()
                .orElse(0.0);

        return DailyMenuDto.builder()
                .menuId(plan.getMenu().getMenuId())
                .menuName(plan.getMenu().getMenu())
                .course(plan.getCourse())
                .imgPath(plan.getMenu().getImgPath())
                .averageScore(averageScore)
                .reviewCount(plan.getMenu().getReviews().size())
                .build();
    }

    @Transactional(readOnly = true)
    public PlanResponseDto getDailyPlan(String date) {
        validateDateFormat(date);
        List<Plan> plans = planRepository.findByDateWithMenuAndReviews(date);
        
        if (plans.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜의 식단이 없습니다: " + date);
        }

        return createPlanResponseDto(date, plans);
    }

    private void validateDateFormat(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. 형식: yyyy-MM-dd");
        }
    }
} 