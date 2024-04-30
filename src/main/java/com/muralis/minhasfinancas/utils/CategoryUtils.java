package com.muralis.minhasfinancas.utils;

import com.muralis.minhasfinancas.api.dto.CategoryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseCategoryDTO;
import com.muralis.minhasfinancas.model.entity.Category;

public interface CategoryUtils {

    Category convertDtoToCategory (CategoryDTO dto);

    CategoryDTO convertCategoryToDto (Category category);

    ResponseCategoryDTO convertCategoryToResponseDTO (Category category);

}
