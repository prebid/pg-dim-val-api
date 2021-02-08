package org.prebid.pg.dimval.api.controller;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    SoftAssertions softAssertions;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    public void setup() {
        softAssertions = new SoftAssertions();
    }

    @Test
    void adminTest() {
        softAssertions.assertThatCode(() -> adminController.adminTest()).doesNotThrowAnyException();
        softAssertions.assertAll();
    }
}