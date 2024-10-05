package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.UserRepository;
import backend.budget.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseStatisticsService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseStatisticsService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // 지난달 대비 소비율 비교
    public double compareLastMonth(CustomUserDetails customUserDetails, LocalDate date) {
        User user = customUserDetails.getUser();

        LocalDate startMonth = date.withDayOfMonth(1);
        LocalDate endMonth = date.withDayOfMonth(date.lengthOfMonth());
        LocalDate startLastMonth = startMonth.minusMonths(1);
        LocalDate endLastMonth = endMonth.minusMonths(1);

        // 이번 달 소비
        long currentMonthTotal = expenseRepository.getTotalExpenseByUserAndDateBetween(user.getId(), startMonth, endMonth);

        // 지난달 소비
        long lastMonthTotal = expenseRepository.getTotalExpenseByUserAndDateBetween(user.getId(), startLastMonth, endLastMonth);

        // 지난달 대비 소비율 계산
        return lastMonthTotal == 0 ? 0 : (double) currentMonthTotal / lastMonthTotal * 100;
    }

    // 지난 요일 대비 소비율
    public double compareWeek(CustomUserDetails customUserDetails, LocalDate date) {
        User user = customUserDetails.getUser();
        DayOfWeek currentDayOfWeek = date.getDayOfWeek();

        // 오늘 요일에 해당하는 날짜 범위의 소비 데이터를 가져옴
        LocalDate startOfCurrentWeek = date.minusDays(currentDayOfWeek.getValue() - 1);  // 이번 주 월요일
        long todayExpense = expenseRepository.getTotalExpenseByUserAndDateBetween(user.getId(), startOfCurrentWeek, date);

        // 지난주 같은 요일의 소비
        LocalDate startOfLastWeek = startOfCurrentWeek.minusWeeks(1);  // 지난주 월요일
        LocalDate endOfLastWeek = startOfLastWeek.plusDays(currentDayOfWeek.getValue() - 1);  // 지난주 같은 요일
        long lastWeekExpense = expenseRepository.getTotalExpenseByUserAndDateBetween(user.getId(), startOfLastWeek, endOfLastWeek);

        // 지난 주 대비 소비율 계산
        return lastWeekExpense == 0 ? 0 : (double) todayExpense / lastWeekExpense * 100;
    }

    // 다른 사용자 대비 소비율
    public double compareWithOtherUsers(CustomUserDetails customUserDetails, LocalDate date) {
        User user = customUserDetails.getUser();
        LocalDate startMonth = date.withDayOfMonth(1);
        LocalDate today = date;

        // 내 소비 비율
        long myTotalExpense = expenseRepository.getTotalExpenseByUserAndDateBetween(user.getId(), startMonth, today);
        long myBudget = 1000000L;

        // 다른 유저들의 평균 소비율 계산
        List<User> users = userRepository.findAll();
        double averageExpenseRatio = users.stream()
                .filter(u -> u.getId() != user.getId())  // 다른 유저 필터링
                .mapToLong(u -> expenseRepository.getTotalExpenseByUserAndDateBetween(u.getId(), startMonth, today))
                .average()
                .orElse(0);

        // 내 소비 비율 계산
        double myExpenseRatio = (double) myTotalExpense / myBudget * 100;

        // 다른 유저들과 비교한 소비율 계산
        return myExpenseRatio == 0 ? 0 : myExpenseRatio / averageExpenseRatio * 100;
    }
}