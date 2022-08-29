package uk.tw.energy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CostControllerTest {
    @Test
    public void shouldReturnNoCostWhenNoReadingsExist() {

        AccountService accountService = mock(AccountService.class);
        PricePlanService pricePlanService = mock(PricePlanService.class);

        CostController costController = new CostController(accountService, pricePlanService);

        when(accountService.getPricePlanIdForSmartMeterId(any(String.class))).thenReturn("PLAN-ID");
        when(pricePlanService.getConsumptionCostForDateRange(any(String.class), any(String.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

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
        LocalDate startDate = LocalDate.now().minusWeeks(1L);

        BigDecimal consumptionCostForDateRange = expectedConsumptionCost;

        AccountService accountService = mock(AccountService.class);
        PricePlanService pricePlanService = mock(PricePlanService.class);

        when(accountService.getPricePlanIdForSmartMeterId(meterId)).thenReturn(planId);
        when(pricePlanService.getConsumptionCostForDateRange(meterId, planId, startDate))
                .thenReturn(consumptionCostForDateRange);

        CostController costController = new CostController(accountService, pricePlanService);

        // when
        ResponseEntity response = costController.getCost(meterId);

        // then
        UsageCost actualUsageCost = (UsageCost) response.getBody();
        assertEquals(expectedConsumptionCost, actualUsageCost.getCost());
    }
}