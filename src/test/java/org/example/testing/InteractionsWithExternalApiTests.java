package org.example.testing;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InteractionsWithExternalApiTests {

    private ExternalApiServer apiServer = new ExternalApiServer();

    @BeforeEach
    void setUp() {
        apiServer.start();
    }

    @AfterEach
    void tearDown() {
        apiServer.stop();
    }

    @Test
    void successfulInteraction() {
        apiServer.givenThisPersonExists("Jon", "Doe");

        when()
            .get(apiServer.urlFor("/foo"))
            .then()
            .statusCode(200)
            .body(
                equalTo(
                    """
                    [
                        {
                            "firstName": "Jon",
                            "lastName": "Doe"
                        }
                    ]
                    """
                )
            );
    }
}
