package tn.insat.gl4.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class ApiReceiverConsumer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "fire-calls";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "fire-call-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(String.valueOf(Collections.singletonList(TOPIC)));
            while (true) {
                Map<String, ConsumerRecords<String, String>> records = consumer.poll(Duration.ofMillis(100).toMillis());
               /* for (Map<String, ConsumerRecords<String, String>> record : records) {
                    System.out.println(record.get().value());
                } */

                for (Map.Entry<String, ConsumerRecords<String, String>> entry : records.entrySet()) {
                    String key = entry.getKey();
                    ConsumerRecords<String, String> record = entry.getValue();
                        System.out.println(record);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
