package com.muralis.minhasfinancas.utils.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muralis.minhasfinancas.api.dto.file.ResultOfFileEntriesDTO;
import com.muralis.minhasfinancas.api.dto.file.EntriesFileDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseCategoryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.model.entity.Entry;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.model.enums.StatusEntry;
import com.muralis.minhasfinancas.model.enums.TypeEntry;
import com.muralis.minhasfinancas.service.CategoryService;
import com.muralis.minhasfinancas.service.UserService;
import com.muralis.minhasfinancas.utils.ArchiveUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service
public class ArchiveUtilsImpl implements ArchiveUtils {

    private final CategoryService categoryService;
    private final UserService userService;
    private final Validator validator;

    public ArchiveUtilsImpl(CategoryService categoryService, UserService userService, Validator validator) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.validator = validator;
    }

    private final static int maxSize = 15728640;

    @Override
    public ResultOfFileEntriesDTO convertFileToEntity(MultipartFile file) {
        try {
            File convertedFile = convertToFile(file);
            CSVReader cvsReader = new CSVReader(new FileReader(convertedFile));

            List<Entry> entries = new ArrayList<>();
            List<Map> errors = new ArrayList<>();

            String[] headers = cvsReader.readNext();
            String[] columns;
            int line = 1;
            Integer total = 0;

            while((columns = cvsReader.readNext()) != null) {
                Map<String, String> fields = new HashMap<>();

                for (int i = 0; i < columns.length; i++) {
                    fields.put(headers[i], columns[i]);
                }

                Map result = formatToEntriesFileDto(fields.get("DESC"), fields.get("VALOR_LANC"), fields.get("TIPO"), fields.get("STATUS"), fields.get("USUARIO"), fields.get("DATA_LANC"), fields.get("CATEGORIA"), fields.get("LAT"), fields.get("LONG"), line );

                if (result.get("errors") == null) {
                    entries.add((Entry) result.get("entry"));
                } else {
                    List<Map> err = (List<Map>) result.get("errors");
                    errors.addAll(err);
                }
                total++;
                line++;
            }

            cvsReader.close();

            Files.deleteIfExists(Paths.get(convertedFile.getPath()));

            return ResultOfFileEntriesDTO
                    .builder()
                    .total(total)
                    .entries(entries)
                    .errors(errors)
                    .build();

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> formatToEntriesFileDto (String description,String value, String type, String status, String user,String date, String category, String lat, String longi, int line) {
        try {
            List<Map> errors = new ArrayList<>();

            String descriptionEntity = description;
            BigDecimal valueEntity = BigDecimal.ZERO;
            int monthEntity = 0;
            int yearEntity = 0;
            String typeEntity = "";
            String statusEntity = "";
            long userEntity = 0L;
            Category categoryEntity = Category.builder().build();
            String latEntity = lat;
            String longEntity = longi;

            if(value != null && !value.isEmpty()) {
                value = value.replace("$", "");
                valueEntity = (new BigDecimal(value));
            }

            if(type != null && !type.isEmpty()) {
                typeEntity = type;
            }

            if (date != null && !date.isEmpty()) {
                String[] preDate = date.split("/");
                monthEntity = Integer.parseInt(preDate[1]);
                yearEntity = Integer.parseInt(preDate[2]);
            }

            if(status != null && !status.isEmpty()) {
                statusEntity = status;
            }

            if(user != null && !user.isEmpty()) {
                userEntity = Long.parseLong(user);
                Optional<User> verify = userService.getById(userEntity);
                if(!verify.isPresent()) {
                    userEntity = 0L;
                }
            }

            if(category != null && !category.isEmpty()) {
                Optional<Category> verify = categoryService.getByDecription(category);
                categoryEntity = verify.orElseGet(() -> categoryService.register(Category.builder().description(category).build()));
            }

            EntriesFileDTO entriesFileDto = EntriesFileDTO
                    .builder()
                    .description(descriptionEntity)
                    .value(valueEntity)
                    .month(monthEntity)
                    .year(yearEntity)
                    .user(userEntity)
                    .type(typeEntity)
                    .status(statusEntity)
                    .category(categoryEntity)
                    .latitude(latEntity)
                    .longitude(longEntity)
                    .build();

            validator.validate(entriesFileDto).forEach(violation -> {
                String[] err = violation.getMessage().split(";");
                for (String error : err) {
                    Map<String, Object> values = new HashMap<>();
                    values.put("motivo", error);
                    values.put("linha", line);
                    errors.add(values);
                }
            });

            if (errors.isEmpty()) {
                Entry entry = convertDtoToEntity(entriesFileDto);
                Map response = new HashMap<>();
                response.put("entry", entry);
                return response;
            } else {
                Map response = new HashMap<>();
                response.put("errors", errors);
                return response;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private File convertToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            convertedFile = null;
        }
        return convertedFile;
    }

    private Entry convertDtoToEntity (EntriesFileDTO dto) {
        Entry entry = new Entry();
        entry.setDescription(dto.getDescription());
        entry.setMonth(dto.getMonth());
        entry.setYear(dto.getYear());
        entry.setRegistrationDate(LocalDate.now());
        entry.setValue(dto.getValue());

        if(dto.getUser() != null) {
            Optional<User> user = userService.getById(dto.getUser());
            if(!user.isPresent()) {
                throw new BusinessRuleException("Informe um usuário válido para o lançamento.;");
            } else {
                entry.setUser(user.get());
            }
        }

        if (dto.getCategory() != null) {
            List<Category> category = new ArrayList<>();
            category.add(dto.getCategory());
            entry.setCategory(category);
        } else {
            List<Category> noCategory = new ArrayList<>();
            entry.setCategory(noCategory);
        }

        if (dto.getType().equals("RECEITA")) {
            entry.setType(TypeEntry.RECIPE);
        } else if (dto.getType().equals("DESPESA")) {
            entry.setType(TypeEntry.EXPENSE);
        }

        if (dto.getStatus().equals("CANCELADO")) {
            entry.setStatus(StatusEntry.CANCELED);
        } else if (dto.getStatus().equals("EFETIVADO")) {
            entry.setStatus(StatusEntry.EFFECTIVE);
        } else if (dto.getStatus().equals("PENDENTE")) {
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

    @Override
    public String convertResponseUpload(Integer total, Integer totalSuccess, Integer totalErrors, List<Map> errors, List<ResponseEntryDTO> entries) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapResponse = new HashMap<>();

//        mapResponse.put("erros: ", errors); TODO
        mapResponse.put("totalDeLinhasProcessadas", total);
        mapResponse.put("totalDeSucessos", totalSuccess);
        mapResponse.put("totalDeErros", totalErrors);
        mapResponse.put("lancamentosRegistrados", entries);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapResponse);
    }

    @Override
    public String convertToJson(List<ResponseEntryDTO> entries) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entries);
    }

    @Override
    public String convertToCsv(List<ResponseEntryDTO> entries) {
        StringBuilder csv = new StringBuilder();
        csv.append("DESC,");
        csv.append("VALOR_LANC,");
        csv.append("TIPO,");
        csv.append("STATUS,");
        csv.append("USUARIO,");
        csv.append("DATA_LANC,");
        csv.append("CATEGORIA,");
        csv.append("LAT,");
        csv.append("LONG");
        csv.append("\n");

        for (ResponseEntryDTO responseEntryDTO : entries) {

            csv.append(responseEntryDTO.getDescricao());
            csv.append(",");
            csv.append(responseEntryDTO.getValor());
            csv.append(",");
            csv.append(responseEntryDTO.getTipo().name());
            csv.append(",");
            csv.append(responseEntryDTO.getStatus().name());
            csv.append(",");
            csv.append(responseEntryDTO.getUsuario().getId());
            csv.append(",");
            csv.append(responseEntryDTO.getDataCadastro());
            csv.append(",");
            csv.append(convertCategoryCollectionToString(responseEntryDTO.getCategoria()));
            csv.append(",");
            csv.append(responseEntryDTO.getLatitude());
            csv.append(",");
            csv.append(responseEntryDTO.getLongitude());
            csv.append("\n");

        }

        return csv.toString();
    }

    @Override
    public Throwable fileIsValid(MultipartFile file) {
        if (file == null) {
            throw new BusinessRuleException("Arquivo não encontrado, seleciona um arquivo para importação.");
        } else if (!Objects.equals(file.getContentType(), "text/csv")) {
            throw new BusinessRuleException("Arquivo inválido, selecione um arquivo válido para importação.");
        } else if (file.getSize() > maxSize) {
            throw new BusinessRuleException("Arquivo excede o tamanho limite do sistema, selecione um arquivo de até 1MB para a importação.");
        }
        return null;
    }

    @Override
    public String getHeadersToDownload(String format) {
        if (format == null || format.equalsIgnoreCase("json")) {
            return ("attachment;filename=\"extrato_lancamentos.json\"");
        } else if (format.equalsIgnoreCase("csv")) {
            return ("attachment;filename=\"extrato_lancamentos.csv\"");
        } else {
            throw new BusinessRuleException("Formato de arquivo inválido, informe um formato entre JSON e CSV para obter sua lista, se não informar nenhum formato será enviado um arquivo JSON por padrão.");
        }
    }

    @Override
    public byte[] getFileToDownload(String format, List<ResponseEntryDTO> entriesDTO) throws JsonProcessingException {
        if (format == null || format.equalsIgnoreCase("json")) {
            String json = convertToJson(entriesDTO);

            return json.getBytes();
        } else if (format.equalsIgnoreCase("csv")) {
            String csv = convertToCsv(entriesDTO);

            return csv.getBytes();
        } else {
            throw new BusinessRuleException("Formato de arquivo inválido, informe um formato entre JSON e CSV para obter sua lista, se não informar nenhum formato será enviado um arquivo JSON por padrão.");
        }
    }

    private String convertCategoryCollectionToString (Collection<ResponseCategoryDTO> categories) {
        if (categories.size() > 1) {
            StringBuilder listOfCategories = new StringBuilder();
            for (ResponseCategoryDTO category: categories) {
                listOfCategories.append(category.getDescricao());
                listOfCategories.append(";");
            }
            return listOfCategories.toString();
        } else {
            for (ResponseCategoryDTO category: categories) {
                return category.getDescricao();
            }
        }

        return null;
    }

}
