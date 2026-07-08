package com.library.hei.endpoint.rest.controller.health;

import com.library.hei.model.entity.Genre;
import com.library.hei.service.GenreService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        return genreService.crupdate(null, genre);
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable String id) {
        return genreService.getById(id);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAll();
    }

    @PutMapping("/{id}")
    public Genre updateGenre(@PathVariable String id, @RequestBody Genre genre) {
        return genreService.crupdate(id, genre);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenre(@PathVariable String id) {
        genreService.delete(id);
    }
}
