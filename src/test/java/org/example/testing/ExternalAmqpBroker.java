package org.example.testing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.qpid.server.SystemLauncher;
import org.apache.qpid.server.util.urlstreamhandler.classpath.Handler;

class ExternalAmqpBroker {

    private final SystemLauncher server;
    private final ConnectionFactory factory;
    private Channel publisher;
    private Channel receiver;

    static {
        URL.setURLStreamHandlerFactory(protocol ->
            "classpath".equals(protocol) ? new Handler() : null
        );
    }

    @SneakyThrows
    public ExternalAmqpBroker() {
        server = new SystemLauncher();
        factory = new ConnectionFactory();
        factory.setUri(connectionString());
    }

    @SneakyThrows
    public void start() {
        server.startup(systemConfig());
    }

    public void stop() {
        server.shutdown();
    }

    @SneakyThrows
    public String queue(String exchange, String routingKey) {
        var channel = receiver();
        var queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, exchange, routingKey);
        return queue;
    }

    @SneakyThrows
    private Channel publisher() {
        if (publisher == null) {
            publisher = factory.newConnection().createChannel();
        }
        return publisher;
    }

    @SneakyThrows
    public Channel receiver() {
        if (receiver == null) {
            receiver = factory.newConnection().createChannel();
        }
        return receiver;
    }

    @SneakyThrows
    public void givenThereArePeople() {
        publisher().exchangeDeclare("people", "topic", true);
    }

    @SneakyThrows
    public void givenThisPersonJoined(String firstName, String lastName) {
        publisher()
            .basicPublish(
                "people",
                "joined",
                null,
                "%s %s".formatted(firstName, lastName)
                    .getBytes(StandardCharsets.UTF_8)
            );
    }

    private String connectionString() {
        return "amqp://guest:guest@localhost:5672/default";
    }

    @SneakyThrows
    private Map<String, Object> systemConfig() {
        var initialConfig =
            ExternalAmqpBroker.class.getClassLoader()
                .getResource("qpid-config.json");
        return Map.of(
            "type",
            "Memory",
            "initialConfigurationLocation",
            initialConfig.toExternalForm(),
            "startupLoggedToSystemOut",
            true
        );
    }
}
