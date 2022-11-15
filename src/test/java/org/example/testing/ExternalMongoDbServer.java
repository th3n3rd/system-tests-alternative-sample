package org.example.testing;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import java.util.UUID;
import lombok.SneakyThrows;
import org.bson.Document;

class ExternalMongoDbServer {

    private final MongodExecutable server;
    private final int port;
    private final String uniqueDatabase;

    @SneakyThrows
    public ExternalMongoDbServer() {
        uniqueDatabase = UUID.randomUUID().toString();
        port = Network.freeServerPort(Network.getLocalHost());

        var config = MongodConfig.builder()
            .version(Version.Main.V4_4)
            .net(new Net(port, Network.localhostIsIPv6()))
            .build();

        var starter = MongodStarter.getDefaultInstance();
        server = starter.prepare(config);
    }

    @SneakyThrows
    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public MongoCollection<Document> collection(String name) {
        return client().getDatabase(uniqueDatabase).getCollection(name);
    }

    public void givenThisPersonExists(String firstName, String lastName) {
        var people = collection("people");
        people.insertOne(
            new Document()
                .append("firstName", firstName)
                .append("lastName", lastName)
        );
    }

    private MongoClient client() {
        return MongoClients.create(connectionString());
    }

    private String connectionString() {
        return "mongodb://localhost:%s".formatted(port);
    }
}
