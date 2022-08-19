package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

@RestController
@RequestMapping("/cost/{smartMeterId}")
public class CostController {

    private PricePlanService pricePlanService;

    public CostController(PricePlanService pricePlanService) {
        this.pricePlanService = pricePlanService;
    }

    @GetMapping("")
    public ResponseEntity getCost(@PathVariable String smartMeterId) {
        return ResponseEntity.ok(new UsageCost());
    }
}
