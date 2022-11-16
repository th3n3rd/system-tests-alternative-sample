package org.example.testing;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
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
            .assertThat()
            .statusCode(200)
            .assertThat()
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
