package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.User;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  private User seller;
  private User admin;

  @BeforeEach
  void setUp() {
    seller = User.builder().id("user-1").username("alice").role(User.UserRole.SELLER).build();
    admin = User.builder().id("user-2").username("bob").role(User.UserRole.ADMIN).build();
  }

  // ─── getAll ───────────────────────────────────────────────────────────────

  @Test
  void getAll_returnsAllUsers() {
    when(userRepository.findAll()).thenReturn(List.of(seller, admin));

    List<User> result = userService.getAll();

    assertEquals(2, result.size());
    verify(userRepository).findAll();
  }

  @Test
  void getAll_returnsEmptyList() {
    when(userRepository.findAll()).thenReturn(List.of());

    assertTrue(userService.getAll().isEmpty());
  }

  // ─── getById ──────────────────────────────────────────────────────────────

  @Test
  void getById_found() {
    when(userRepository.findById("user-1")).thenReturn(Optional.of(seller));

    User result = userService.getById("user-1");

    assertEquals("alice", result.getUsername());
    assertEquals(User.UserRole.SELLER, result.getRole());
  }

  @Test
  void getById_notFound_throwsNotFoundException() {
    when(userRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.getById("unknown"));
  }

  // ─── crupdate ─────────────────────────────────────────────────────────────

  @Test
  void crupdate_validUser_savesAndReturns() {
    when(userRepository.save(any(User.class))).thenReturn(seller);

    User result = userService.crupdate("user-1", seller);

    assertEquals("alice", result.getUsername());
    verify(userRepository).save(seller);
  }

  @Test
  void crupdate_setsIdBeforeSaving() {
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    User input = User.builder().username("charlie").role(User.UserRole.SELLER).build();
    User result = userService.crupdate("new-id", input);

    assertEquals("new-id", result.getId());
  }

  @Test
  void crupdate_nullUsername_throwsBadRequest() {
    User invalid = User.builder().username(null).role(User.UserRole.SELLER).build();

    assertThrows(BadRequestException.class, () -> userService.crupdate("user-1", invalid));
    verify(userRepository, never()).save(any());
  }

  @Test
  void crupdate_blankUsername_throwsBadRequest() {
    User invalid = User.builder().username("   ").role(User.UserRole.SELLER).build();

    assertThrows(BadRequestException.class, () -> userService.crupdate("user-1", invalid));
    verify(userRepository, never()).save(any());
  }

  @Test
  void crupdate_nullRole_throwsBadRequest() {
    User invalid = User.builder().username("alice").role(null).build();

    assertThrows(BadRequestException.class, () -> userService.crupdate("user-1", invalid));
    verify(userRepository, never()).save(any());
  }

  // ─── delete ───────────────────────────────────────────────────────────────

  @Test
  void delete_existingUser_deletesAndReturns() {
    when(userRepository.findById("user-1")).thenReturn(Optional.of(seller));
    doNothing().when(userRepository).deleteById("user-1");

    User result = userService.delete("user-1");

    assertEquals("alice", result.getUsername());
    verify(userRepository).deleteById("user-1");
  }

  @Test
  void delete_notFound_throwsNotFoundException() {
    when(userRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.delete("unknown"));
    verify(userRepository, never()).deleteById(any());
  }
}
