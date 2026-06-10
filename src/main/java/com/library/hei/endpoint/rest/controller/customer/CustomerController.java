package com.library.hei.endpoint.rest.controller.customer;

import com.library.hei.model.entity.Customer;
import com.library.hei.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
  private final CustomerService customerService;

  @GetMapping
  public List<Customer> getAll() { return customerService.getAll(); }

  @GetMapping("/{id}")
  public Customer getById(@PathVariable String id) { return customerService.getById(id); }

  @PutMapping("/{id}")
  public Customer crupdate(@PathVariable String id, @RequestBody Customer customer) {
    return customerService.crupdate(id, customer);
  }

  @DeleteMapping("/{id}")
  public Customer delete(@PathVariable String id) { return customerService.delete(id); }
}
