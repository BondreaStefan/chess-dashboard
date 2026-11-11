package com.bond.chess_dashboard.dashboard;

import java.time.Duration;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bond.chess_dashboard.student.StudentService;
import com.bond.chess_dashboard.game.GameStatsService;
import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.dashboard.dto.DashboardResponse;
import com.bond.chess_dashboard.dashboard.dto.DashboardSummary;
import com.bond.chess_dashboard.dashboard.dto.RatingOverview;
import com.bond.chess_dashboard.dashboard.dto.StudentOverview;
import com.bond.chess_dashboard.student.dto.StudentResponse;
import com.bond.chess_dashboard.game.dto.PlatformBreakdown;
import com.bond.chess_dashboard.game.dto.StudentActivity;
import com.bond.chess_dashboard.game.Platform;
import com.bond.chess_dashboard.game.TimeClass;
import com.bond.chess_dashboard.game.dto.RatingPoint;
import com.bond.chess_dashboard.dashboard.dto.MostActiveStudent;

@Service
public class DashboardService {

    private static final Duration RECENT_WINDOW = Duration.ofDays(14);
    private static final Duration WEEK_WINDOW = Duration.ofDays(7);
    private static final Duration HISTORY_WINDOW = Duration.ofDays(90);

    private final StudentService studentService;
    private final GameStatsService gameStatsService;
    private final Clock clock;

    public DashboardService(StudentService studentService, GameStatsService gameStatsService, Clock clock) {
        this.studentService = studentService;
        this.gameStatsService = gameStatsService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(AuthenticatedCoach coach) {
        Instant now = clock.instant();
        Instant since7 = now.minus(WEEK_WINDOW);
        Instant since14 = now.minus(RECENT_WINDOW);
        Instant since90 = now.minus(HISTORY_WINDOW);

        List<StudentResponse> students = studentService.getStudents(coach);
        List<Long> studentIds = students.stream().map(StudentResponse::id).toList();

        Map<Long, StudentActivity> activityByStudentId = gameStatsService
                .getActivity(studentIds, since14, since7)
                .stream()
                .collect(Collectors.toMap(StudentActivity::studentId, Function.identity()));

        Map<Long, List<PlatformBreakdown>> breakdownByStudentId = gameStatsService
                .getPlatformBreakdown(studentIds, since14)
                .stream()
                .collect(Collectors.groupingBy(PlatformBreakdown::studentId));

        List<StudentOverview> overviews = students.stream()
                .map(student -> toOverview(
                        student,
                        activityByStudentId.get(student.id()),
                        breakdownByStudentId.getOrDefault(student.id(), List.of()),
                        since14,
                        since90))
                .toList();

        return new DashboardResponse(summarize(overviews, activityByStudentId), overviews);
    }

    private StudentOverview toOverview(StudentResponse student,
            StudentActivity activity,
            List<PlatformBreakdown> breakdown,
            Instant since14,
            Instant since90) {

        if (activity == null) {
            activity = new StudentActivity(student.id(), 0, 0, null);
        }

        ActivityLevel activityLevel = DashboardRules.activityLevel(activity.gamesLast14Days());

        RatingOverview ratingOverview = buildRating(student.id(), breakdown, since14, since90);

        return new StudentOverview(student.id(),
                student.firstName(),
                student.lastName(),
                activity.gamesLast14Days(),
                activityLevel,
                activity.lastPlayedAt(),
                ratingOverview);
    }

    private RatingOverview buildRating(Long studentId,
            List<PlatformBreakdown> breakdown,
            Instant since14,
            Instant since90) {

        Optional<PlatformBreakdown> optPlatformBreakdown = DashboardRules.chooseCombination(breakdown);
        if (optPlatformBreakdown.isEmpty()) {
            return null;
        }
        PlatformBreakdown platformBreakdown = optPlatformBreakdown.get();
        Platform platform = platformBreakdown.platform();
        TimeClass timeClass = platformBreakdown.timeClass();

        Instant historyStart = platformBreakdown.lastPlayedAt() != null
                ? platformBreakdown.lastPlayedAt().minus(HISTORY_WINDOW)
                : since90;

        List<RatingPoint> history = gameStatsService.getRatingHistory(studentId, platform, timeClass, historyStart);
        if (history.isEmpty()) {
            return null;
        }
        Integer change = DashboardRules.ratingChange(history, since14);
        int current = history.getLast().rating();
        Trend trend = DashboardRules.trend(change);

        return new RatingOverview(platform, timeClass, current, change, trend, history);
    }

    private DashboardSummary summarize(List<StudentOverview> overviews,
            Map<Long, StudentActivity> activityByStudentId) {

        int totalStudents = overviews.size();

        long activeThisWeek = activityByStudentId.values().stream()
                .filter(a -> a.gamesLast7Days() > 0)
                .count();

        MostActiveStudent mostActive = overviews.stream()
                .filter(o -> o.gamesLast14Days() > 0)
                .max(Comparator.comparingLong(StudentOverview::gamesLast14Days))
                .map(o -> new MostActiveStudent(
                        o.id(),
                        o.firstName() + " " + o.lastName(),
                        o.gamesLast14Days()))
                .orElse(null);
        return new DashboardSummary(totalStudents, activeThisWeek, mostActive);
    }
}
