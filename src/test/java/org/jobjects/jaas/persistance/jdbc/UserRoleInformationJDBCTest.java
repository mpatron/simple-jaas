/**
 * 
 */
package org.jobjects.jaas.persistance.jdbc;

import static org.junit.Assert.*;

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

			Connection conn = DriverManager.getConnection("jdbc:derby:memory:MyDerbyDB", p);
			
			{
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE MyDerbyDB.MyTable (";
				sql += " MonChampsTexte VARCHAR(6) NOT NULL,";
				sql += " MonChampsChar CHAR(2) NOT NULL,";
				sql += " MonChampsDate DATE,";
				sql += " MonChampsDateTime TIMESTAMP,";
				sql += " MonChampsDecimal DOUBLE";
				sql += " )";
				stmt.execute(sql);
				stmt.execute("ALTER TABLE MyDerbyDB.MyTable ADD PRIMARY KEY (MonChampsTexte, MonChampsChar)");
				stmt.close();
			}
			
			{
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE MyDerbyDB.secu_user (";
				sql += " username VARCHAR(255) NOT NULL,";
				sql += " password VARCHAR(255),";
				sql += " MonChampsDateTime TIMESTAMP";
				sql += " )";
				stmt.execute(sql);
				stmt.execute("ALTER TABLE MyDerbyDB.secu_user ADD PRIMARY KEY (username)");
				stmt.execute("insert into MyDerbyDB.secu_user (username, password) values ('myName', 'myPassword')");
				stmt.close();
			}

			{
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE MyDerbyDB.secu_user_role (";
				sql += " username VARCHAR(255) NOT NULL,";
				sql += " rolename VARCHAR(255) NOT NULL,";
				sql += " MonChampsDateTime TIMESTAMP";
				sql += " )";
				stmt.execute(sql);
				stmt.execute("ALTER TABLE MyDerbyDB.secu_user_role ADD PRIMARY KEY (username, rolename)");
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
			 DriverManager.getConnection("jdbc:derby:memory:MyDerbyDB;shutdown=true");
			//DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (Exception ignored) {
			ignored.printStackTrace();
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
	 * Test method for {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#UserRoleInformationJDBC()}.
	 */
	@Test
	public void testUserRoleInformationJDBC() {
		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertNotNull(instance);
	}

	/**
	 * Test method for {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#init(java.util.Map)}.
	 */
	@Test
	public void testInit() {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
		options.put("dbUser", "sa");
		options.put("dbPassword", "manager");
		options.put("userQuery", "select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
		options.put("roleQuery", "select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
		options.put("debug", "true");

		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertTrue(instance.init(options));
		
	}

	/**
	 * Test method for {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#isValidUser(java.lang.String, char[])}.
	 */
	@Test
	public void testIsValidUser() {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
		options.put("dbUser", "sa");
		options.put("dbPassword", "manager");
		options.put("userQuery", "select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
		options.put("roleQuery", "select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
		options.put("debug", "true");

		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertTrue(instance.init(options));
		Assert.assertTrue(instance.isValidUser("myName", "myPassword".toCharArray()));
	}

	/**
	 * Test method for {@link org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC#getRoles(java.lang.String)}.
	 */
	@Test
	public void testGetRoles() {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
		options.put("dbUser", "sa");
		options.put("dbPassword", "manager");
		options.put("userQuery", "select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
		options.put("roleQuery", "select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
		options.put("debug", "true");

		UserRoleInformationJDBC instance = new UserRoleInformationJDBC();
		Assert.assertTrue(instance.init(options));		
		Assert.assertTrue(instance.getRoles("myName").size()>0);
	}

}
