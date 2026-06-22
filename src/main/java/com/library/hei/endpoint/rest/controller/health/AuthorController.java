package com.library.hei.endpoint.rest.controller.health;

import com.library.hei.entity.Author;
import com.library.hei.service.AuthorService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

  private final AuthorService authorService;

  public AuthorController(AuthorService authorService) {
    this.authorService = authorService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Author createAuthor(@RequestBody Author author) {
    return authorService.createAuthor(author);
  }

  @GetMapping("/{id}")
  public Author getAuthorById(@PathVariable Long id) {
    return authorService
        .getAuthorById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
  }

  @GetMapping
  public List<Author> getAllAuthors() {
    return authorService.getAllAuthors();
  }

  @GetMapping("/search")
  public List<Author> searchAuthorsByLastName(@RequestParam String lastName) {
    return authorService.searchByLastName(lastName);
  }

  @PutMapping("/{id}")
  public Author updateAuthor(@PathVariable Long id, @RequestBody Author author) {
    return authorService.updateAuthor(id, author);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAuthor(@PathVariable Long id) {
    authorService.deleteAuthor(id);
  }
}
