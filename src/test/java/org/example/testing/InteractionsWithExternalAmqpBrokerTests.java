package org.example.testing;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InteractionsWithExternalAmqpBrokerTests {

    private ExternalAmqpBroker amqpBroker = new ExternalAmqpBroker();

    @BeforeEach
    void setUp() {
        amqpBroker.start();
    }

    @AfterEach
    void tearDown() {
        amqpBroker.stop();
    }

    @SneakyThrows
    @Test
    void successfulInteraction() {
        amqpBroker.givenThereArePeople();
        var queue = amqpBroker.queue("people", "joined");
        amqpBroker.givenThisPersonJoined("Jon", "Doe");
        var response = amqpBroker.receiver().basicGet(queue, true);
        var message = new String(response.getBody());
        assertThat(message).isEqualTo("Jon Doe");
    }
}
