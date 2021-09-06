package com.sarf;

import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerWithKey {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		String bootstrapServer = "localhost:9092";

		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// Create a producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

		while (true) {
			String topicname = "pushMessageTopic";
			for (int i = 0; i < 1; i = i + 1) {
				String value = "Time="  + ",Data= SQ & EK Message for client"  ;
				String key = "Id_" + i;

				// Create a producer record
				ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicname, key, value);

				// Send
				producer.send(record, new Callback() {

					public void onCompletion(RecordMetadata meta, Exception ex) {
						if (ex == null) {
							// Record was successfully sent
							StringBuilder sb = new StringBuilder();
							sb.append(Calendar.getInstance().getTime()).append("-").append(" Topic Name =")
									.append(meta.topic()).append(" Partition =").append(meta.partition())
									.append(" Offset= ").append(meta.offset());
							System.out.println("Sending metadata Topic Name " + sb.toString());
						} else {
							System.out.println("ERROR!! WHILE PRODUCING");
						}

					}
				}).get(); // block the .send to make it synchronous call- Don't
							// use it in production
			}
			Thread.sleep(1000);
		}
		// producer.flush();
		// producer.close();

	}

}
