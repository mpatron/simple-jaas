/**
 * 
 */
package org.jobjects.jaas;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
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

	HttpJaasLoginModule instance= new HttpJaasLoginModule (); 
	@Mock private CallbackHandler callbackHandler;
	private Subject subject = new Subject();
	@Mock private Map<String, ?> sharedState;
	@Mock private Map<String, ?> options;

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.HttpJaasLoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)}
	 * .
	 */
	@Test
	public void testInitialize() {
		instance.initialize(subject, callbackHandler, sharedState, options);
	}

	/**
	 * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#login()}.
	 */
	@Test(/*timeout=1000*/)
	public void testLogin() {
		try {
			doAnswer(new Answer() {
				  public Object answer(InvocationOnMock invocation) {
				      
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
		


		//when(callbackHandler.handle(   (Callback[])anyObject()              )).thenReturn("element");
		instance.initialize(subject, callbackHandler, sharedState, options);
		try {
			instance.login();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
