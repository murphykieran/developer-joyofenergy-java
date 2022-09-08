package uk.tw.energy.service;

import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

//        TODO - for reference, example doing date math with the Instant class
        Instant now = Instant.now();
        LocalDate startDate = LocalDate.now().minusDays(3);
        Instant oneWeekAgo = now.minus(7, ChronoUnit.DAYS);
        Instant twoDaysAgo = now.minus(2, ChronoUnit.DAYS);
        Instant oneDaysAgo = now.minus(1, ChronoUnit.DAYS);

        BigDecimal expectedCost = BigDecimal.valueOf(10800L);


        PricePlan pricePlan = mock(PricePlan.class);
        when(pricePlan.getPlanName()).thenReturn("PLAN-ID");
        when(pricePlan.getUnitRate()).thenReturn(BigDecimal.ONE);

        List<PricePlan> pricePlans = Collections.singletonList(pricePlan);

        MeterReadingService meterReadingService = mock(MeterReadingService.class);

        ElectricityReading outOfRangeReading = new ElectricityReading(oneWeekAgo, BigDecimal.valueOf(1000L));
        ElectricityReading inRangeReading1 = new ElectricityReading(oneDaysAgo, BigDecimal.valueOf(100L));
        ElectricityReading inRangeReading2 = new ElectricityReading(twoDaysAgo, BigDecimal.valueOf(200L));
        List<ElectricityReading> readings = Arrays.asList(outOfRangeReading, inRangeReading1, inRangeReading2);
        when(meterReadingService.getReadings("METER-ID")).thenReturn(Optional.of(readings));

        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);

        BigDecimal actualCost = pricePlanService.getConsumptionCostSince(startDate, "METER-ID", "PLAN-ID");

        assertEquals(expectedCost, actualCost);
    }

}