package com.muralis.minhasfinancas.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.muralis.minhasfinancas.api.dto.UpdateStatusDTO;
import com.muralis.minhasfinancas.api.dto.file.ResultOfFileEntriesDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseBalanceDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Entry;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.model.enums.StatusEntry;
import com.muralis.minhasfinancas.model.enums.TypeEntry;
import com.muralis.minhasfinancas.model.repository.EntryRepository;
import com.muralis.minhasfinancas.service.EntryService;
import com.muralis.minhasfinancas.service.UserService;
import com.muralis.minhasfinancas.utils.ArchiveUtils;
import com.muralis.minhasfinancas.utils.EntryUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class EntryServiceImpl implements EntryService {
    private final EntryUtils utils;
    private final EntryRepository repository;
    private final UserService userService;
    private final ArchiveUtils archiveUtils;

    public EntryServiceImpl(EntryRepository repository, UserService userService, ArchiveUtils archiveUtils, EntryUtils utils) {
        this.utils = utils;
        this.repository = repository;
        this.userService = userService;
        this.archiveUtils = archiveUtils;
    }

    @Override
    @Transactional
    public Entry register(Entry entry) {
        valid(entry);
        entry.setRegistrationDate(LocalDate.now());
        return repository.save(entry);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            archiveUtils.fileIsValid(file);
            ResultOfFileEntriesDTO result = archiveUtils.convertFileToEntity(file);
            registerToArchive(result.getEntries());
            List<ResponseEntryDTO> entriesDTO = utils.convertEntityListToResponseDTO(result.getEntries());
            return (archiveUtils.convertResponseUpload(result.getTotal(), result.getEntries().size(), result.getErrors().size(), result.getErrors(), entriesDTO));
        } catch (BusinessRuleException | JsonProcessingException e) {
            throw new BusinessRuleException(e.getMessage());
        }

    }

    @Override
    public List<Entry> registerToArchive(List<Entry> entries) {
        return repository.saveAll(entries);
    }

    @Override
    public void delete(Entry entry) {
        Objects.requireNonNull(entry.getId());
        repository.delete(entry);
    }

    @Override
    public List<Entry> find(Long id, String description, Integer month, Integer year, String type, String status, Long idCategory, Long idUser) {
        String idString = "%%";
        String idCategoryString = "%%";
        String monthString = "%%";
        String yearString = "%%";
        String idUserString = "%%";

        if (id != null) {
            idString = id.toString();
        }
        if (month != null) {
            monthString = month.toString();
        }
        if (year != null) {
            yearString = year.toString();
        }
        if (idUser != null) {
            idUserString = idUser.toString();
        }

        if (idCategory != null) {
            idCategoryString = idCategory.toString();
            return repository.findByFilters(idString, description, monthString, yearString, idCategoryString, type, status, idUserString);
        }

        return repository.findAllNoCategoryFilter(idString, description, monthString, yearString, type, status, idUserString);
    }

    @Override
    public List<Entry> findToDownload(Long id, String description, Integer month, Integer year, String type, String status, Long idCategory, Long idUser) {
        String idString = "%%";
        String monthString = "%%";
        String yearString = String.valueOf(LocalDate.now().getYear());
        String idCategoryString = "%%";
        String idUserString = "%%";

        if (id != null) {
            idString = id.toString();
        }
        if (month != null) {
            monthString = month.toString();
        }
        if (year != null) {
            yearString = year.toString();
        }
        if (idUser != null) {
            idUserString = idUser.toString();
        }
        if (idCategory != null) {
            idCategoryString = idCategory.toString();
            return repository.findByFilters(idString, description, monthString, yearString, idCategoryString, type, status, idUserString);
        }

        return repository.findAllNoCategoryFilter(idString, description, monthString, yearString, type, status, idUserString);
    }

    @Transactional
    @Override
    public Entry updateStatus(Entry entry, StatusEntry statusEntry) {
        entry.setStatus(statusEntry);
        valid(entry);
        return repository.save(entry);
    }

    @Override
    public Entry update(Entry oldEntryToUpdate, Entry newEntry) {
        if (oldEntryToUpdate.getStatus() != StatusEntry.PENDING) {
            throw new BusinessRuleException("Não é possível editar um lançamento já efetivado ou cancelado, somente lançamentos pendentes podem ser atualizados.");
        }
        newEntry.setId(oldEntryToUpdate.getId());
        newEntry.setRegistrationDate(oldEntryToUpdate.getRegistrationDate());
        newEntry.setUpdatedDate(LocalDate.now());
        valid(newEntry);
        return repository.save(newEntry);
    }

    @Override
    public void valid(Entry entry) {
        if (entry.getDescription() == null || entry.getDescription().trim().isEmpty()) {
            throw new BusinessRuleException("Informe uma descrição válida para o lançamento.");
        }
        if (entry.getDescription().length() > 100) {
            throw new BusinessRuleException("Descrição excede o limite de 100 caracteres, informe uma descrição válida para o lançamento.");
        }
        if(entry.getMonth() == null || entry.getMonth() < 1 || entry.getMonth() > 12) {
            throw new BusinessRuleException("Informe uma data válida para o lançamento.");
        }
        if(entry.getYear() == null || entry.getYear().toString().length() != 4 ) {
            throw new BusinessRuleException("Informe um ano válido para o lançamento.");
        }
        if(entry.getUser() == null || entry.getUser().getId() == null) {
            throw new BusinessRuleException("Informe um usuário válido para o lançamento.");
        }
        Optional<User> user = userService.getById(entry.getUser().getId());
        if(!user.isPresent()) {
            throw new BusinessRuleException("Informe um usuário válido para o lançamento.");
        } else {
            entry.setUser(user.get());
        }
        if(entry.getValue() == null || entry.getValue().compareTo(BigDecimal.ZERO) < 1 ) {
            throw new BusinessRuleException("Informe um valor válido para o lançamento.");
        }
        if(entry.getType() == null) {
            throw new BusinessRuleException("Informe um tipo de lançamento válido para o lançamento.");
        }
        if (entry.getStatus() == null) {
            entry.setStatus(StatusEntry.PENDING);
        }
    }

    @Override
    public StatusEntry validDtoUpdateStatus(UpdateStatusDTO dto) {
        StatusEntry statusSelected;

        if (dto.getStatus() != null) {
            if (dto.getStatus().equals("CANCELADO")) {
                statusSelected = (StatusEntry.CANCELED);
            } else if (dto.getStatus().equals("EFETIVADO")) {
                statusSelected = (StatusEntry.EFFECTIVE);
            } else if (dto.getStatus().equals("PENDENTE")) {
                statusSelected = (StatusEntry.PENDING);
            } else {
                throw new BusinessRuleException("Status inválido.");
            }
        } else {
            throw new BusinessRuleException("Informe um status.");
        }

        return statusSelected;
    }

    @Override
    public Entry getById (Long id) {
        Optional<Entry> launch = repository.findById(id);
        if (!launch.isPresent()) {
            throw new BusinessRuleException("Lançamento não encontrado para o id informado.");
        } else {
            return launch.get();
        }
    }

    @Override
    @Transactional
    public ResponseBalanceDTO getBalanceById(Long id) {
        BigDecimal recipes = repository.getBalanceByIdAndType(id, TypeEntry.RECIPE, StatusEntry.EFFECTIVE);
        BigDecimal expenses = repository.getBalanceByIdAndType(id, TypeEntry.EXPENSE, StatusEntry.EFFECTIVE);

        BigDecimal recipesMonth = repository.getBalanceByIdAndTypeAndMonth(id, TypeEntry.RECIPE, StatusEntry.EFFECTIVE, LocalDate.now().getMonthValue());
        BigDecimal expensesMonth = repository.getBalanceByIdAndTypeAndMonth(id, TypeEntry.EXPENSE, StatusEntry.EFFECTIVE, LocalDate.now().getMonthValue());

        if (recipes == null) {
            recipes = BigDecimal.ZERO;
        }
        if (expenses == null) {
            expenses = BigDecimal.ZERO;
        }
        if (recipesMonth == null) {
            recipesMonth = BigDecimal.ZERO;
        }
        if (expensesMonth == null) {
            expensesMonth = BigDecimal.ZERO;
        }

        return(ResponseBalanceDTO.builder().total(recipes.subtract(expenses)).mensal(recipesMonth.subtract(expensesMonth)).build());
    }

    @Override
    public List<ResponseEntryDTO> getAllEntriesJSON() {
        List<Entry> allEntries = repository.findAll();
        return utils.convertEntityListToResponseDTO(allEntries);
    }

}
