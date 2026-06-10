package com.library.hei.service;

import com.library.hei.model.entity.User;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public List<User> getAll() { return userRepository.findAll(); }

  public User getById(String id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Utilisateur id=" + id + " introuvable"));
  }

  public User crupdate(String id, User user) {
    if (user.getUsername() == null || user.getUsername().isBlank())
      throw new BadRequestException("Le username est obligatoire");
    if (user.getRole() == null)
      throw new BadRequestException("Le rôle est obligatoire (ADMIN ou SELLER)");
    user.setId(id);
    return userRepository.save(user);
  }

  public User delete(String id) {
    User u = getById(id);
    userRepository.deleteById(id);
    return u;
  }
}
