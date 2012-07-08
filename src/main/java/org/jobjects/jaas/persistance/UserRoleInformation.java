package org.jobjects.jaas.persistance;

import java.util.List;
import java.util.Map;

/**
 * @author Mickael
 * 
 */
public interface UserRoleInformation {
	/**
	 * @param options
	 * @return
	 */
	boolean init(Map<String, ?> options);

	/**
	 * @param login
	 * @param password
	 * @return
	 */
	boolean isValidUser(String login, char[] password);

	/**
	 * @param login
	 * @return
	 */
	List<String> getRoles(String login);
}
