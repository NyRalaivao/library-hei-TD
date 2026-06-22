package com.library.hei.endpoint.rest.controller.health;

import com.library.hei.entity.Genre;
import com.library.hei.service.GenreService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

  private final GenreService genreService;

  public GenreController(GenreService genreService) {
    this.genreService = genreService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Genre createGenre(@RequestBody Genre genre) {
    return genreService.createGenre(genre);
  }

  @GetMapping("/{id}")
  public Genre getGenreById(@PathVariable Long id) {
    return genreService
        .getGenreById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
  }

  @GetMapping
  public List<Genre> getAllGenres() {
    return genreService.getAllGenres();
  }

  @GetMapping("/search")
  public Genre getGenreByName(@RequestParam String name) {
    return genreService
        .getGenreByName(name)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
  }

  @PutMapping("/{id}")
  public Genre updateGenre(@PathVariable Long id, @RequestBody Genre genre) {
    return genreService.updateGenre(id, genre);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGenre(@PathVariable Long id) {
    genreService.deleteGenre(id);
  }
}
