package com.library.hei.repository;

import com.library.hei.model.entity.Author;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String> {
  List<Author> findByLastNameContainingIgnoreCase(String lastName);
}
