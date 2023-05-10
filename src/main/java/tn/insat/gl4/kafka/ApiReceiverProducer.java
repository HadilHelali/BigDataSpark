package tn.insat.gl4.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteRecordsResult;
import org.apache.kafka.clients.admin.DeletedRecords;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.quartz.JobExecutionException;
import tn.insat.gl4.ApiReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

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
                System.out.println("sending the results to the topic '"+TOPIC+"' ...");
                producer.send(new ProducerRecord<>(TOPIC, "fire-call", jsonString));
                Thread.sleep(10000);


                // Clear the buffer after the traitement :
                AdminClient adminClient = AdminClient.create(props);

                Map<TopicPartition, RecordsToDelete> recordsToDelete = new HashMap<>();
                TopicPartition topicPartition = new TopicPartition(TOPIC, 0);
                RecordsToDelete deleteRequest = RecordsToDelete.beforeOffset(0L);
                recordsToDelete.put(topicPartition, deleteRequest);
                DeleteRecordsResult result = adminClient.deleteRecords(recordsToDelete);
                Map<TopicPartition, KafkaFuture<DeletedRecords>> lowWatermarks = result.lowWatermarks();
                try {
                    for (Map.Entry<TopicPartition, KafkaFuture<DeletedRecords>> entry : lowWatermarks.entrySet()) {
                        System.out.println(entry.getKey().topic() + " " + entry.getKey().partition() + " " + entry.getValue().get().lowWatermark());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                adminClient.close();



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
