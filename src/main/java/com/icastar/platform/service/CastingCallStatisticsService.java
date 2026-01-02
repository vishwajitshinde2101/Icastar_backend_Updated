package com.icastar.platform.service;

import com.icastar.platform.entity.CastingCall;
import com.icastar.platform.entity.CastingCallApplication;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.CastingCallApplicationRepository;
import com.icastar.platform.repository.CastingCallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CastingCallStatisticsService {

    private final CastingCallRepository castingCallRepository;
    private final CastingCallApplicationRepository applicationRepository;

    // 13. Get statistics
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics(User recruiter) {
        if (!User.UserRole.RECRUITER.equals(recruiter.getRole())) {
            throw new RuntimeException("Access forbidden: Recruiter role required");
        }

        log.info("Fetching casting call statistics for recruiter: {}", recruiter.getEmail());

        // Get all casting call IDs for this recruiter
        List<CastingCall> allCastingCalls = castingCallRepository.findByRecruiter(recruiter);
        List<Long> castingCallIds = allCastingCalls.stream()
            .map(CastingCall::getId)
            .collect(Collectors.toList());

        // Overall counts
        Long totalCastingCalls = castingCallRepository.countActiveByRecruiterId(recruiter.getId());
        Long openCastingCalls = castingCallRepository.countByRecruiterIdAndStatus(
            recruiter.getId(), CastingCall.CastingCallStatus.OPEN
        );
        Long closedCastingCalls = castingCallRepository.countByRecruiterIdAndStatus(
            recruiter.getId(), CastingCall.CastingCallStatus.CLOSED
        );
        Long draftCastingCalls = castingCallRepository.countByRecruiterIdAndStatus(
            recruiter.getId(), CastingCall.CastingCallStatus.DRAFT
        );

        // Application statistics
        Long totalApplications = castingCallIds.isEmpty() ? 0L :
            applicationRepository.countByCastingCallIdIn(castingCallIds);

        Long applicationsApplied = castingCallIds.isEmpty() ? 0L :
            applicationRepository.countByCastingCallIdInAndStatus(
                castingCallIds, CastingCallApplication.ApplicationStatus.APPLIED
            );

        Long applicationsUnderReview = castingCallIds.isEmpty() ? 0L :
            applicationRepository.countByCastingCallIdInAndStatus(
                castingCallIds, CastingCallApplication.ApplicationStatus.UNDER_REVIEW
            );

        Long applicationsShortlisted = castingCallIds.isEmpty() ? 0L :
            applicationRepository.countShortlistedByCastingCallIdIn(castingCallIds);

        Long applicationsSelected = castingCallIds.isEmpty() ? 0L :
            applicationRepository.countSelectedByCastingCallIdIn(castingCallIds);

        Long applicationsRejected = castingCallIds.isEmpty() ? 0L :
            applicationRepository.countByCastingCallIdInAndStatus(
                castingCallIds, CastingCallApplication.ApplicationStatus.REJECTED
            );

        // Calculate rates
        Double selectionRate = totalApplications > 0 ?
            (applicationsSelected.doubleValue() / totalApplications.doubleValue()) * 100 : 0.0;

        Double averageApplicationsPerCastingCall = totalCastingCalls > 0 ?
            totalApplications.doubleValue() / totalCastingCalls.doubleValue() : 0.0;

        // Recent activity (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Long recentApplications = castingCallIds.isEmpty() ? 0L :
            applicationRepository.countByCastingCallIdInAndAppliedAtBetween(
                castingCallIds, thirtyDaysAgo, LocalDateTime.now()
            );

        // Build response
        Map<String, Object> statistics = new HashMap<>();

        // Casting call counts
        statistics.put("totalCastingCalls", totalCastingCalls);
        statistics.put("openCastingCalls", openCastingCalls);
        statistics.put("closedCastingCalls", closedCastingCalls);
        statistics.put("draftCastingCalls", draftCastingCalls);

        // Application counts
        statistics.put("totalApplications", totalApplications);
        statistics.put("applicationsApplied", applicationsApplied);
        statistics.put("applicationsUnderReview", applicationsUnderReview);
        statistics.put("applicationsShortlisted", applicationsShortlisted);
        statistics.put("applicationsSelected", applicationsSelected);
        statistics.put("applicationsRejected", applicationsRejected);

        // Metrics
        statistics.put("selectionRate", Math.round(selectionRate * 100.0) / 100.0);
        statistics.put("averageApplicationsPerCastingCall",
                      Math.round(averageApplicationsPerCastingCall * 100.0) / 100.0);
        statistics.put("recentApplications30Days", recentApplications);

        // Status breakdown
        Map<String, Long> statusBreakdown = new HashMap<>();
        statusBreakdown.put("APPLIED", applicationsApplied);
        statusBreakdown.put("UNDER_REVIEW", applicationsUnderReview);
        statusBreakdown.put("SHORTLISTED", applicationsShortlisted);
        statusBreakdown.put("SELECTED", applicationsSelected);
        statusBreakdown.put("REJECTED", applicationsRejected);
        statistics.put("statusBreakdown", statusBreakdown);

        log.info("Generated statistics for recruiter: {} - {} total casting calls, {} total applications",
                 recruiter.getEmail(), totalCastingCalls, totalApplications);

        return statistics;
    }
}
