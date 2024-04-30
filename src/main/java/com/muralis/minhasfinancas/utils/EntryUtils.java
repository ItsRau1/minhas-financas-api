package com.muralis.minhasfinancas.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.muralis.minhasfinancas.api.dto.EntryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.model.entity.Entry;

import java.util.List;

public interface EntryUtils {

    Entry convertDtoToEntity (EntryDTO dto);

    ResponseEntryDTO convertEntityToResponseDTO (Entry entry);

    List<ResponseEntryDTO> convertEntityListToResponseDTO(List<Entry> entries);
}
