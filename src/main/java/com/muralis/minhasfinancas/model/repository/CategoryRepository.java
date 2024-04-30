package com.muralis.minhasfinancas.model.repository;

import com.muralis.minhasfinancas.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>{

    boolean existsByDescription(String description);

    Optional<Category> getCategoriesByDescription(String description);

}
