package com.muralis.minhasfinancas.api.dto.file;

import com.muralis.minhasfinancas.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntriesFileDTO {

    @Size(max = 100, message = "Descrição excede o limite de 100 caracteres, informe uma descrição válida para o lançamento.")
    @NotBlank(message = "Informe uma descrição válida para o lançamento.")
    private String description;

    @Min(value = 1, message = "Informe um mês válido para o lançamento.")
    @Max(value = 12, message = "Informe um mês válido para o lançamento.")
    @NotNull(message = "Informe um mês válido para o lançamento.")
    private Integer month;

    @Min(value = 1000, message = "Informe um ano válido para o lançamento.")
    @NotNull(message = "Informe um ano válido para o lançamento.")
    private Integer year;

    private Category category;

    @DecimalMin(value = "0.01", message = "Informe um valor válido para o lançamento.")
    @NotNull(message = "Informe um valor válido para o lançamento.")
    private BigDecimal value;

    @Min(value = 1, message = "Informe um usuário válido para o lançamento.")
    @NotNull(message = "Informe um usuário válido para o lançamento.")
    private Long user;

    @Pattern(regexp = "RECEITA|DESPESA", message = "Informe um tipo de lançamento válido para o lançamento, tipos válidos são 'RECEITA' e 'DESPESA'.")
    @NotNull(message = "Informe um tipo de lançamento válido para o lançamento.")
    private String type;

    @Pattern(regexp = "PENDENTE|EFETIVADO|CANCELADO", message = "Informe um status de lançamento válido para o lançamento, statis válidos são 'PENDENTE', 'EFETIVADO' e 'CANCELADO'.")
    private String status;

    private String latitude;
    private String longitude;

}
