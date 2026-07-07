package com.library.hei.service;

import com.library.hei.model.entity.Author;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.AuthorRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorService {
  private final AuthorRepository authorRepository;

  public List<Author> getAll() {
    return authorRepository.findAll();
  }

  public Author getById(String id) {
    return authorRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Auteur id=" + id + " introuvable"));
  }

  public Author crupdate(String id, Author author) {
    if (author.getFirstName() == null || author.getFirstName().isBlank())
      throw new BadRequestException("Le prénom de l'auteur est obligatoire");
    if (author.getLastName() == null || author.getLastName().isBlank())
      throw new BadRequestException("Le nom de l'auteur est obligatoire");
    author.setId(id);
    return authorRepository.save(author);
  }

  public Author delete(String id) {
    Author author = getById(id);
    authorRepository.deleteById(id);
    return author;
  }

  public List<Author> searchByLastName(String lastName) {
    return authorRepository.findByLastNameContainingIgnoreCase(lastName);
  }
}
