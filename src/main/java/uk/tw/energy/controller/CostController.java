package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cost/{smartMeterId}")
public class CostController {
    @GetMapping("")
    public ResponseEntity<Void> getCost(@PathVariable String smartMeterId) {
        return ResponseEntity.ok().build();
    }
}
