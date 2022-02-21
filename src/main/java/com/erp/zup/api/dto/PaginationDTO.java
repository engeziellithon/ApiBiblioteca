package com.erp.zup.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDTO<T>  {
    public int totalPages;
    public int totalElements;
    public int size;
    public int number;
    public List<T> content;
}

