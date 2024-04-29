package com.oleksiity.usersapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Pagination pagination;

    private Collection<T> data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, URI> links;
}
