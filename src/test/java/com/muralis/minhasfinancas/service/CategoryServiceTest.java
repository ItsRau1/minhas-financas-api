package com.muralis.minhasfinancas.service;

import com.muralis.minhasfinancas.MinhasFinancasApplication;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.model.repository.CategoryRepository;
import com.muralis.minhasfinancas.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MinhasFinancasApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class CategoryServiceTest {

    @SpyBean
    CategoryServiceImpl service;

    @MockBean
    CategoryRepository repository;

    @Test
    @DisplayName("Deve ser possível registrar uma nova categoria.")
    void shouldBeAbleRegisterCategory () {
        Mockito.doNothing().when(service).valid(Mockito.any());
        Category category = Category.builder().description("Descrição Válida").build();
        Mockito.when(repository.save(Mockito.any(Category.class))).thenReturn(category);

        Category savedCategory = service.register(new Category());

        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isEqualTo(category.getId());
        assertThat(savedCategory.getDescription()).isEqualTo(category.getDescription());
    }

    @Test
    @DisplayName("Não deve ser possível registrar uma nova categoria com uma descrição já cadastrada.")
    void shouldBeAbleNotRegisterCategoryWithDescriptionAlreadyExist () {
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            Category category = Category.builder().description("Descrição Repetida").build();
            Mockito.doThrow(RuntimeException.class).when(service).valid(category);

            service.register(category);

            Mockito.verify(repository, Mockito.never()).save(category);
        });
    }

    @Test
    @DisplayName("Não deve ser possível registrar uma nova categoria com uma descrição maior que 255 caracteres.")
    void shouldBeAbleNotRegisterCategoryWithDescriptionBigger255Characters () {
        String descriptionBigger255Character = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam consectetur ante sed dignissim elementum. Maecenas ullamcorper mattis neque, eget egestas lacus. Nunc ac tellus erat. Sed sed enim eget ante sagittis egestas quis quis massa. Nam bibendum mi.";

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            Category category = Category.builder()
                                    .description(descriptionBigger255Character)
                                    .build();

            service.register(category);

            Mockito.verify(repository, Mockito.never()).save(category);
        });
    }

    @Test
    @DisplayName("Não deve ser possível registrar uma nova categoria com a descrição vazia.")
    void shouldBeAbleNotRegisterCategoryWithDescriptionEmpty () {
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            Category category = Category.builder()
                    .description("")
                    .build();

            service.register(category);

            Mockito.verify(repository, Mockito.never()).save(category);
        });
    }

}
