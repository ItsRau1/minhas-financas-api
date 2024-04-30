package com.muralis.minhasfinancas.api.dto.file;

import com.muralis.minhasfinancas.model.entity.Entry;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultOfFileEntriesDTO {

    private Integer total;

    private List<Entry> entries;

    private List<Map> errors;

}
