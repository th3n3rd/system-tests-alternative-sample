package org.example.testing;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.model.Filters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
class InteractionsWithExternalMongoDbTests {

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
