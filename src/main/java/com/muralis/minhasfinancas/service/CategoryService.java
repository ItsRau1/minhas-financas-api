package com.muralis.minhasfinancas.service;

import com.muralis.minhasfinancas.model.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> list();

    Category register(Category category);

    void valid(Category category);

    Optional<Category> getById (Long id);

    Optional<Category> getByDecription (String description);

}
