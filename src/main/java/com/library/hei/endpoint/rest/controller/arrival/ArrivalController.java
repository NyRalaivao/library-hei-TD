package com.library.hei.endpoint.rest.controller.arrival;

import com.library.hei.model.entity.Arrival;
import com.library.hei.service.ArrivalService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/arrivals")
public class ArrivalController {
  private final ArrivalService arrivalService;

  @GetMapping
  public List<Arrival> getAll() {
    return arrivalService.getAll();
  }

  @GetMapping("/book/{bookId}")
  public List<Arrival> getByBook(@PathVariable String bookId) {
    return arrivalService.getByBook(bookId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Arrival create(@RequestBody Arrival arrival, @RequestParam String formatId) {
    return arrivalService.createArrival(arrival, formatId);
  }
}
