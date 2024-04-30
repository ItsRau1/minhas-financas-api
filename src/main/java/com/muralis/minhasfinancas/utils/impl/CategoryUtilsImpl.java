package com.muralis.minhasfinancas.utils.impl;

import com.muralis.minhasfinancas.api.dto.CategoryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseCategoryDTO;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.utils.CategoryUtils;
import org.springframework.stereotype.Service;

@Service
public class CategoryUtilsImpl implements CategoryUtils {

    @Override
    public Category convertDtoToCategory(CategoryDTO dto) {
        if (dto.getDescricao() == null) {
            throw new BusinessRuleException("Informe uma descrição para a categoria.");
        }
        return Category.builder()
                .description(dto.getDescricao())
                .build();
    }

    @Override
    public CategoryDTO convertCategoryToDto(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .descricao(category.getDescription())
                .ativo(category.getActive())
                .build();
    }

    @Override
    public ResponseCategoryDTO convertCategoryToResponseDTO(Category category) {
        return ResponseCategoryDTO.builder()
                .id(category.getId())
                .descricao(category.getDescription())
                .ativo(category.getActive())
                .build();
    }

}
