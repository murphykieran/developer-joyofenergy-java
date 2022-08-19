package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.UsageCost;

@RestController
@RequestMapping("/cost/{smartMeterId}")
public class CostController {
    @GetMapping("")
    public ResponseEntity getCost(@PathVariable String smartMeterId) {
        return ResponseEntity.ok(new UsageCost());
    }
}
