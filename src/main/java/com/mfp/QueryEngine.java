package com.mfp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryEngine {
	private static String selectChatStr = "Select * FROM chats WHERE id = ?";
	private static String insertChatStr = "INSERT into chats(username, text, expiryTimestamp) values "
			+ "(?, ?, ?)";

	public Chat getChat(long id) {
		Connection sqlConnection = null;
		PreparedStatement selectChatPS = null;
		Chat chat = null;
		try {
			sqlConnection = DBConnectionManager.getInstance().getConnection();
			if (sqlConnection == null) {
				System.out.println("Unable to connect to DB");
			} else {
				selectChatPS = sqlConnection.prepareStatement(selectChatStr);
				selectChatPS.setLong(1, id);
				ResultSet rs = selectChatPS.executeQuery();
				if (rs != null && rs.next()) {
					chat = new Chat(rs.getLong(1), rs.getString(2),
							rs.getString(3), rs.getLong(4));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBConnectionManager.getInstance()
						.returnConnection(sqlConnection);
				if (selectChatPS != null)
					selectChatPS.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return chat;
	}

	public long addChat(Chat chat) {
		Connection sqlConnection = null;
		PreparedStatement insertChatPS = null;
		long id = -1;
		try {
			sqlConnection = DBConnectionManager.getInstance().getConnection();
			if (sqlConnection == null) {
				System.out.println("Unable to connect to DB");
			} else {
				insertChatPS = sqlConnection.prepareStatement(insertChatStr,
						Statement.RETURN_GENERATED_KEYS);
				insertChatPS.setString(1, chat.getUsername());
				insertChatPS.setString(2, chat.getText());
				insertChatPS.setLong(3, chat.getExpiryTimestamp());
				insertChatPS.execute();
				ResultSet rs = insertChatPS.getGeneratedKeys();
				if (rs != null && rs.next()) {
					id = rs.getLong(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBConnectionManager.getInstance()
						.returnConnection(sqlConnection);
				if (insertChatPS != null)
					insertChatPS.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}
}
