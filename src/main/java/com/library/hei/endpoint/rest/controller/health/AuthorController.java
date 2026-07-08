package com.library.hei.endpoint.rest.controller.health;

import com.library.hei.model.entity.Author;
import com.library.hei.service.AuthorService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        return authorService.crupdate(null, author);
    }

    @GetMapping("/{id}")
    public Author getAuthorById(@PathVariable String id) {
        return authorService.getById(id);
    }

    @GetMapping
    public List<Author> getAllAuthors() {
        return authorService.getAll();
    }

    @PutMapping("/{id}")
    public Author updateAuthor(@PathVariable String id, @RequestBody Author author) {
        return authorService.crupdate(id, author);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable String id) {
        authorService.delete(id);
    }
}