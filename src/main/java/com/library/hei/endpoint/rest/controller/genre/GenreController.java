package com.library.hei.endpoint.rest.controller.genre;

import com.library.hei.model.entity.Genre;
import com.library.hei.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/genres")
public class GenreController {
  private final GenreService genreService;

  @GetMapping
  public List<Genre> getAll() { return genreService.getAll(); }

  @GetMapping("/{id}")
  public Genre getById(@PathVariable String id) { return genreService.getById(id); }

  @PutMapping("/{id}")
  public Genre crupdate(@PathVariable String id, @RequestBody Genre genre) {
    return genreService.crupdate(id, genre);
  }

  @DeleteMapping("/{id}")
  public Genre delete(@PathVariable String id) { return genreService.delete(id); }
}
