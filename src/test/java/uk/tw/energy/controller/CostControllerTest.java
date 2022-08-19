package uk.tw.energy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.domain.UsageCost;

import static org.junit.jupiter.api.Assertions.*;

public class CostControllerTest {
    @Test
    public void shouldReturnNoCostWhenNoReadingsExist() {
        CostController costController = new CostController();

        ResponseEntity response = costController.getCost("TEST-SMART-METER");

        UsageCost actualCost = (UsageCost) response.getBody();
        assertEquals(0, actualCost.getCost());
    }
}