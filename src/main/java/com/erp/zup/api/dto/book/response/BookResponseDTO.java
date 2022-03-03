package com.erp.zup.api.dto.book.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    public Long id;
    public String title;
    public String author;
    public String isbn;
}
