/**
 * 
 */
package org.jobjects.jaas.persistance.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import org.jobjects.jaas.persistance.UserRoleInformation;

/**
 * @author Mickael
 * 
 */
public class UserRoleInformationJDBC implements UserRoleInformation {

	private static Logger LOGGER = Logger
			.getLogger(UserRoleInformationJDBC.class.getName());

	private String dBUser=null;
	private String dBPassword=null;
	private String dBUrl=null;
	private String dBDriver=null;
	private String userQuery=null;
	private String roleQuery=null; 
	
	public UserRoleInformationJDBC() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jobjects.jaas.persistance.UserRoleInformation#init(Map<String,
	 * ?> options)
	 */
	@Override
	public boolean init(Map<String, ?> options) {
		dBUser = (String) options.get("dbUser");
		dBPassword = (String) options.get("dbPassword");
		dBUrl = (String) options.get("dbURL");
		dBDriver = (String) options.get("dbDriver");

		userQuery = (String) options.get("userQuery");
		roleQuery = (String) options.get("roleQuery");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jobjects.jaas.persistance.UserRoleInformation#isValidUser(java.lang
	 * .String, char[])
	 */
	@Override
	public boolean isValidUser(String login, char[] password) {
		boolean returnValue=false;
		try {
			Connection connection =getConnection();
			try {
				PreparedStatement ptmt = connection.prepareStatement(userQuery);
				try {
					ptmt.setString(1, login);
					ptmt.setString(2, new String(password));
					ResultSet rs = ptmt.executeQuery();
					try {
						if (rs.next()) {
							returnValue = true;
						}
					} finally {
						rs.close();
					}
				} finally {
					ptmt.close();
				}
			} finally {
				connection.close();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error when loading user from the database", e);
			returnValue=false;
		}
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jobjects.jaas.persistance.UserRoleInformation#getRoles()
	 */
	@Override
	public List<String> getRoles(String login) {
		List<String> returnValue = new ArrayList<String>();
		try {
			Connection connection =getConnection();
			try {
				PreparedStatement ptmt = connection.prepareStatement(roleQuery);
				try {
					ptmt.setString(1, login);
					ResultSet rs = ptmt.executeQuery();
					try {
						while (rs.next()) {
							returnValue.add(rs.getString("rolename"));
						}
					} finally {
						rs.close();
					}
				} finally {
					ptmt.close();
				}
			} finally {
				connection.close();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error when loading role of user from the database", e);
		}
		return returnValue;
	}

	/**
	 * Returns JDBC connection
	 * 
	 * @return
	 * @throws LoginException
	 */
	private Connection getConnection() throws SQLException {
		Connection connection = null;
		try {
			// loading driver
			Class.forName(dBDriver).newInstance();
			connection = DriverManager.getConnection(dBUrl, dBUser, dBPassword);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error when creating database connection", e);
			throw new SQLException(e);
		}
		return connection;
	}

}
