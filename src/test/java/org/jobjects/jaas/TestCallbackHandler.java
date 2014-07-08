package org.jobjects.jaas;

import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class TestCallbackHandler implements CallbackHandler {
	private static Logger LOGGER = Logger.getLogger(TestCallbackHandler.class.getName());
	
	String name;
	String password;

	public TestCallbackHandler(String name, String password) {
		LOGGER.finest("Callback Handler - constructor called");
		this.name = name;
		this.password = password;
	}

	@Override
	public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
		LOGGER.info("Callback Handler - handle called");
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof NameCallback) {
				NameCallback nameCallback = (NameCallback) callbacks[i];
				nameCallback.setName(name);
			} else if (callbacks[i] instanceof PasswordCallback) {
				PasswordCallback passwordCallback = (PasswordCallback) callbacks[i];
				passwordCallback.setPassword(password.toCharArray());
			} else {
				throw new UnsupportedCallbackException(callbacks[i], "The submitted Callback is unsupported");
			}
		}
	}

}
