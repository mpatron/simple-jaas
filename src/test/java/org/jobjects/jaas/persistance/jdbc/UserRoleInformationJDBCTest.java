/**
 * 
 */
package org.jobjects.jaas.persistance.jdbc;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Mickael
 * 
 */
public class UserRoleInformationJDBCTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			Class.forName(driver).newInstance();
			Properties p = new Properties();
			p.setProperty("user", "sa");
			p.setProperty("password", "manager");
			p.setProperty("create", "true");

			Connection conn = DriverManager.getConnection(
					"jdbc:derby:memory:MyDerbyDB", p);

			{
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE MYDERBYDB.MYTABLE (";
				sql += " MONCHAMPSTEXTE VARCHAR(6) NOT NULL,";
				sql += " MONCHAMPSCHAR CHAR(2) NOT NULL,";
				sql += " MONCHAMPSDATE DATE,";
				sql += " MONCHAMPSDATETIME TIMESTAMP,";
				sql += " MONCHAMPSDECIMAL DOUBLE";
				sql += " )";
				stmt.execute(sql);
				stmt.execute("ALTER TABLE MYDERBYDB.MYTABLE ADD PRIMARY KEY (MONCHAMPSTEXTE, MONCHAMPSCHAR)");
				stmt.close();
			}

			{
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE MYDERBYDB.SECU_USER (";
				sql += " USERNAME VARCHAR(255) NOT NULL,";
				sql += " PASSWORD VARCHAR(255),";
				sql += " MONCHAMPSDATETIME TIMESTAMP";
				sql += " )";
				stmt.execute(sql);
				stmt.execute("ALTER TABLE MYDERBYDB.SECU_USER ADD PRIMARY KEY (username)");
				stmt.execute("INSERT INTO MYDERBYDB.SECU_USER (USERNAME, PASSWORD) VALUES ('myName', 'myPassword')");
				stmt.close();
			}

			{
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE MYDERBYDB.SECU_USER_ROLE (";
				sql += " USERNAME VARCHAR(255) NOT NULL,";
				sql += " ROLENAME VARCHAR(255) NOT NULL,";
				sql += " MONCHAMPSDATETIME TIMESTAMP";
				sql += " )";
				stmt.execute(sql);
				stmt.execute("ALTER TABLE MyDerbyDB.secu_user_role ADD PRIMARY KEY (username, rolename)");
				stmt.execute("INSERT INTO MYDERBYDB.SECU_USER_ROLE (USERNAME, ROLENAME) VALUES ('myName', 'tomcat')");
				stmt.execute("INSERT INTO MYDERBYDB.SECU_USER_ROLE (USERNAME, ROLENAME) VALUES ('myName', 'admin')");
				stmt.execute("INSERT INTO MYDERBYDB.SECU_USER_ROLE (USERNAME, ROLENAME) VALUES ('myName', 'root')");
				stmt.execute("INSERT INTO MYDERBYDB.SECU_USER_ROLE (USERNAME, ROLENAME) VALUES ('myName', 'dieu')");
				stmt.close();
			}

			final ResultSet tables = conn.getMetaData().getTables(null, null,
					"%", new String[] { "TABLE" });
			List<String> tableNames = new ArrayList<String>();
			while (tables.next()) {
				tableNames.add(tables.getString("TABLE_NAME").toLowerCase());
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			// DriverManager.getConnection("jdbc:derby:memory:MyDerbyDB;shutdown=true");
			System.out.println("Extinction de Derby");
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (Exception ignored) {
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#UserRoleInformationJDBC()}
	 * .
	 */
	@Test
	public void testUserRoleInformationJDBC() {
		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertNotNull(instance);
	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#init(java.util.Map)}
	 * .
	 */
	@Test
	public void testInit() {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
		options.put("dbUser", "sa");
		options.put("dbPassword", "manager");
		options.put(
				"userQuery",
				"select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
		options.put(
				"roleQuery",
				"select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
		options.put("debug", "true");

		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertTrue(instance.init(options));

	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#isValidUser(java.lang.String, char[])}
	 * .
	 */
	@Test
	public void testIsValidUserTrue() {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
		options.put("dbUser", "sa");
		options.put("dbPassword", "manager");
		options.put(
				"userQuery",
				"select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
		options.put(
				"roleQuery",
				"select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
		options.put("debug", "true");

		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertTrue(instance.init(options));
		Assert.assertTrue(instance.isValidUser("myName",
				"myPassword".toCharArray()));
	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#isValidUser(java.lang.String, char[])}
	 * .
	 */
	@Test
	public void testIsValidUserFalse() {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
		options.put("dbUser", "sa");
		options.put("dbPassword", "manager");
		options.put(
				"userQuery",
				"select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
		options.put(
				"roleQuery",
				"select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
		options.put("debug", "true");

		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertTrue(instance.init(options));
		Assert.assertFalse(instance.isValidUser("myNameFalse",
				"myPassword".toCharArray()));
	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#getRoles(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetRoles() {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
		options.put("dbUser", "sa");
		options.put("dbPassword", "manager");
		options.put(
				"userQuery",
				"select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
		options.put(
				"roleQuery",
				"select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
		options.put("debug", "true");

		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertTrue(instance.init(options));
		Assert.assertTrue(instance.getRoles("myName").size() > 0);
	}

}
