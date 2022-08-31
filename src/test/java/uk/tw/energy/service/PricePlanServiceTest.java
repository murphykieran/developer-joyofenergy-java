package uk.tw.energy.service;

import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PricePlanServiceTest {

    @Test
    public void startDateInFutureGetsUsageCostZero() {

        // given
        LocalDate startDate = LocalDate.now().plusWeeks(1);
        String meterId = "myMeter";
        String planId = "myPlan";

        PricePlan pricePlan = mock(PricePlan.class);
        when(pricePlan.getPlanName()).thenReturn(planId);

        List<PricePlan> pricePlans = Collections.singletonList(pricePlan);
        MeterReadingService meterReadingService = mock(MeterReadingService.class);

        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);

        // when
        BigDecimal actualConsumptionCostSince = pricePlanService.getConsumptionCostSince(startDate, meterId, planId);

        // then
        assertEquals(BigDecimal.ZERO, actualConsumptionCostSince);

    }

    @Test
    public void shouldReturnCostForInRangeMeterReadings() {
        BigDecimal expectedCost = BigDecimal.TEN;
        LocalDate startDate = LocalDate.now().minusDays(1);
        PricePlan pricePlan = mock(PricePlan.class);
        when(pricePlan.getPlanName()).thenReturn("PLAN-ID");
        List<PricePlan> pricePlans = Collections.singletonList(pricePlan);
        when(pricePlan.getUnitRate()).thenReturn(expectedCost);
        MeterReadingService meterReadingService = mock(MeterReadingService.class);
        int twoDaysAgo = 60 * 60 * 24 * 2;
        ElectricityReading outOfRangeReading = new ElectricityReading(Instant.now().minusSeconds(twoDaysAgo), BigDecimal.ONE);
        ElectricityReading inRangeReading = new ElectricityReading(Instant.now().minusSeconds(1), BigDecimal.ONE);
        List<ElectricityReading> readings = Arrays.asList(outOfRangeReading, inRangeReading);
        when(meterReadingService.getReadings("METER-ID")).thenReturn(Optional.of(readings));
        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);

        BigDecimal actualCost = pricePlanService.getConsumptionCostSince(startDate, "METER-ID", "PLAN-ID");

        assertEquals(expectedCost, actualCost);
    }

}