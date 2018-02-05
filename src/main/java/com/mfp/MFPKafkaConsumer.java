package com.mfp;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MFPKafkaConsumer implements Runnable {
	public boolean stopListening = false;
	public Integer counter = null;

	public MFPKafkaConsumer() {
		new Thread(this).start();
		System.out.println("Started Kafka consumer");
	}

	public void run() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "kafka1:9092");
		props.put("group.id", Main.CONTAINER);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "earliest");
		props.put("key.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(
				props);
		consumer.subscribe(Arrays.asList(Main.kafkaTopic_chat_events));
		while (!stopListening) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				System.out.printf("offset = %d, key = %s, value = %s%n",
						record.offset(), record.key(), record.value());

				try {
					JSONObject jsonEvent = new JSONObject(record.value());
					if (jsonEvent.get("sender").equals(Main.CONTAINER))
						continue;

					System.out.println("Recieved " + record.key());
					switch (record.key()) {
					case "addChatEvent":
						ChatCache.getInstance().addChat(getChatFromJSON(
								jsonEvent.getJSONObject("data")));
						break;
					case "removeChatEvent":
						JSONArray jsonArray = jsonEvent.getJSONArray("data");
						for (int i = 0; i < jsonArray.length(); i++) {
							ChatCache.getInstance().removeChat(getChatFromJSON(
									jsonArray.getJSONObject(i)));
						}
						break;
					default:
						System.out.println("Invalid Kafka event type");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("Invalid Kafka event data");
				}

			}
		}
		consumer.close();
	}

	private Chat getChatFromJSON(JSONObject jsonObject) throws JSONException {
		return new Chat(jsonObject.getLong("id"),
				jsonObject.getString("username"), jsonObject.getString("text"),
				jsonObject.getLong("expiryTimestamp"));
	}

}
