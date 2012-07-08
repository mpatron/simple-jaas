package org.jobjects.jaas.persistance;

import java.util.List;
import java.util.Map;

public interface UserRoleInformation {
	boolean init(Map<String, ?> options); 
	boolean isValidUser(String login, char[]password);
	List<String> getRoles();
}
