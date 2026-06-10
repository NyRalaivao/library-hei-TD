package com.library.hei.endpoint.rest.controller.author;

import com.library.hei.model.entity.Author;
import com.library.hei.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/authors")
public class AuthorController {
  private final AuthorService authorService;

  @GetMapping
  public List<Author> getAll() { return authorService.getAll(); }

  @GetMapping("/{id}")
  public Author getById(@PathVariable String id) { return authorService.getById(id); }

  @PutMapping("/{id}")
  public Author crupdate(@PathVariable String id, @RequestBody Author author) {
    return authorService.crupdate(id, author);
  }

  @DeleteMapping("/{id}")
  public Author delete(@PathVariable String id) { return authorService.delete(id); }
}
