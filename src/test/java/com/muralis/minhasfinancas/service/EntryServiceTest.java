package com.muralis.minhasfinancas.service;

import com.muralis.minhasfinancas.MinhasFinancasApplication;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.Entry;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.model.enums.StatusEntry;
import com.muralis.minhasfinancas.model.enums.TypeEntry;
import com.muralis.minhasfinancas.model.repository.EntryRepository;
import com.muralis.minhasfinancas.service.impl.EntryServiceImpl;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = MinhasFinancasApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class EntryServiceTest {

    @SpyBean
    EntryServiceImpl service;
    @MockBean
    EntryRepository repository;
    @MockBean
    UserService userService;

    @Test
    @DisplayName("Deve ser possível registrar um novo lançamento.")
    void shouldBeAbleToRegisterALaunch () {
        Entry entrySave = createLaunch();
        entrySave.setId(1L);
        Mockito.doNothing().when(service).valid(entrySave);
        Mockito.when(repository.save(entrySave)).thenReturn(entrySave);

        Entry entry = service.register(entrySave);

        assertThat(entry.getId()).isEqualTo(entrySave.getId());
    }

    @Test
    @DisplayName("Não deve ser possível registrar um novo lançamento com um erro de validação.")
    void notShouldBeAbleToRegisterALaunchWithValidationError () {
        Entry entryToSave = createLaunch();
        Mockito.doThrow(BusinessRuleException.class).when(service).valid(entryToSave);

        catchThrowableOfType(() -> service.register(entryToSave), BusinessRuleException.class);

        Mockito.verify(repository, Mockito.never()).save(entryToSave);
    }

    @Test
    @DisplayName("Deve ser possível atualizar um lançamento.")
    void shouldBeAbleToUpdateALaunch () {
        Entry entry = createLaunch();
        entry.setId(1L);

        Mockito.doNothing().when(service).valid(entry);

        Mockito.when(repository.save(entry)).thenReturn(entry);

        service.update(entry, entry);

        Mockito.verify(repository, Mockito.times(1)).save(entry);
    }

    @Test
    @DisplayName("Deve ser possível excluir um lançamento.")
    void shouldBeAbleToDelete () {
        Entry entry = createLaunch();
        entry.setId(1L);

        service.delete(entry);

        Mockito.verify(repository).delete(entry);
    }

    @Test
    @DisplayName("Não deve ser possível excluri um lançamento não registrado.")
    void notShouldBeAbleDeleteALaunchNotSaved () {
        Entry entry = createLaunch();

        catchThrowableOfType( () -> service.delete(entry), NullPointerException.class);

        Mockito.verify(repository, Mockito.never()).delete(entry);
    }

    @Test
    @DisplayName("Deve ser possível atualizar o status de um lançamento.")
    void shouldBeAbleToUpdateStatusOfALaunch () {
        Entry entry = createLaunch();
        entry.setId(1L);
        entry.setStatus(StatusEntry.PENDING);

        Mockito.doNothing().when(service).valid(entry);

        StatusEntry newStatus = StatusEntry.EFFECTIVE;

        service.updateStatus(entry, newStatus);

        assertThat(entry.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("Deve ser possível buscar um lançamento por id.")
    void shouldBeAbleToFindById () {
        Long id = 1L;

        Entry entry = createLaunch();
        entry.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(entry));

        Optional<Entry> res = Optional.ofNullable(service.getById(id));

        assertThat(res.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Não deve ser possível buscar um lançamento por um id inexistente.")
    void notShouldBeAbleToFindByIdWhenLaunchNotExist () {
        Long id = 1L;

        Entry entry = createLaunch();
        entry.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        catchThrowableOfType(() -> service.getById(id), BusinessRuleException.class);
    }

    @Test
    @DisplayName("Não deve ser possível válidar um lançamento com erro.")
    void notShouldBeAbleToValidALaunchWithError() {
        Entry entry = new Entry();

        Throwable error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe uma descrição válida para o lançamento.");

        entry.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam finibus laoreet ante. Nulla molestie.");

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Descrição excede o limite de 100 caracteres, informe uma descrição válida para o lançamento.");

        entry.setDescription("Salario");

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe uma data válida para o lançamento.");

        entry.setMonth(0);

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe uma data válida para o lançamento.");

        entry.setMonth(13);

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe uma data válida para o lançamento.");

        entry.setMonth(1);

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe um ano válido para o lançamento.");

        entry.setYear(202);

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe um ano válido para o lançamento.");

        entry.setYear(2025);

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe um usuário válido para o lançamento.");

        entry.setUser(new User());

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe um usuário válido para o lançamento.");

        entry.getUser().setId(1L);
        Mockito.when(userService.getById(Mockito.any())).thenReturn(Optional.of(User.builder().id(1L).build()));
        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe um valor válido para o lançamento.");

        entry.setValue(BigDecimal.ZERO);

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe um valor válido para o lançamento.");

        entry.setValue(BigDecimal.valueOf(1));

        error = catchThrowable( () -> service.valid(entry) );
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Informe um tipo de lançamento válido para o lançamento.");

    }

    private Entry createLaunch () {
        return Entry.builder()
                .year(2024)
                .month(1)
                .description("Fake Launch")
                .value(BigDecimal.valueOf(10))
                .type(TypeEntry.RECIPE)
                .status(StatusEntry.PENDING)
                .user(User.builder().name("user").id(1L).build())
                .registrationDate(LocalDate.now())
                .build();
    }

}
