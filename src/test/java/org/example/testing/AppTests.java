package org.example.testing;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AppTests {

    @Test
    void tautology() {
        assertThat(true).isTrue();
    }

    @Nested
    class InteractionWithExternalMongoDb {

        private ExternalMongoDbServer mongoDbServer = new ExternalMongoDbServer();

        @BeforeEach
        void setUp() {
            mongoDbServer.start();
        }

        @AfterEach
        void tearDown() {
            mongoDbServer.stop();
        }

        @Test
        void successfulInteraction() {
            mongoDbServer.givenThisPersonExists("Jon", "Doe");
            var people = mongoDbServer.collection("people");
            var jon = people.find(Filters.eq("firstName", "Jon")).first();
            assertThat(jon.get("lastName")).isEqualTo("Doe");
        }
    }

    @Nested
    class InteractionWithExternalAmqpBroker {

        private ExternalAmqpServer amqpServer = new ExternalAmqpServer();

        @BeforeEach
        void setUp() {
            amqpServer.start();
        }

        @AfterEach
        void tearDown() {
            amqpServer.stop();
        }

        @SneakyThrows
        @Test
        void successfulInteraction() {
            amqpServer.givenThereArePeople();
            var queue = amqpServer.queue("people", "joined");
            amqpServer.givenThisPersonJoined("Jon", "Doe");
            var response = amqpServer.receiver().basicGet(queue, true);
            var message = new String(response.getBody());
            assertThat(message).isEqualTo("Jon Doe");
        }
    }
}
