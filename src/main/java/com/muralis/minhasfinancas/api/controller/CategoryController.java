package com.muralis.minhasfinancas.api.controller;

import com.muralis.minhasfinancas.api.dto.CategoryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseCategoryDTO;
import com.muralis.minhasfinancas.exception.*;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.service.CategoryService;
import com.muralis.minhasfinancas.utils.CategoryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;
    private final CategoryUtils utils;

    @GetMapping
    public ResponseEntity<?> list() {
        List<Category> categories = service.list();
        List<ResponseCategoryDTO> response = categories.stream()
                .map(utils::convertCategoryToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody CategoryDTO dto) {
        try {
            Category entity = utils.convertDtoToCategory(dto);
            entity = service.register(entity);
            ResponseCategoryDTO response = utils.convertCategoryToResponseDTO(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
