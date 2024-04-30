package com.muralis.minhasfinancas.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.muralis.minhasfinancas.MinhasFinancasApplication;
import com.muralis.minhasfinancas.api.dto.file.EntriesFileDTO;
import com.muralis.minhasfinancas.api.dto.file.ResultOfFileEntriesDTO;
import com.muralis.minhasfinancas.api.dto.enums.StatusEntryDTO;
import com.muralis.minhasfinancas.api.dto.enums.TypeEntryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseCategoryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseUserDTO;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.model.entity.Entry;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.service.CategoryService;
import com.muralis.minhasfinancas.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest(classes = MinhasFinancasApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ArchiveUtilsTest {

    @SpyBean
    ArchiveUtils utils;

    @MockBean
    CategoryService categoryService;
    @MockBean
    UserService userService;

    @TempDir
    static Path tempDir;
    static Path tempFile;
    static MultipartFile tempMultipartFile;

    @BeforeAll
    public static void init() throws IOException {
        tempFile = Files.createFile(tempDir.resolve("file.csv"));

        String contentFile = "DESC,VALOR_LANC,TIPO,STATUS,USUARIO,DATA_LANC,CATEGORIA,LAT,LONG\n" +
                "cras in purus eu magna vulputate luctus,$3.14,RECEITA,CANCELADO,9,22/06/2022,Energy,41.9164917,20.2862003\n" +
                ",$5.36,,PENDENTE,,06/04/2022,,50.2709275,12.7842341";

        Files.write(tempFile, contentFile.getBytes());

        Path path = Paths.get(tempFile.toUri());
        String name = "multipartFile.csv";
        String originalFileName = "file.csv";
        String contentType = "text/csv";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
        tempMultipartFile = new MockMultipartFile(name,
                originalFileName, contentType, content);
    }

    @AfterAll
    public static void finish () throws IOException {
        Files.delete(tempFile);
    }

    @Test
    @DisplayName("Deve ser possível converter um lançamento DTO para entidade lançamento.")
    void shouldBeAbleToConvertDtoToLaunch () {

        EntriesFileDTO dto = EntriesFileDTO
                .builder()
                .description("description")
                .value(new BigDecimal(10))
                .type("RECEITA")
                .status("PENDENTE")
                .user(1L)
                .month(1)
                .year(2004)
                .category(Category.builder().description("category description").build())
                .latitude("0")
                .longitude("0")
                .build();

        ResultOfFileEntriesDTO response = utils.convertFileToEntity(tempMultipartFile);

        assertThat(response.getEntries()).hasSize(0);
        System.out.println(response.getErrors());
        assertThat(response.getErrors()).hasSize(4);
    }

    // TODO Teste ("Deve ser possível converter uma lista de lançamentos DTO para uma resposta de requisição.")

    @Test
    @DisplayName("Deve ser possível converter um lançamento DTO para JSON.")
    void shouldBeAbleToConvertToJson () throws JsonProcessingException {
        List<ResponseEntryDTO> responseEntryDto = new ArrayList<>();
        Collection<ResponseCategoryDTO> collectionCategory = new ArrayList<>();
        collectionCategory.add(ResponseCategoryDTO.builder().build());
        responseEntryDto.add(ResponseEntryDTO
                .builder()
                .id(1L)
                .descricao("Description")
                .mes(1)
                .ano(2024)
                .tipo(TypeEntryDTO.RECEITA)
                .status(StatusEntryDTO.EFETIVADO)
                .valor(BigDecimal.TEN)
                .categoria(collectionCategory)
                .dataCadastro("01-01-2000")
                .usuario(ResponseUserDTO.builder().build())
                .latitude("0")
                .longitude("0")
                .build());

        String json = utils.convertToJson(responseEntryDto);

        assertThat(json).isNotEmpty();
    }

    @Test
    @DisplayName("Deve ser possível converter um lançamento DTO para CSV.")
    void shouldBeAbleToConvertToCsv () {
        List<ResponseEntryDTO> responseEntryDto = new ArrayList<>();
        Collection<ResponseCategoryDTO> collectionCategory = new ArrayList<>();
        collectionCategory.add(ResponseCategoryDTO.builder().build());
        responseEntryDto.add(ResponseEntryDTO
                .builder()
                .id(1L)
                .descricao("Description")
                .mes(1)
                .ano(2024)
                .tipo(TypeEntryDTO.RECEITA)
                .status(StatusEntryDTO.EFETIVADO)
                .valor(BigDecimal.TEN)
                .categoria(collectionCategory)
                .dataCadastro("01-01-2000")
                .usuario(ResponseUserDTO.builder().build())
                .latitude("0")
                .longitude("0")
                .build());

        String csv = utils.convertToCsv(responseEntryDto);

        assertThat(csv).isNotEmpty();
    }

}
