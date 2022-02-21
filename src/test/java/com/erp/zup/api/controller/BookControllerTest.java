package com.erp.zup.api.controller;

import com.erp.zup.service.book.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    BookService bookService;

    @Test
    @DisplayName("must successfully create a book with status code 201 ")
    public void sendBookShouldReturnSuccess() throws Exception {
        //String json = new ObjectMapper().writeValueAsString("null");

//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(BOOK_API)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(json);
//
//
//
//
//        MvcResult ateste = mvc.perform(requestBuilder).andExpect(status().isCreated())
//                .andExpect(MockMvcResultMatchers.header().stringValues("Location")).andReturn();



    }

}
