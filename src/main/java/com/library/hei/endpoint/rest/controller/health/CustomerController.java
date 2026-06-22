package com.library.hei.endpoint.rest.controller.health;

import com.library.hei.entity.Customer;
import com.library.hei.service.CustomerService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Customer createCustomer(@RequestBody Customer customer) {
    return customerService.createCustomer(customer);
  }

  @GetMapping("/{id}")
  public Customer getCustomerById(@PathVariable Long id) {
    return customerService
        .getCustomerById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
  }

  @GetMapping
  public List<Customer> getAllCustomers() {
    return customerService.getAllCustomers();
  }

  @GetMapping("/search")
  public List<Customer> searchCustomersByLastName(@RequestParam String lastName) {
    return customerService.searchByLastName(lastName);
  }

  @PutMapping("/{id}")
  public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
    return customerService.updateCustomer(id, customer);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCustomer(@PathVariable Long id) {
    customerService.deleteCustomer(id);
  }
}
