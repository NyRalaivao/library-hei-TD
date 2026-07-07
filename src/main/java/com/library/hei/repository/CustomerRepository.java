package com.library.hei.repository;

import com.library.hei.model.entity.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
  List<Customer> findByLastNameContainingIgnoreCase(String lastName);
}
