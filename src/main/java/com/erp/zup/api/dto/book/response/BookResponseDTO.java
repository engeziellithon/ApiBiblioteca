package com.erp.zup.api.dto.book.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private String title;
    private String author;
    private String isbn;
}
