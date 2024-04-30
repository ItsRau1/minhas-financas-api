package com.muralis.minhasfinancas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.muralis.minhasfinancas.api.dto.UpdateStatusDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseBalanceDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.model.entity.Entry;
import com.muralis.minhasfinancas.model.enums.StatusEntry;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EntryService {

    Entry register (Entry entry);

    String uploadFile (MultipartFile file);

    List<Entry> registerToArchive (List<Entry> entries);

//    Entry update (Entry entry);

    void delete (Entry entry);

    List<Entry> find (Long id, String description, Integer month, Integer year, String type, String status, Long idCategory, Long idUser);

    List<Entry> findToDownload (Long id, String description, Integer month, Integer year, String type, String status, Long idCategory, Long idUser);

    Entry updateStatus (Entry entry, StatusEntry statusEntry);

    Entry update (Entry oldEntryToUpdate, Entry newEntry);

    void valid (Entry entry);

    StatusEntry validDtoUpdateStatus (UpdateStatusDTO dto);

    Entry getById (Long id);

    ResponseBalanceDTO getBalanceById (Long id);

    List<ResponseEntryDTO> getAllEntriesJSON () throws JsonProcessingException;

}
