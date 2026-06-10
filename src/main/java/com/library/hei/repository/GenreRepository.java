package com.library.hei.repository;

import com.library.hei.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
  Optional<Genre> findByNameIgnoreCase(String name);
}
