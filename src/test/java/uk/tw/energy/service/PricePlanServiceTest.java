package uk.tw.energy.service;

import jdk.vm.ci.meta.Local;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
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
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        String meterId = "myMeter";
        String planId = "myPlan";

        LocalDateTime currentDateTime = LocalDateTime.of(2021, 12, 15, 1, 1, 1);
        LocalDateTimeFactory localDateTimeFactory = mock(LocalDateTimeFactory.class);
        when(localDateTimeFactory.now()).thenReturn(currentDateTime);

        LocalDateTime meterReadingDateTime = LocalDateTime.of(2021, 12, 1, 1, 1);
        Instant meterReadingInstant = Instant.ofEpochSecond(meterReadingDateTime.toEpochSecond(ZoneOffset.ofHours(0)));

        PricePlan pricePlan = mock(PricePlan.class);
        when(pricePlan.getPlanName()).thenReturn(planId);
        List<PricePlan> pricePlans = Collections.singletonList(pricePlan);

        ElectricityReading electricityReading = new ElectricityReading(meterReadingInstant, BigDecimal.ONE);
        MeterReadingService meterReadingService = mock(MeterReadingService.class);
        when(meterReadingService.getReadings(meterId)).thenReturn(Optional.of(Collections.singletonList(electricityReading)));

        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService, localDateTimeFactory);

        // when
        BigDecimal actualConsumptionCostSince = pricePlanService.getConsumptionCostSince(startDate, meterId, planId);

        // then
        assertEquals(BigDecimal.ZERO, actualConsumptionCostSince);

    }

    @Test
    public void shouldReturnCostForInRangeMeterReadings() {

//        TODO - for reference, example doing date math with the Instant class
//        Instant oneWeekAgo = Instant.now().minus(1, ChronoUnit.WEEKS);

        BigDecimal expectedCost = BigDecimal.valueOf(5000L);
        LocalDate startDate = LocalDate.now().minusDays(1);

        PricePlan pricePlan = mock(PricePlan.class);
        when(pricePlan.getPlanName()).thenReturn("PLAN-ID");
        when(pricePlan.getUnitRate()).thenReturn(BigDecimal.ONE);

        List<PricePlan> pricePlans = Collections.singletonList(pricePlan);

        MeterReadingService meterReadingService = mock(MeterReadingService.class);
        int twoDaysAgo = 60 * 60 * 24 * 2;
        ElectricityReading outOfRangeReading = new ElectricityReading(Instant.now().minusSeconds(twoDaysAgo), BigDecimal.valueOf(1000L));
        ElectricityReading inRangeReading = new ElectricityReading(Instant.now().minusSeconds(1), BigDecimal.valueOf(5000L));
        List<ElectricityReading> readings = Arrays.asList(outOfRangeReading, inRangeReading);
        when(meterReadingService.getReadings("METER-ID")).thenReturn(Optional.of(readings));

        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);

        BigDecimal actualCost = pricePlanService.getConsumptionCostSince(startDate, "METER-ID", "PLAN-ID");

        assertEquals(expectedCost, actualCost);
    }

}