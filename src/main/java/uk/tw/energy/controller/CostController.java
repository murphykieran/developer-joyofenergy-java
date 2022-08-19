package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

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
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionCostPerPricePlan = pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);
        if (consumptionCostPerPricePlan.isPresent()) {
            BigDecimal consumptionCost = consumptionCostPerPricePlan.get().get(pricePlanId);
            UsageCost usageCost = new UsageCost(consumptionCost);
            return ResponseEntity.ok(usageCost);
        }
        else {
            return ResponseEntity.ok(new UsageCost(BigDecimal.ZERO));
        }
    }
}
