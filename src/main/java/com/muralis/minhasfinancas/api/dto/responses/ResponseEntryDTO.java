package com.muralis.minhasfinancas.api.dto.responses;

import com.muralis.minhasfinancas.api.dto.enums.StatusEntryDTO;
import com.muralis.minhasfinancas.api.dto.enums.TypeEntryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntryDTO {

    private Long id;
    private String descricao;
    private Integer mes;
    private Integer ano;
    private ResponseUserDTO usuario;
    private BigDecimal valor;
    private String dataCadastro;
    private TypeEntryDTO tipo;
    private StatusEntryDTO status;
    private Collection<ResponseCategoryDTO> categoria;
    private String latitude;
    private String longitude;

}
