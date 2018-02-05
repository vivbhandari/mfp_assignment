package com.mfp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.collections4.map.PassiveExpiringMap;

public class ChatCache {
	private static ChatCache chatCache = null;

	private Hashtable<String, HashSet<Long>> userMap;
	private PassiveExpiringMap<Long, Chat> chatMap;

	public static ChatCache getInstance() {
		if (chatCache == null) {
			chatCache = new ChatCache();
		}

		return chatCache;
	}

	private ChatCache() {
		userMap = new Hashtable<String, HashSet<Long>>();
		chatMap = new PassiveExpiringMap<Long, Chat>(new ChatExpiringPolicy());
	}

	public synchronized void addChat(Chat chat) {
		chatMap.put(chat.getId(), chat);
		HashSet<Long> chatIds = userMap.get(chat.getUsername());
		if (chatIds == null) {
			chatIds = new HashSet<Long>();
			userMap.put(chat.getUsername(), chatIds);
		}
		chatIds.add(chat.getId());
		System.out.println(chatMap);
		System.out.println(userMap);
	}

	public synchronized List<Chat> getChats(String username) {
		System.out.println(chatMap);
		System.out.println(userMap);
		List<Chat> chats = new ArrayList<Chat>();
		if (userMap.containsKey(username)) {
			for (Long id : userMap.remove(username)) {
				if (chatMap.containsKey(id)) {
					Chat chat = chatMap.remove(id);
					if (chat != null) {
						chats.add(chat);
					}
				}
			}
		}
		return chats;
	}

	public synchronized Chat getChat(long id) {
		System.out.println(chatMap);
		System.out.println(userMap);
		Chat chat = null;
		if (chatMap.containsKey(id)) {
			chat = chatMap.get(id);
		}
		return chat;
	}

	public synchronized void removeChat(Chat chat) {
		if (chatMap.containsKey(chat.getId())) {
			chatMap.remove(chat.getId());
		}
		if (userMap.contains(chat.getUsername())) {
			userMap.get(chat.getUsername()).remove(chat.getId());
		}
	}
}

class ChatExpiringPolicy
		implements PassiveExpiringMap.ExpirationPolicy<Long, Chat> {
	private static final long serialVersionUID = 1L;

	@Override
	public long expirationTime(Long key, Chat value) {
		return value.getExpiryTimestamp();
	}
}
