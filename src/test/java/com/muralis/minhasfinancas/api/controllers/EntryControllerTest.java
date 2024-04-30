package com.muralis.minhasfinancas.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muralis.minhasfinancas.api.dto.EntryDTO;
import com.muralis.minhasfinancas.api.dto.UpdateStatusDTO;
import com.muralis.minhasfinancas.api.dto.file.EntriesFileDTO;
import com.muralis.minhasfinancas.api.dto.enums.StatusEntryDTO;
import com.muralis.minhasfinancas.api.dto.enums.TypeEntryDTO;
import com.muralis.minhasfinancas.api.dto.file.ResultOfFileEntriesDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseCategoryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseEntryDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseUserDTO;
import com.muralis.minhasfinancas.model.entity.Category;
import com.muralis.minhasfinancas.model.entity.Entry;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.model.enums.StatusEntry;
import com.muralis.minhasfinancas.model.enums.TypeEntry;
import com.muralis.minhasfinancas.service.impl.CategoryServiceImpl;
import com.muralis.minhasfinancas.service.impl.EntryServiceImpl;
import com.muralis.minhasfinancas.service.impl.UserServiceImpl;
import com.muralis.minhasfinancas.utils.impl.ArchiveUtilsImpl;
import com.muralis.minhasfinancas.utils.impl.EntryUtilsImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class EntryControllerTest {

    static final String API = "/lancamentos";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc;

    @MockBean
    EntryServiceImpl service;

    @MockBean
    ArchiveUtilsImpl archiveUtils;

    @MockBean
    EntryUtilsImpl utils;

    @MockBean
    UserServiceImpl userService;

    @MockBean
    CategoryServiceImpl categoryService;

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

    @Test
    @DisplayName("Deve ser possível registrar um novo lançamento.")
    void shouldBeAbleToRegisterALaunch () throws Exception {
        EntryDTO dto = createLaunchDto();
        Entry entry = createLaunch();

        Mockito.when(utils.convertDtoToEntity(Mockito.any())).thenReturn(entry);
        Mockito.when(service.register(Mockito.any())).thenReturn(entry);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Deve ser possível obter um único lançamento.")
    void shouldBeAbleToGetALaunch () throws Exception {
        ResponseEntryDTO responseEntryDTO = createLaunchResponseDto();
        Entry entry = createLaunch();

        Mockito.when(service.getById(Mockito.any())).thenReturn(entry);
        Mockito.when(utils.convertEntityToResponseDTO(Mockito.any())).thenReturn(responseEntryDTO);

        String json = new ObjectMapper().writeValueAsString(entry.getId());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API.concat("/1"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve ser possível obter uma lista de lançamentos.")
    void shouldBeAbleToListLaunches () throws Exception {
        User user = User.builder().build();
        ResponseEntryDTO responseEntryDTO = createLaunchResponseDto();
        List<Entry> entries = new ArrayList<>();
        entries.add(createLaunch());

        Mockito.when(userService.getById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(service.find(
                Mockito.any(), // Id
                Mockito.any(), // Descrição
                Mockito.any(), // Mês
                Mockito.any(), // Ano
                Mockito.any(), // Tipo
                Mockito.any(), // Status
                Mockito.any(), // Id Categoria
                Mockito.any() // Id Usuário
        )).thenReturn(entries);
        Mockito.when(utils.convertEntityToResponseDTO(Mockito.any())).thenReturn(responseEntryDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API.concat("?user=1"))
                .accept(JSON)
                .contentType(JSON);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve ser possível atualizar o status de um lançamento.")
    void shouldBeAbleToUpdateStatus () throws Exception {
        ResponseEntryDTO responseEntryDTO = createLaunchResponseDto();
        Entry entry = createLaunch();
        UpdateStatusDTO updateStatusDTO = createUpdateStatusDtoCanceled();

        Mockito.when(utils.convertEntityToResponseDTO(Mockito.any())).thenReturn(responseEntryDTO);
        Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(entry);
        Mockito.when(service.getById(Mockito.any())).thenReturn(entry);

        String json = new ObjectMapper().writeValueAsString(updateStatusDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/1/atualiza-status"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve ser possível atualizar as informaçoes de um lançamento.")
    void shouldBeAbleToUpdate () throws Exception {
        ResponseEntryDTO responseEntryDTO = createLaunchResponseDto();
        Entry entry = createLaunch();
        EntryDTO launchDTO = createLaunchDto();

        Mockito.when(utils.convertEntityToResponseDTO(Mockito.any())).thenReturn(responseEntryDTO);
        Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(entry);
        Mockito.when(service.getById(Mockito.any())).thenReturn(entry);
        Mockito.when(utils.convertDtoToEntity(Mockito.any())).thenReturn(entry);

        String json = new ObjectMapper().writeValueAsString(launchDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/1"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve ser possível excluir um lançamento.")
    void shouldBeAbleToDelete () throws Exception {
        Entry entry = createLaunch();

        Mockito.when(service.getById(Mockito.any())).thenReturn(entry);
        Mockito.doNothing().when(service).delete(Mockito.any());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(API.concat("/1"))
                .accept(JSON)
                .contentType(JSON);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve ser possível realizar upload de um arquivo csv para cadastrar lançamentos.")
    void shouldBeAbleToUpload () throws Exception {
        List<EntriesFileDTO> archiveLauncheDTOS = createListArchiveLaunchesDto();
        List<Entry> entries = new ArrayList<>();
        entries.add(createLaunch());
        ResultOfFileEntriesDTO result = ResultOfFileEntriesDTO.builder().entries(entries).build();

        Mockito.when(archiveUtils.fileIsValid(Mockito.any())).thenReturn(null);
        Mockito.when(categoryService.getByDecription(Mockito.any())).thenReturn(Optional.ofNullable(archiveLauncheDTOS.get(0).getCategory()));
        Mockito.when(archiveUtils.convertFileToEntity(Mockito.any())).thenReturn(result);
        Mockito.when(service.registerToArchive(Mockito.any())).thenReturn(entries);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/upload"))
                .accept(JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(tempMultipartFile.getBytes());

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    private EntryDTO createLaunchDto () {
        Collection<Object> category = new ArrayList<>();
        category.add(Category.builder().description("description category").build());
        return EntryDTO
                .builder()
                .descricao("description")
                .mes(1)
                .ano(2000)
                .valor(BigDecimal.TEN)
                .tipo("DESPESA")
                .status("EFETIVADO")
                .usuario(1L)
                .latitude("0")
                .longitude("0")
                .categoria(category)
                .build();
    }

    private Entry createLaunch () {
        List<Category> category = new ArrayList<>();
        category.add(Category.builder().description("description category").build());
        return Entry
                .builder()
                .id(1L)
                .description("description")
                .month(1)
                .year(2000)
                .value(BigDecimal.TEN)
                .type(TypeEntry.EXPENSE)
                .status(StatusEntry.EFFECTIVE)
                .user(User.builder().build())
                .latitude("0")
                .longitude("0")
                .category(category)
                .build();
    }

    private ResponseEntryDTO createLaunchResponseDto () {
        Collection<ResponseCategoryDTO> category = new ArrayList<>();
        category.add(ResponseCategoryDTO.builder().descricao("description category").build());
        return ResponseEntryDTO
                .builder()
                .id(1L)
                .descricao("description")
                .mes(1)
                .ano(2000)
                .valor(BigDecimal.TEN)
                .tipo(TypeEntryDTO.DESPESA)
                .status(StatusEntryDTO.EFETIVADO)
                .usuario(ResponseUserDTO.builder().build())
                .latitude("0")
                .longitude("0")
                .categoria(category)
                .build();
    }

    private List<EntriesFileDTO> createListArchiveLaunchesDto () {
        List<EntriesFileDTO> list = new ArrayList<>();
        list.add(EntriesFileDTO
                .builder()
                .description("description")
                .month(1)
                .year(2000)
                .category(Category.builder().description("category").build())
                .value(BigDecimal.TEN)
                .user(1L)
                .type("EXPENSE")
                .status("EFFECTIVE")
                .latitude("0")
                .longitude("0")
                .build());

        return list;
    }

    private UpdateStatusDTO createUpdateStatusDtoCanceled () {
        return UpdateStatusDTO
                .builder()
                .status("CANCELED")
                .build();
    }

}
