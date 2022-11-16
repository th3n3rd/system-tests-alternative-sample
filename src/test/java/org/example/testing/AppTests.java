package org.example.testing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AppTests {

    @Test
    void tautology() {
        assertThat(true).isTrue();
    }

}
