package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricePlanService {

    private final List<PricePlan> pricePlans;
    private final MeterReadingService meterReadingService;
    private final LocalDateTimeFactory localDateTimeFactory;

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
        this(pricePlans, meterReadingService, new LocalDateTimeFactory());
    }

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService, LocalDateTimeFactory localDateTimeFactory) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
        this.localDateTimeFactory = localDateTimeFactory;
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));
    }

    public BigDecimal getConsumptionCostSince(LocalDate startDate, String meterId, String planId) {

        final LocalDateTime now = this.localDateTimeFactory.now();
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(0, 0,0));
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(meterId);

        Optional<PricePlan> pricePlan = this.pricePlans.stream().filter((PricePlan plan) -> { return plan.getPlanName().equals(planId); }).findFirst();

        if (electricityReadings.isPresent() && pricePlan.isPresent()) {
            electricityReadings.get().stream().filter((ElectricityReading reading) -> {
                LocalDateTime readingTime = LocalDateTime.from(reading.getTime());
                return readingTime.isAfter(startDateTime);
            }).collect(Collectors.toList());

            return calculateCost(electricityReadings.get(), pricePlan.get());
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

        BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
        return averagedCost.multiply(pricePlan.getUnitRate());
    }

    private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::getReading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
        ElectricityReading first = electricityReadings.stream()
                .min(Comparator.comparing(ElectricityReading::getTime))
                .get();
        ElectricityReading last = electricityReadings.stream()
                .max(Comparator.comparing(ElectricityReading::getTime))
                .get();

        return BigDecimal.valueOf(Duration.between(first.getTime(), last.getTime()).getSeconds() / 3600.0);
    }

}
