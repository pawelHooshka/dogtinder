package com.treat.tinderdog.web;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LoginControllerTest {

    @Test
    void testLogin() {
        assertThat(new LoginController().login(), is("login"));
    }

    @Test
    void testGreeting() {
        assertThat(new LoginController().greeting(), is("index"));
    }

    @Test
    void testLogout() {
        assertThat(new LoginController().logout(), is("redirect:/login"));
    }
}
