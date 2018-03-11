package com.mfp;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MFPKafkaProducer {
	KafkaProducer<String, String> producer = null;
	Properties props = new Properties();

	public MFPKafkaProducer() {
		props = new Properties();
		props.put("bootstrap.servers", "kafka1:9092,kafka2:9093,kafka3:9094");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<String, String>(props);
	}

	public void sendAddChatEvent(JSONObject data) {
		sendEvent("addChatEvent", data);
	}

	public void sendRemoveChatEvent(JSONArray data) {
		sendEvent("removeChatEvent", data);
	}

	public void sendAllActiveChatsEvent(JSONArray data) {
		sendEvent("allActiveChatsEvent", data);
	}

	private void sendEvent(String key, Object data) {
		System.out.println("Sending " + key);
		producer.send(new ProducerRecord<String, String>(Main.kafkaTopic_chat_events, key,
				getEventData(data)));
	}

	private String getEventData(Object data) {
		JSONObject jsonEvent = new JSONObject();
		try {
			jsonEvent.put("sender", Main.CONTAINER);
			jsonEvent.put("data", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonEvent.toString();
	}

	public void close() {
		producer.close();
	}
}