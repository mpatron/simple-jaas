/**
 * 
 */
package org.jobjects.jaas;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jobjects.jaas.persistance.PersistanceSelector;
import org.jobjects.jaas.persistance.UserRoleInformation;

/**
 * http://www.javacodegeeks.com/2012/06/java-jaas-form-based-authentication.html
 * ?m=1
 * 
 * @author Mickael
 * 
 */
public class HttpJaasLoginModule implements LoginModule {

	private static Logger LOGGER = Logger.getLogger(HttpJaasLoginModule.class
			.getName());

	// initial state
	private Subject subject;
	private CallbackHandler callbackHandler;
	private Map<String, ?> sharedState;
	private Map<String, ?> options;

	// configurable option
	private boolean finest = false;

	// the authentication status
	private boolean succeeded = false;
	private boolean commitSucceeded = false;

	// user credentials
	private String username = null;
	private char[] password = null;

	// user principle
	private HttpJaasUserPrincipal userPrincipal = null;
	private HttpJaasPasswordPrincipal passwordPrincipal = null;

	// Persistance
	UserRoleInformation userRoleInformation = null;

	public HttpJaasLoginModule() {
		super();
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;
		finest = "true".equalsIgnoreCase((String) options.get("finest"));

		/*
		 * Log des options partagées
		 */
		if(finest) {
			StringBuffer sb = new StringBuffer();
			sb.append("Jaas sharedState" + "\n");
			Set<String> keys = this.sharedState.keySet();
			for (String key : keys) {
				sb.append(key + "=" + this.sharedState.get(key) + "\n");
			}
			LOGGER.finest(sb.toString());
		}
		/*
		 * Logs des options de configuration
		 */
		if(finest) {
			StringBuffer sb = new StringBuffer();
			sb.append("Jaas options" + "\n");
			Set<String> keys = this.options.keySet();
			for (String key : keys) {
				sb.append(key + "=" + this.options.get(key) + "\n");
			}
			LOGGER.finest(sb.toString());
		}

		userRoleInformation = PersistanceSelector.getInstance().getUserRoleInformation(options);
	}

	@Override
	public boolean login() throws LoginException {

		if (callbackHandler == null) {
			throw new LoginException("Error: no CallbackHandler available "
					+ "to garner authentication information from the user");
		}
		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("username");
		callbacks[1] = new PasswordCallback("password: ", false);

		try {

			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			password = ((PasswordCallback) callbacks[1]).getPassword();

			if (finest) {
				LOGGER.finest("Username :" + username);
				LOGGER.finest("Password : " + new String(password));
			}

			if (username == null || password == null) {
				LOGGER.severe("Callback handler does not return login data properly");
				throw new LoginException(
						"Callback handler does not return login data properly");
			}

			if (userRoleInformation.isValidUser(username, password)) { // validate
																		// user.
				succeeded = true;
				return true;
			}

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (UnsupportedCallbackException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		return false;
	}

	@Override
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			userPrincipal = new HttpJaasUserPrincipal(username);
			if (!subject.getPrincipals().contains(userPrincipal)) {
				subject.getPrincipals().add(userPrincipal);
				LOGGER.finest("User principal added:" + userPrincipal);
			}
			passwordPrincipal = new HttpJaasPasswordPrincipal(new String(
					password));
			if (!subject.getPrincipals().contains(passwordPrincipal)) {
				subject.getPrincipals().add(passwordPrincipal);
				LOGGER.finest("Password principal added: " + passwordPrincipal);
			}

			// populate subject with roles.
			List<String> roles = userRoleInformation.getRoles(username);
			for (String role : roles) {
				HttpJaasRolePrincipal rolePrincipal = new HttpJaasRolePrincipal(
						role);
				if (!subject.getPrincipals().contains(rolePrincipal)) {
					subject.getPrincipals().add(rolePrincipal);
					LOGGER.finest("Role principal added: " + rolePrincipal);
				}
			}

			commitSucceeded = true;

			LOGGER.info("Login subject were successfully populated with principals and roles");

			return true;
		}
	}

	@Override
	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			succeeded = false;
			username = null;
			if (password != null) {
				password = null;
			}
			userPrincipal = null;
		} else {
			logout();
		}
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(userPrincipal);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		if (password != null) {
			for (int i = 0; i < password.length; i++) {
				password[i] = ' ';
			}
			password = null;
		}
		userPrincipal = null;
		return true;
	}

}
