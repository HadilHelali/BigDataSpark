package tn.insat.gl4.kafka;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class ApiReceiverConsumer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "fire-calls";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "fire-call-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RangeAssignor");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton(String.valueOf(TOPIC)));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100).toMillis());
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Received message: " + record.value());
                    String str = record.value() ;

                    // Establishing the connection with mongodb :
                    System.out.println("configuring MongoDB and establishing connection ...");
                    MongoClient mongoClient = new MongoClient("localhost", 27017);
                    System.out.println("retrieving the database then the collection ...");
                    MongoDatabase database = mongoClient.getDatabase("fire-calls");
                    MongoCollection<Document> collection = database.getCollection("fire-calls-spark");

                    System.out.println("inserting the records ...");
                    String[] lines = str.split("\n");
                    for (String line : lines ){
                        String[] values = line.split("\\|");
                        if (values.length >= 4) {
                        Document document = new Document();
                        UUID uuid = UUID.randomUUID();
                        document.append("_id",uuid);
                        document.append("IncidentNumber", values[0]);
                        document.append("Address", values[1]);
                        document.append("Type", values[2]);
                        document.append("Datetime", values[3]);
                        collection.insertOne(document); }
                    }
                    System.out.println("records inserted ...");
                    mongoClient.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
