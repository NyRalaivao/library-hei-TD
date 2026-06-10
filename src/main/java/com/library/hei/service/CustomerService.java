package com.library.hei.service;

import com.library.hei.model.entity.Customer;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService {
  private final CustomerRepository customerRepository;

  public List<Customer> getAll() { return customerRepository.findAll(); }

  public Customer getById(String id) {
    return customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Client id=" + id + " introuvable"));
  }

  public Customer crupdate(String id, Customer customer) {
    if (customer.getFirstName() == null || customer.getFirstName().isBlank())
      throw new BadRequestException("Le prénom est obligatoire");
    if (customer.getLastName() == null || customer.getLastName().isBlank())
      throw new BadRequestException("Le nom est obligatoire");
    customer.setId(id);
    return customerRepository.save(customer);
  }

  public Customer delete(String id) {
    Customer c = getById(id);
    customerRepository.deleteById(id);
    return c;
  }
}
