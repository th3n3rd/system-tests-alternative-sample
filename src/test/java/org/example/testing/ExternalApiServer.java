package org.example.testing;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.net.ServerSocket;
import lombok.SneakyThrows;

class ExternalApiServer {

    private final WireMockServer server;

    ExternalApiServer() {
        server = new WireMockServer(options().port(randomPort()));
    }

    public String urlFor(String path) {
        return server.url(path);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public void givenThisPersonExists(String firstName, String lastName) {
        server.stubFor(
            WireMock
                .get("/foo")
                .willReturn(
                    WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            [
                                {
                                    "firstName": "%s",
                                    "lastName": "%s"
                                }
                            ]
                            """.formatted(firstName, lastName)
                        )
                )
        );
    }

    @SneakyThrows
    private int randomPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        }
    }
}
