package com.muralis.minhasfinancas.api.dto;

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
public class EntryDTO {

    private Long id;
    private String descricao;
    private Integer mes;
    private Integer ano;
    private Collection<Object> categoria;
    private BigDecimal valor;
    private Long usuario;
    private String tipo;
    private String status;
    private String latitude;
    private String longitude;

}
