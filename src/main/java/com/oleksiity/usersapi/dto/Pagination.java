package com.oleksiity.usersapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class Pagination {

    private int page;

    private int pageSize;

    private long totalPages;

    private long totalElements;
}
