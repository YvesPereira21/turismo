package io.turismo.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @InjectMocks
    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        activityService = new ActivityService();
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldInstantiateService() {
        assertNotNull(activityService);
    }

    // -----------------------UNHAPPY PATH------------------------------
}
