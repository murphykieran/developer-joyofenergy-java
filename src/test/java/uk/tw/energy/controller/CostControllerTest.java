package uk.tw.energy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.domain.UsageCost;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CostControllerTest {
    @Test
    public void shouldReturnNoCostWhenNoReadingsExist() {
        PricePlanService pricePlanService = mock(PricePlanService.class);
        CostController costController = new CostController(pricePlanService);

        ResponseEntity response = costController.getCost("TEST-SMART-METER");

        UsageCost actualCost = (UsageCost) response.getBody();
        assertEquals(BigDecimal.ZERO, actualCost.getCost());
    }

    @Test
    public void shouldReturnCostForReadings() {

        // given
        String meterId = "meter-id";
        ElectricityReading reading = new ElectricityReading(Instant.MIN, BigDecimal.ONE);
        List<ElectricityReading> readings = Collections.singletonList(reading);
        Map<String, List<ElectricityReading>> readingsByMeterId = Collections.singletonMap(meterId, readings);
        MeterReadingService meterReadingService = new MeterReadingService(readingsByMeterId);

        PricePlan pricePlanForMySmartMeter = new PricePlan("My Plan", "My Supplier", BigDecimal.ONE, Collections.EMPTY_LIST);
        List<PricePlan> pricePlans = Collections.singletonList(pricePlanForMySmartMeter);
        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);

        CostController costController = new CostController(pricePlanService);

        // when
        ResponseEntity response = costController.getCost("TEST-SMART-METER");

        // then
        UsageCost actualUsageCost = (UsageCost) response.getBody();
        assertEquals(BigDecimal.ONE, actualUsageCost.getCost());
    }
}