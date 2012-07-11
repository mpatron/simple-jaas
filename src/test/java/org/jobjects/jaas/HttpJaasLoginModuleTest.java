/**
 * 
 */
package org.jobjects.jaas;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * @author Mickael
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpJaasLoginModuleTest {

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
	HttpJaasLoginModule instance= new HttpJaasLoginModule (); 
	@Mock private CallbackHandler callbackHandler;
	private Subject subject = new Subject();
	@Mock private Map<String, ?> sharedState;
	private Map<String, String> options;

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.HttpJaasLoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)}
	 * .
	 */
	@Test
	public void testInitialize() {

		try {
			doAnswer(new Answer<Void>() {
				  public Void answer(InvocationOnMock invocation) {
				      
					  Object[] args = invocation.getArguments();
				      Callback[] callbacks = (Callback[])args[0];
				      ((NameCallback)callbacks[0]).setName("mon nom"); 
				      ((PasswordCallback)callbacks[1]).setPassword("password".toCharArray());
				      
				      //Mock mock = (Mock) invocation.getMock();
				      return null;
				  }}).when(callbackHandler).handle(any(Callback[].class));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedCallbackException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		options = new HashMap<String, String>();
		options.put("persistanceMode", "ONLY_TRUE");
		options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
		options.put("dbUser", "sa");
		options.put("dbPassword", "manager");
		options.put("userQuery", "select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
		options.put("roleQuery", "select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
		options.put("debug", "true");
		
		instance.initialize(subject, callbackHandler, sharedState, options);
	}

	/**
	 * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#login()}.
	 */
	@Test(timeout=1000)
	public void testLoginFalse() {

		try {
			doAnswer(new Answer<Void>() {
				  public Void answer(InvocationOnMock invocation) {
				      
					  Object[] args = invocation.getArguments();
				      Callback[] callbacks = (Callback[])args[0];
				      ((NameCallback)callbacks[0]).setName("mon nom"); 
				      ((PasswordCallback)callbacks[1]).setPassword("password".toCharArray());
				      
				      //Mock mock = (Mock) invocation.getMock();
				      return null;
				  }}).when(callbackHandler).handle(any(Callback[].class));

			options = new HashMap<String, String>();
			options.put("persistanceMode", "ONLY_TRUE");
			options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
			options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
			options.put("dbUser", "sa");
			options.put("dbPassword", "manager");
			options.put("userQuery", "select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
			options.put("roleQuery", "select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
			options.put("debug", "true");
			
			instance.initialize(subject, callbackHandler, sharedState, options);		
			Assert.assertFalse(instance.login()) ;			
		} catch (Exception e1) {			
			e1.printStackTrace();
			fail(e1.getLocalizedMessage());
		}
	}

	/**
	 * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#login()}.
	 */
	@Test(timeout=1000)
	public void testLoginTrue() {

		try {
			doAnswer(new Answer<Void>() {
				  public Void answer(InvocationOnMock invocation) {
				      
					  Object[] args = invocation.getArguments();
				      Callback[] callbacks = (Callback[])args[0];
				      ((NameCallback)callbacks[0]).setName("mon nom"); 
				      ((PasswordCallback)callbacks[1]).setPassword("password".toCharArray());
				      
				      //Mock mock = (Mock) invocation.getMock();
				      return null;
				  }}).when(callbackHandler).handle(any(Callback[].class));

			options = new HashMap<String, String>();
			options.put("persistanceMode", "ONLY_TRUE");
			options.put("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver");
			options.put("dbURL", "jdbc:derby:memory:MyDerbyDB;upgrade=true");
			options.put("dbUser", "sa");
			options.put("dbPassword", "manager");
			options.put("userQuery", "select username from MyDerbyDB.secu_user u where u.username=? and u.password=?");
			options.put("roleQuery", "select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?");
			options.put("debug", "true");
			
			instance.initialize(subject, callbackHandler, sharedState, options);		
			Assert.assertFalse(instance.login()) ;			
		} catch (Exception e1) {			
			e1.printStackTrace();
			fail(e1.getLocalizedMessage());
		}
	}
	
	
	/**
	 * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#commit()}.
	 */
	@Test
	public void testCommit() {

	}

	/**
	 * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#abort()}.
	 */
	@Test
	public void testAbort() {
	}

	/**
	 * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#logout()}.
	 */
	@Test
	public void testLogout() {
	}

}
