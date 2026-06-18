package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.Customer;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock private CustomerRepository customerRepository;

  @InjectMocks private CustomerService customerService;

  private Customer marie;
  private Customer jean;

  @BeforeEach
  void setUp() {
    marie = Customer.builder().id("c-1").firstName("Marie").lastName("Curie").build();
    jean = Customer.builder().id("c-2").firstName("Jean").lastName("Dupont").build();
  }

  @Test
  void getAll_returnsAllCustomers() {
    when(customerRepository.findAll()).thenReturn(List.of(marie, jean));

    List<Customer> result = customerService.getAll();

    assertEquals(2, result.size());
    verify(customerRepository).findAll();
  }

  @Test
  void getAll_returnsEmptyList() {
    when(customerRepository.findAll()).thenReturn(List.of());

    assertTrue(customerService.getAll().isEmpty());
  }

  @Test
  void getById_found() {
    when(customerRepository.findById("c-1")).thenReturn(Optional.of(marie));

    Customer result = customerService.getById("c-1");

    assertEquals("Marie", result.getFirstName());
    assertEquals("Curie", result.getLastName());
  }

  @Test
  void getById_notFound_throwsNotFoundException() {
    when(customerRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> customerService.getById("unknown"));
  }

  @Test
  void crupdate_validCustomer_savesAndReturns() {
    when(customerRepository.save(any(Customer.class))).thenReturn(marie);

    Customer result = customerService.crupdate("c-1", marie);

    assertEquals("Marie", result.getFirstName());
    verify(customerRepository).save(marie);
  }

  @Test
  void crupdate_setsIdBeforeSaving() {
    when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

    Customer input = Customer.builder().firstName("Alice").lastName("Martin").build();
    Customer result = customerService.crupdate("new-id", input);

    assertEquals("new-id", result.getId());
  }

  @Test
  void crupdate_nullFirstName_throwsBadRequest() {
    Customer invalid = Customer.builder().firstName(null).lastName("Curie").build();

    assertThrows(BadRequestException.class, () -> customerService.crupdate("c-1", invalid));
    verify(customerRepository, never()).save(any());
  }

  @Test
  void crupdate_blankFirstName_throwsBadRequest() {
    Customer invalid = Customer.builder().firstName("  ").lastName("Curie").build();

    assertThrows(BadRequestException.class, () -> customerService.crupdate("c-1", invalid));
  }

  @Test
  void crupdate_nullLastName_throwsBadRequest() {
    Customer invalid = Customer.builder().firstName("Marie").lastName(null).build();

    assertThrows(BadRequestException.class, () -> customerService.crupdate("c-1", invalid));
    verify(customerRepository, never()).save(any());
  }

  @Test
  void crupdate_blankLastName_throwsBadRequest() {
    Customer invalid = Customer.builder().firstName("Marie").lastName("").build();

    assertThrows(BadRequestException.class, () -> customerService.crupdate("c-1", invalid));
  }

  @Test
  void delete_existingCustomer_deletesAndReturns() {
    when(customerRepository.findById("c-1")).thenReturn(Optional.of(marie));
    doNothing().when(customerRepository).deleteById("c-1");

    Customer result = customerService.delete("c-1");

    assertEquals("Marie", result.getFirstName());
    verify(customerRepository).deleteById("c-1");
  }

  @Test
  void delete_notFound_throwsNotFoundException() {
    when(customerRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> customerService.delete("unknown"));
    verify(customerRepository, never()).deleteById(any());
  }
}
