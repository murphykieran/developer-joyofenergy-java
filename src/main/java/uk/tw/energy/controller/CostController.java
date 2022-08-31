package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/cost/{smartMeterId}")
public class CostController {

    private AccountService accountService;
    private PricePlanService pricePlanService;

    public CostController(AccountService accountService, PricePlanService pricePlanService) {
        this.accountService = accountService;
        this.pricePlanService = pricePlanService;
    }

    @GetMapping("")
    public ResponseEntity getCost(@PathVariable String smartMeterId) {

        LocalDate startDate = LocalDate.now().minusWeeks(1L);
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        BigDecimal consumptionCost = pricePlanService.getConsumptionCostSince(startDate, smartMeterId, pricePlanId);
        UsageCost usageCost = new UsageCost(consumptionCost);
        return ResponseEntity.ok(usageCost);
    }
}
