package backend.budget.budget.controller;

import backend.budget.budget.dto.CategoryResponse;
import backend.budget.budget.service.CategoryService;
import backend.budget.common.constants.SuccessCode;
import backend.budget.common.dto.ApiSuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public ApiSuccessResponse<List<CategoryResponse>> category(){
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_CATEGORY_LIST, categoryService.getCategory());
    }
}
