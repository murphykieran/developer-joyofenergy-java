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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        AccountService accountService = mock(AccountService.class);
        String meterId = "meter-id";
        String planId = "plan-id";
        when(accountService.getPricePlanIdForSmartMeterId(meterId)).thenReturn(planId);
        ElectricityReading firstReading = new ElectricityReading(Instant.MIN, BigDecimal.ONE);
        ElectricityReading secondReading = new ElectricityReading(Instant.MIN.plusSeconds(3600), BigDecimal.ONE);
        List<ElectricityReading> readings = Arrays.asList(firstReading, secondReading);
        Map<String, List<ElectricityReading>> readingsByMeterId = Collections.singletonMap(meterId, readings);
        MeterReadingService meterReadingService = new MeterReadingService(readingsByMeterId);

        PricePlan pricePlanForMySmartMeter = new PricePlan(planId, "My Supplier", BigDecimal.ONE, Collections.EMPTY_LIST);
        List<PricePlan> pricePlans = Collections.singletonList(pricePlanForMySmartMeter);
        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);

        CostController costController = new CostController(accountService, pricePlanService);

        // when
        ResponseEntity response = costController.getCost(meterId);

        // then
        UsageCost actualUsageCost = (UsageCost) response.getBody();
        assertEquals(BigDecimal.ONE, actualUsageCost.getCost());
    }
}