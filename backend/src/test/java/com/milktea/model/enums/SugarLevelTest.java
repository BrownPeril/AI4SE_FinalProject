package com.milktea.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SugarLevelTest {

    @Test
    void shouldContainAllFiveValues() {
        assertEquals(5, SugarLevel.values().length);
    }

    @Test
    void shouldContainExpectedValues() {
        assertNotNull(SugarLevel.valueOf("无糖"));
        assertNotNull(SugarLevel.valueOf("微糖"));
        assertNotNull(SugarLevel.valueOf("半糖"));
        assertNotNull(SugarLevel.valueOf("少糖"));
        assertNotNull(SugarLevel.valueOf("全糖"));
    }

    @Test
    void shouldThrowForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> SugarLevel.valueOf("超糖"));
    }
}
