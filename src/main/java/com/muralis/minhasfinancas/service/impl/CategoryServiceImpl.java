package com.muralis.minhasfinancas.service.impl;


import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.model.repository.CategoryRepository;
import com.muralis.minhasfinancas.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.repository = categoryRepository;
    }

    @Override
    @Transactional
    public List<Category> list() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Category register(Category category) {
        valid(category);
        category.setActive(true);
        category.setRegistration_date(LocalDate.now());

        return repository.save(category);
    }

    @Override
    public void valid(Category category) {
        if (category.getDescription() == null || category.getDescription().trim().isEmpty()) {
            throw new BusinessRuleException("Informe uma descrição válida para categoria.");
        }
        if (repository.existsByDescription(category.getDescription())) {
            throw new BusinessRuleException("Categoria já cadastrada.");
        }
        if (category.getDescription().codePointCount(0, category.getDescription().length()) > 255) {
            throw new BusinessRuleException("Descrição ultrapassa o limite de caracteres para a categoria.");
        }
    }

    @Override
    public Optional<Category> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Category> getByDecription(String description) {
        return repository.getCategoriesByDescription(description);
    }

}
