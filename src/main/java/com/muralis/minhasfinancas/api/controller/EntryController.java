package com.muralis.minhasfinancas.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.muralis.minhasfinancas.api.dto.*;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Entry;
import com.muralis.minhasfinancas.model.enums.StatusEntry;
import com.muralis.minhasfinancas.service.EntryService;
import com.muralis.minhasfinancas.utils.ArchiveUtils;
import com.muralis.minhasfinancas.utils.EntryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/lancamentos")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService service;
    private final ArchiveUtils archiveUtils;
    private final EntryUtils entryUtils;

    @PostMapping
    public ResponseEntity<?> register (@RequestBody EntryDTO dto) {
        try {
            Entry entity = entryUtils.convertDtoToEntity(dto);
            entity = service.register(entity);
            ResponseEntryDTO response = entryUtils.convertEntityToResponseDTO(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLaunch( @PathVariable("id") Long id ) {
        Entry entry;
        try {
            entry = service.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(entryUtils.convertEntityToResponseDTO(entry));
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> find (
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "category", required = false) Long idCategory,
            @RequestParam(value = "user", required = false) Long idUser
    ) {
        List<Entry> entries = service.find(id, description, month, year, type, status, idCategory, idUser);
        List<ResponseEntryDTO> response = entries.stream()
                .map(entryUtils::convertEntityToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusDTO dto) {
        try {
            Entry entry;
            try {
                entry = service.getById(id);
            } catch (BusinessRuleException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            StatusEntry statusSelected = service.validDtoUpdateStatus(dto);
            entry = service.updateStatus(entry, statusSelected);
            ResponseEntryDTO response = entryUtils.convertEntityToResponseDTO(entry);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update (@PathVariable("id") Long id, @RequestBody EntryDTO dto) {
            try{
                Entry entry;
                try {
                    entry = service.getById(id);
                } catch (NullPointerException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lançamento não encontrado para id informado.");
                } catch (BusinessRuleException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                }
                Entry newEntry = entryUtils.convertDtoToEntity(dto);
                Entry updatedEntry = service.update(entry, newEntry);

                ResponseEntryDTO response = entryUtils.convertEntityToResponseDTO(updatedEntry);

                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch ( BusinessRuleException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable ("id") Long id) {
        try {
            Entry entry;
            try {
                entry = service.getById(id);
            } catch (BusinessRuleException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            service.delete(entry);
            return ResponseEntity.status(HttpStatus.OK).body("Lançamento deletado com sucesso.");
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            String response = service.uploadFile(file);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> download (
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "category", required = false) Long idCategory,
            @RequestParam(value = "format", required = false) String format,
            @RequestParam(value = "user", required = false) Long idUser
    ) throws JsonProcessingException {

        List<Entry> entries = service.findToDownload(id, description, month, year, type, status, idCategory, idUser);
        List<ResponseEntryDTO> responseEntryDTO = entries.stream()
                .map(entryUtils::convertEntityToResponseDTO)
                .collect(Collectors.toList());

        try {
            String headersFile = archiveUtils.getHeadersToDownload(format);
            byte[] response = archiveUtils.getFileToDownload(format, responseEntryDTO);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_DISPOSITION, headersFile)
                    .body(response);
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Ocorreu um erro interno ao realizar upload dos lançamentos: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping("/obter-todos-lancamentos")
    public ResponseEntity<?> getAllEntries() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.getAllEntriesJSON());
        } catch (JsonProcessingException e) {
            log.error("Ocorreu um erro interno ao converter uma lista de lançamentos para JSON no método '/obter-todos-lancamentos': ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (RuntimeException e) {
            log.error("Ocorreu um erro interno ao realizar a o método de obter todos os lançamentos: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
