package com.mfp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConnectionManager {
	private static DBConnectionManager dbConnectionManager = null;
	List<Connection> connectionList = new ArrayList<Connection>();
	int maxOpenConnections = 5; // make it property driven

	private DBConnectionManager() {
	}

	public synchronized static DBConnectionManager getInstance() {
		if (dbConnectionManager == null) {
			dbConnectionManager = new DBConnectionManager();
		}
		return dbConnectionManager;
	}

	private Connection createConnection() {
		Connection sqlConnection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String mysqlHost = Main.LOCALHOST;
			if (!Main.CONTAINER.equals(Main.LOCALHOST)) {
				mysqlHost = "mysql1";
			}
			sqlConnection = DriverManager.getConnection(
					"jdbc:mysql://" + mysqlHost + ":3306/mfp", "root", "root");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sqlConnection;
	}

	public synchronized Connection getConnection() {
		System.out.println("get sql connection");
		Connection sqlConnection = null;
		while (true) {
			if (connectionList.size() < maxOpenConnections) {
				sqlConnection = createConnection();
				connectionList.add(sqlConnection);
				break;
			} else {
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println("Error getting connection");
					e.printStackTrace();
				}
			}
		}
		return sqlConnection;
	}

	public synchronized void returnConnection(Connection sqlConnection) {
		System.out.println("return sql connection");
		if (sqlConnection == null) {
			System.out.println("sqlConnection is null");
		} else if (!connectionList.contains(sqlConnection)) {
			System.out.println("sqlConnection not in the pool");
		} else {
			connectionList.remove(sqlConnection);
			try {
				sqlConnection.close();
			} catch (SQLException e) {
				System.out.println("Error closing connection");
				e.printStackTrace();
			}
			sqlConnection = null;
			notifyAll();
		}
		System.out.println("Open connections = " + connectionList.size());
	}
}
