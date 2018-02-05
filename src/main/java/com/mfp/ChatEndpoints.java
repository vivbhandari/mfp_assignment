package com.mfp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Root resource (exposed at "v1" path)
 */
@Path("v1")
public class ChatEndpoints {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("test")
	public String test() {
		return "test";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("chats/{username}")
	public Response getChats(@PathParam("username") String username) {
		JSONArray jsonOutputArray = new JSONArray();
		try {
			List<Chat> chats = ChatCache.getInstance().getChats(username);
			JSONArray jsonEventArray = new JSONArray();
			for (Chat chat : chats) {
				jsonEventArray.put(chat.getJSON());
			}
			Main.mfpKafkaProducer.sendRemoveChatEvent(jsonEventArray);

			for (Chat chat : chats) {
				System.out.println(chat);
				// only need id and text in output
				JSONObject outputChatJson = new JSONObject();
				outputChatJson.put("id", chat.getId());
				outputChatJson.put("text", chat.getText());
				jsonOutputArray.put(outputChatJson);
			}
			System.out.println(jsonOutputArray);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(getJsonErrorOutput(e.getMessage())).build();
		}
		return Response.status(200).entity(jsonOutputArray.toString()).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("chat/{id}")
	public Response getChat(@PathParam("id") Long id) {
		JSONObject jsonObject = new JSONObject();
		try {
			Chat chat = ChatCache.getInstance().getChat(id);
			if (chat == null) {
				chat = new QueryEngine().getChat(id);
			}
			if (chat != null) {
				jsonObject.put("username", chat.getUsername());
				jsonObject.put("text", chat.getText());
				jsonObject.put("expiration_date",
						getDateStr(chat.getExpiryTimestamp()));
			} else {
				return Response.status(Response.Status.EXPECTATION_FAILED)
						.entity(getJsonErrorOutput("Chat not found")).build();
			}

			System.out.println(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(getJsonErrorOutput(e.getMessage())).build();
		}
		return Response.status(200).entity(jsonObject.toString()).build();
	}

	private String getDateStr(long timestamp) {
		Date date = new Date(timestamp);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("chat")
	public Response createChat(String chatInput) {
		System.out.println(String.format("Served by %s \n", Main.CONTAINER));

		JSONObject jsonOutput = new JSONObject();
		try {
			JSONObject jsonInput = new JSONObject(chatInput);
			String username = jsonInput.getString("username");
			String text = jsonInput.getString("text");
			int timeout = jsonInput.optInt("timeout");

			if (timeout == 0) {
				timeout = 60;
			}
			Chat chat = new Chat(username, text,
					System.currentTimeMillis() + timeout * 1000);
			long id = new QueryEngine().addChat(chat);
			chat.setId(id);
			ChatCache.getInstance().addChat(chat);
			Main.mfpKafkaProducer.sendAddChatEvent(chat.getJSON());
			jsonOutput.put("id", id);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(getJsonErrorOutput(e.getMessage())).build();
		}
		return Response.status(201).entity(jsonOutput.toString()).build();
	}

	private String getJsonErrorOutput(String errorMessage) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("error", errorMessage);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
