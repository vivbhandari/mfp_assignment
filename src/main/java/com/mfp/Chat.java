package com.mfp;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Chat {
	private long id;
	private String username;
	private String text;
	private long expiryTimestamp;

	public Chat(long id, String username, String text, long expiryTimestamp) {
		super();
		this.id = id;
		this.username = username;
		this.text = text;
		this.expiryTimestamp = expiryTimestamp;
	}

	public Chat(String username, String text, long expiryTimestamp) {
		this(-1, username, text, expiryTimestamp);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getExpiryTimestamp() {
		return expiryTimestamp;
	}

	public void setExpiryTimestamp(long expiryTimestamp) {
		this.expiryTimestamp = expiryTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chat other = (Chat) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public JSONObject getJSON() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", this.id);
			jsonObject.put("username", this.username);
			jsonObject.put("text", this.text);
			jsonObject.put("expiryTimestamp", this.expiryTimestamp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	@Override
	public String toString() {
		return "Chat [id=" + id + ", username=" + username + ", text=" + text + ", expiryTimestamp="
				+ expiryTimestamp + "]";
	}
}
