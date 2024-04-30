package com.muralis.minhasfinancas.api.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCategoryDTO {

    private Long id;
    private String descricao;
    private boolean ativo;

}
