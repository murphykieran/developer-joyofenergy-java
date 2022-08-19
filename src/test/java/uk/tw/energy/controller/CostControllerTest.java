package uk.tw.energy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CostControllerTest {
    @Test
    public void shouldReturnNoCostWhenNoReadingsExist() {
        AccountService accountService = mock(AccountService.class);
        PricePlanService pricePlanService = mock(PricePlanService.class);
        CostController costController = new CostController(accountService, pricePlanService);

        ResponseEntity response = costController.getCost("TEST-SMART-METER");

        UsageCost actualCost = (UsageCost) response.getBody();
        assertEquals(BigDecimal.ZERO, actualCost.getCost());
    }

    @Test
    public void shouldReturnCostForReadingsWithOnePricePlan() {

        // given
        String meterId = "meter-id";
        String planId = "plan-id";
        BigDecimal expectedConsumptionCost = BigDecimal.ONE;

        Map<String, BigDecimal> consumptionCostOfElectricityReadingsForEachPricePlan =
                Collections.singletonMap(planId, expectedConsumptionCost);

        AccountService accountService = mock(AccountService.class);
        PricePlanService pricePlanService = mock(PricePlanService.class);

        when(accountService.getPricePlanIdForSmartMeterId(meterId)).thenReturn(planId);
        when(pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(meterId))
                .thenReturn(Optional.of(consumptionCostOfElectricityReadingsForEachPricePlan));

        CostController costController = new CostController(accountService, pricePlanService);

        // when
        ResponseEntity response = costController.getCost(meterId);

        // then
        UsageCost actualUsageCost = (UsageCost) response.getBody();
        assertEquals(BigDecimal.ONE, actualUsageCost.getCost());
    }
}