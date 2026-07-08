package com.library.hei.endpoint.rest.controller.health;

import com.library.hei.model.entity.Customer;
import com.library.hei.service.CustomerService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    return customerService.crupdate(null, customer);
  }

  @GetMapping("/{id}")
  public Customer getCustomerById(@PathVariable String id) {
    return customerService.getById(id);
  }

  @GetMapping
  public List<Customer> getAllCustomers() {
    return customerService.getAll();
  }

  @PutMapping("/{id}")
  public Customer updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
    return customerService.crupdate(id, customer);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCustomer(@PathVariable String id) {
    customerService.delete(id);
  }
}
