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
		String sql = userQuery;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, login);
			stmt.setString(2, new String(password));

			rs = stmt.executeQuery();

			if (rs.next()) { // User exist with the given user name and
								// password.
				return true;
			}
		} catch (Exception e) {
			LOGGER.severe("Error when loading user from the database " + e);
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				LOGGER.severe("Error when closing result set." + e);
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				LOGGER.severe("Error when closing statement." + e);
			}
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.severe("Error when closing connection." + e);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jobjects.jaas.persistance.UserRoleInformation#getRoles()
	 */
	@Override
	public List<String> getRoles(String login) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;

		List<String> roleList = new ArrayList<String>();

		try {
			con = getConnection();
			String sql = roleQuery;
			stmt = con.prepareStatement(sql);
			stmt.setString(1, login);

			rs = stmt.executeQuery();

			if (rs.next()) {
				roleList.add(rs.getString("rolename"));
			}
		} catch (Exception e) {
			LOGGER.severe("Error when loading user from the database " + e);
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				LOGGER.severe("Error when closing result set." + e);
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				LOGGER.severe("Error when closing statement." + e);
			}
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.severe("Error when closing connection." + e);
			}
		}
		return roleList;
	}

	/**
	 * Returns JDBC connection
	 * 
	 * @return
	 * @throws LoginException
	 */
	private Connection getConnection() throws LoginException {
		Connection con = null;
		try {
			// loading driver
			Class.forName(dBDriver).newInstance();
			con = DriverManager.getConnection(dBUrl, dBUser, dBPassword);
		} catch (Exception e) {
			LOGGER.severe("Error when creating database connection" + e);
			e.printStackTrace();
		} finally {
		}
		return con;
	}

}
