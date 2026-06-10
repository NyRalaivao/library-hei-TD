package com.library.hei.endpoint.rest.controller.user;

import com.library.hei.model.entity.User;
import com.library.hei.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @GetMapping
  public List<User> getAll() { return userService.getAll(); }

  @GetMapping("/{id}")
  public User getById(@PathVariable String id) { return userService.getById(id); }

  @PutMapping("/{id}")
  public User crupdate(@PathVariable String id, @RequestBody User user) {
    return userService.crupdate(id, user);
  }

  @DeleteMapping("/{id}")
  public User delete(@PathVariable String id) { return userService.delete(id); }
}
