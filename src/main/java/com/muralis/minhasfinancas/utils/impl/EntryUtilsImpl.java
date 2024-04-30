package com.muralis.minhasfinancas.utils.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muralis.minhasfinancas.api.dto.EntryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseCategoryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseUserDTO;
import com.muralis.minhasfinancas.api.dto.enums.StatusEntryDTO;
import com.muralis.minhasfinancas.api.dto.enums.TypeEntryDTO;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.model.entity.Entry;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.model.enums.StatusEntry;
import com.muralis.minhasfinancas.model.enums.TypeEntry;
import com.muralis.minhasfinancas.service.CategoryService;
import com.muralis.minhasfinancas.utils.EntryUtils;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EntryUtilsImpl implements EntryUtils {

    private final CategoryService categoryService;

    public EntryUtilsImpl(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public Entry convertDtoToEntity (EntryDTO dto) {
        return validateDTO(dto);
    }

    @Override
    public ResponseEntryDTO convertEntityToResponseDTO(Entry entry) {
        ResponseUserDTO convertedUser = convertUserToResponseDTO(entry.getUser());

        Collection<ResponseCategoryDTO> convertedCategory = convertCategoryToResponseDTO(entry.getCategory());

        TypeEntryDTO typeLaunch;
        StatusEntryDTO statusLaunch;
        String registrationDate = entry.getRegistrationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (entry.getType().equals(TypeEntry.RECIPE)) {
            typeLaunch = TypeEntryDTO.RECEITA;
        } else {
            typeLaunch = TypeEntryDTO.DESPESA;
        }

        if (entry.getStatus().equals(StatusEntry.CANCELED)) {
            statusLaunch = StatusEntryDTO.CANCELADO;
        } else if (entry.getStatus().equals(StatusEntry.EFFECTIVE)) {
            statusLaunch = StatusEntryDTO.EFETIVADO;
        } else {
            statusLaunch = StatusEntryDTO.PENDENTE;
        }

        return ResponseEntryDTO.builder()
                .id(entry.getId())
                .descricao(entry.getDescription())
                .mes(entry.getMonth())
                .ano(entry.getYear())
                .usuario(convertedUser)
                .valor(entry.getValue())
                .dataCadastro(registrationDate)
                .tipo(typeLaunch)
                .status(statusLaunch)
                .categoria(convertedCategory)
                .latitude(entry.getLatitude())
                .longitude(entry.getLongitude())
                .build();
    }

    @Override
    public List<ResponseEntryDTO> convertEntityListToResponseDTO(List<Entry> entries) {
        List<ResponseEntryDTO> response = new ArrayList<>();
        for (Entry entry: entries) {
            response.add(convertEntityToResponseDTO(entry));
        }
        return response;
    }

    private Entry validateDTO (EntryDTO dto) {
        Entry entry = new Entry();
        entry.setDescription(dto.getDescricao());
        entry.setMonth(dto.getMes());
        entry.setYear(dto.getAno());
        if(dto.getUsuario() != null) {
            entry.setUser(User.builder().id(dto.getUsuario()).build());
        } else {
            throw new BusinessRuleException("Informe um usuário para o lançamento.");
        }
        entry.setValue(dto.getValor());
        if (dto.getCategoria() != null) {
            List<Category> category = new ArrayList<>();
            for (Object cat: dto.getCategoria()) {
                if(String.valueOf(cat).matches("[0-9.]+")) {
                    int idCategory = (int) cat;
                    category.add(categoryService.getById(Long.parseLong(String.valueOf(idCategory))).orElseThrow(() -> new BusinessRuleException("Categoria não encontrada.")));
                } else {
                    category.add(categoryService.getByDecription((String) cat).orElseThrow(() -> new BusinessRuleException("Categoria não encontrada.")));
                }
            }
            entry.setCategory(category);
        }
        if (dto.getTipo().equals("RECEITA")) {
            entry.setType(TypeEntry.RECIPE);
        } else if (dto.getTipo().equals("DESPESA")) {
            entry.setType(TypeEntry.EXPENSE);
        } else {
            throw new BusinessRuleException("Informe um tipo válido para o lançamento.");
        }
        if (dto.getStatus() != null) {
            if (dto.getStatus().equals("CANCELADO")) {
                entry.setStatus(StatusEntry.CANCELED);
            } else if (dto.getStatus().equals("EFETIVADO")) {
                entry.setStatus(StatusEntry.EFFECTIVE);
            } else if (dto.getStatus().equals("PENDENTE") ) {
                entry.setStatus(StatusEntry.PENDING);
            } else {
                throw new BusinessRuleException("Informe um status válido para o lançamento.");
            }
        } else {
            entry.setStatus(StatusEntry.PENDING);
        }
        if (dto.getLatitude() != null) {
            entry.setLatitude(dto.getLatitude());
        } else {
            entry.setLatitude("00.000000");
        }
        if (dto.getLongitude() != null) {
            entry.setLongitude(dto.getLongitude());
        } else {
            entry.setLongitude("00.000000");
        }

        return entry;
    }

    private ResponseUserDTO convertUserToResponseDTO (User user) {
        return ResponseUserDTO.builder()
                .id(user.getId())
                .nome(user.getName())
                .build();
    }

    private Collection<ResponseCategoryDTO> convertCategoryToResponseDTO (Collection<Category> category) {
        if (category == null) {
            return null;
        }

        Collection<ResponseCategoryDTO> categories = new ArrayList<>();

        for (Category cat: category) {
             categories.add(ResponseCategoryDTO.builder()
                    .id(cat.getId())
                    .descricao(cat.getDescription())
                    .ativo(cat.getActive())
                    .build()
             );
        }

        return categories;
    }

}
