package org.jobjects.jaas;

import static org.junit.Assert.fail;

import java.security.Principal;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.security.auth.login.Configuration;
import javax.security.auth.login.Configuration.Parameters;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoginContextTest {
	private static Logger LOGGER = Logger.getLogger(LoginContextTest.class.getName());
	
	@BeforeClass
	public static void beforeClass() {
		/**
		 * 		 -Djava.security.auth.login.config=
		 *      file://C:/programs/eclipse-jee-juno-win32-x86_64/workspace/jaas/src/test/resources/login.config
		 */
		
		System.setProperty("java.security.auth.login.config", ""+ClassLoader.getSystemResource("login.config"));
		System.setProperty("java.util.logging.config.file", ""+ClassLoader.getSystemResource("logging.properties").getPath()
				);
	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.HttpJaasLoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)}
	 * .
	 */
	@Test
	public void testLogin() {
		LOGGER.finest("test JAAS du callback de login.");
		try {
			Assert.assertNotNull(System.getProperty("java.security.auth.login.config"));
			Assert.assertNotNull(System.getProperty("java.util.logging.config.file"));
			System.out.println(""+System.getProperty("java.security.auth.login.config"));
			System.out.println(""+System.getProperty("java.util.logging.config.file"));
			Parameters parameters=Configuration.getConfiguration().getParameters();
			System.out.println("parameters="+parameters);
			String name = "myName";
			String password = "myPassword";
			LoginContext lc = new LoginContext("SimpleJaas_ONLY_TRUE", new TestCallbackHandler(name, password));
			lc.login();
			
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
