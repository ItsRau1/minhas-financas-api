package com.muralis.minhasfinancas.utils;

import com.muralis.minhasfinancas.MinhasFinancasApplication;
import com.muralis.minhasfinancas.api.dto.CategoryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseCategoryDTO;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.utils.impl.CategoryUtilsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = MinhasFinancasApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class CategoryUtilsTest {

    @SpyBean
    CategoryUtilsImpl utils;

    @Test
    @DisplayName("Deve ser possível converter um categoria DTO para uma entidade categoria.")
    void shouldBeAbleToConvertDtoToCategory () {
        CategoryDTO categoryDTO = CategoryDTO
                .builder()
                .id(1L)
                .descricao("description")
                .ativo(Boolean.TRUE)
                .build();

        CategoryDTO wrongCategoryDTO = CategoryDTO.builder().build();

        Category category = utils.convertDtoToCategory(categoryDTO);

        Assertions.assertThat(category.getDescription().equals(categoryDTO.getDescricao()));
        catchThrowableOfType( () -> utils.convertDtoToCategory(wrongCategoryDTO), BusinessRuleException.class);
    }

    @Test
    @DisplayName("Deve ser possível converter uma entidade categoria para um categoria DTO.")
    void shouldBeAbleToConvertCategoryToDTO () {
        Category category = Category
                .builder()
                .id(1L)
                .description("description")
                .active(Boolean.TRUE)
                .build();
        Category wrongCategory = Category.builder().build();

        CategoryDTO categoryDTO = utils.convertCategoryToDto(category);
        Throwable error = catchThrowable(() -> utils.convertCategoryToDto(wrongCategory));


        Assertions.assertThat(categoryDTO.getId().equals(category.getId()));
        Assertions.assertThat(categoryDTO.getDescricao().equals(category.getDescription()));

        assertThat(error).isNull();
    }

    @Test
    @DisplayName("Deve ser possível converter uma entidade categoria para uma resposta de requisição.")
    void shouldBeAbleToConvertCategoryToResponseDTO () {
        Category category = Category
                .builder()
                .id(1L)
                .description("description")
                .active(Boolean.TRUE)
                .build();
        Category wrongCategory = Category.builder().build();

        ResponseCategoryDTO responseCategoryDTO = utils.convertCategoryToResponseDTO(category);
        Throwable error = catchThrowable(() -> utils.convertCategoryToResponseDTO(wrongCategory));

        Assertions.assertThat(responseCategoryDTO.getId().equals(category.getId()));
        Assertions.assertThat(responseCategoryDTO.getDescricao().equals(category.getDescription()));

        assertThat(error).isInstanceOf(NullPointerException.class);
    }

}