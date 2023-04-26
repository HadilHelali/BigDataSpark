package tn.insat.gl4.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.quartz.JobExecutionException;
import tn.insat.gl4.ApiReceiver;

import java.util.Properties;

public class ApiReceiverProducer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "fire-calls";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            while (true) {
                ApiReceiver apiReceiver = new ApiReceiver();
                apiReceiver.execute(null);
                String jsonString = apiReceiver.getDataList();
                System.out.println(jsonString);
                producer.send(new ProducerRecord<>(TOPIC, "fire-call", jsonString));
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            System.out.println(e);
            e.printStackTrace();
        } catch (JobExecutionException e) {
            System.out.println(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
