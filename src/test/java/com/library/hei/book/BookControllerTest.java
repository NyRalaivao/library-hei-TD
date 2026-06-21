package com.library.hei.book;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.hei.endpoint.rest.controller.GlobalExceptionHandler;
import com.library.hei.endpoint.rest.controller.book.BookController;
import com.library.hei.model.entity.Book;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.service.BookService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookService bookService;

  private Book book;

  @BeforeEach
  void setUp() {
    book =
        Book.builder()
            .id("book-1")
            .title("Les Misérables")
            .isbn("9782070360024")
            .price(new BigDecimal("12.50"))
            .build();
  }

  @Test
  void getAll_withExistingBooks_shouldReturn200() throws Exception {
    when(bookService.getAll(0, 10)).thenReturn(List.of(book));

    mockMvc
        .perform(get("/books"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].title").value("Les Misérables"));
  }

  @Test
  void getAll_withTitlePrefix_shouldSearchByTitle() throws Exception {
    when(bookService.searchByTitle("Les", 0, 10)).thenReturn(List.of(book));

    mockMvc
        .perform(get("/books").param("titlePrefix", "Les"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Les Misérables"));

    verify(bookService).searchByTitle("Les", 0, 10);
    verify(bookService, never()).getAll(anyInt(), anyInt());
  }

  @Test
  void getAll_withCustomPagination_shouldPassParamsToService() throws Exception {
    when(bookService.getAll(2, 5)).thenReturn(List.of());

    mockMvc
        .perform(get("/books").param("page", "2").param("pageSize", "5"))
        .andExpect(status().isOk());

    verify(bookService).getAll(2, 5);
  }

  @Test
  void getById_withExistingId_shouldReturn200() throws Exception {
    when(bookService.getById("book-1")).thenReturn(book);

    mockMvc
        .perform(get("/books/book-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("book-1"))
        .andExpect(jsonPath("$.title").value("Les Misérables"));
  }

  @Test
  void getById_withNonExistingId_shouldThrow404() throws Exception {
    when(bookService.getById("unknown"))
        .thenThrow(new NotFoundException("Livre id=unknown introuvable"));

    mockMvc
        .perform(get("/books/unknown"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Livre id=unknown introuvable"))
        .andExpect(jsonPath("$.path").value("/books/unknown"));
  }

  @Test
  void getByIsbn_withExistingIsbn_shouldReturn200() throws Exception {
    when(bookService.getByIsbn("9782070360024")).thenReturn(book);

    mockMvc
        .perform(get("/books/isbn/9782070360024"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isbn").value("9782070360024"));
  }

  @Test
  void getByIsbn_withNonExistingIsbn_shouldThrow404() throws Exception {
    when(bookService.getByIsbn("0000000000"))
        .thenThrow(new NotFoundException("Livre ISBN=0000000000 introuvable"));

    mockMvc.perform(get("/books/isbn/0000000000")).andExpect(status().isNotFound());
  }

  @Test
  void crupdate_withValidBody_shouldReturn200() throws Exception {
    when(bookService.crupdate(eq("book-1"), any(Book.class))).thenReturn(book);

    mockMvc
        .perform(
            put("/books/book-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Les Misérables"));
  }

  @Test
  void crupdate_withInvalidBody_shouldThrow400() throws Exception {
    Book invalid = Book.builder().price(BigDecimal.TEN).build(); // titre manquant
    when(bookService.crupdate(eq("book-1"), any(Book.class)))
        .thenThrow(new BadRequestException("Le titre du livre est obligatoire"));

    mockMvc
        .perform(
            put("/books/book-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
  }

  @Test
  void delete_withExistingId_shouldReturn200() throws Exception {
    when(bookService.delete("book-1")).thenReturn(book);

    mockMvc
        .perform(delete("/books/book-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("book-1"));

    verify(bookService).delete("book-1");
  }

  @Test
  void delete_withNonExistingId_shouldThrow404() throws Exception {
    when(bookService.delete("unknown"))
        .thenThrow(new NotFoundException("Livre id=unknown introuvable"));

    mockMvc.perform(delete("/books/unknown")).andExpect(status().isNotFound());
  }
}
