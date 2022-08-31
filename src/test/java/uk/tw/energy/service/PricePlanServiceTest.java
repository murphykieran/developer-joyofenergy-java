package uk.tw.energy.service;

import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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

}