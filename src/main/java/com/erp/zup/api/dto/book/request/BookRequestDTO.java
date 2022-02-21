package com.erp.zup.api.dto.book.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {
    @NotBlank(message = "Campo obrigátorio")
    private String title;

    @NotBlank(message = "Campo obrigátorio")
    private String author;

    @NotBlank(message = "Campo obrigátorio")
    private String isbn;
}
