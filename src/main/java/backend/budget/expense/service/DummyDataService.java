package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.UserRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import backend.budget.expense.entity.Expense;
import backend.budget.expense.repository.ExpenseRepository;
import backend.budget.budget.entity.Category;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class DummyDataService {

    private final ExpenseRepository expenseRepository;

    public DummyDataService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void generateDummyDataForUser(CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();

        List<Category> categories = List.of(
                Category.builder().name("식비").build(),
                Category.builder().name("교통비").build(),
                Category.builder().name("의류비").build()
        );

        Random random = new Random();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 30; i++) {
            Expense expense = Expense.builder()
                    .user(user)
                    .amount(10000L + random.nextInt(50000))  // 임의로 1만 ~ 5만원
                    .expenseDate(today.minusDays(i))
                    .category(categories.get(random.nextInt(categories.size())))  // 카테고리 랜덤 선택
                    .build();

            expenseRepository.save(expense);
        }
    }
}