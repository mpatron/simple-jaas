package org.jobjects.jaas;

import static org.junit.Assert.fail;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoginContextTest {
	
	@BeforeClass
	public static void beforeClass() {
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

	@AfterClass
	public static void afterClass() {
		try {
			// DriverManager.getConnection("jdbc:derby:memory:MyDerbyDB;shutdown=true");
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (Exception ignored) {
		}
	}

	@Before
	public void setUp() throws Exception {

		/**
		 * 		 -Djava.security.auth.login.config=
		 *      file://C:/programs/eclipse-jee-juno-win32-x86_64/workspace/jaas/src/test/resources/login.config
		 */
		
		System.setProperty(
				"java.security.auth.login.config",
				""+Class.class.getResource("/login.config"));
		
	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.HttpJaasLoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)}
	 * .
	 */
	@Test
	public void testLogin() {
		
//		Configuration configuration = Configuration.getConfiguration();
//		Assert.assertEquals("", HttpJaasLoginModule.class.getName(), configuration.getProvider().getClass().getName());
		try {
			String name = "myName";
			String password = "myPassword";
			LoginContext lc = new LoginContext("SimpleJaaS",
					new TestCallbackHandler(name, password));
			lc.login();
			
			System.out.println(""+lc.getClass().getResource("/login.config"));
			
			Assert.assertNotNull(lc.getSubject());
			Iterator<Principal> principals=lc.getSubject().getPrincipals().iterator();
			boolean flag=false;
			while (principals.hasNext()) {
				Principal principal = (Principal) principals.next();
				if(name.equals(principal.getName())) {
					flag=true;
				}
			}
			Assert.assertTrue(flag);
		} catch (LoginException e) {
			e.printStackTrace();
			fail();
		}
	}

}
