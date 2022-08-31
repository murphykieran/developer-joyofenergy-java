package uk.tw.energy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CostControllerTest {
    @Test
    public void shouldReturnNoCostWhenNoReadingsExist() {

        AccountService accountService = mock(AccountService.class);
        PricePlanService pricePlanService = mock(PricePlanService.class);

        CostController costController = new CostController(accountService, pricePlanService);

        when(accountService.getPricePlanIdForSmartMeterId(any(String.class))).thenReturn("PLAN-ID");
        when(pricePlanService.getConsumptionCostSince(any(LocalDate.class), any(String.class), any(String.class)))
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

        LocalDateTimeFactory localDateTimeFactory = mock(LocalDateTimeFactory.class);
        LocalDateTime now = LocalDateTime.of(2022, 1, 15, 15, 5, 10);
        when(localDateTimeFactory.now()).thenReturn(now);

        AccountService accountService = mock(AccountService.class);
        PricePlanService pricePlanService = mock(PricePlanService.class);

        when(accountService.getPricePlanIdForSmartMeterId(meterId)).thenReturn(planId);
        when(pricePlanService.getConsumptionCostSince(now.toLocalDate(), meterId, planId))
                .thenReturn(expectedConsumptionCost);

        CostController costController = new CostController(accountService, pricePlanService, localDateTimeFactory);

        // when
        ResponseEntity response = costController.getCost(meterId);

        // then
        UsageCost actualUsageCost = (UsageCost) response.getBody();
        assertEquals(expectedConsumptionCost, actualUsageCost.getCost());
    }
}