package com.muralis.minhasfinancas.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.muralis.minhasfinancas.api.dto.file.ResultOfFileEntriesDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.model.entity.Entry;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ArchiveUtils {

    ResultOfFileEntriesDTO convertFileToEntity (MultipartFile file);

    String convertResponseUpload (Integer total, Integer totalSuccess, Integer totalErrors, List<Map> errors, List<ResponseEntryDTO> entries) throws JsonProcessingException;

    String convertToJson (List<ResponseEntryDTO> launches) throws JsonProcessingException;

    String convertToCsv (List<ResponseEntryDTO> launches);

    Throwable fileIsValid (MultipartFile file);

    String getHeadersToDownload (String format);

    byte[] getFileToDownload (String format, List<ResponseEntryDTO> entries) throws JsonProcessingException;

}
