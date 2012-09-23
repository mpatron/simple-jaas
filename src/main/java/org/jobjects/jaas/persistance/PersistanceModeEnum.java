/**
 * 
 */
package org.jobjects.jaas.persistance;

/**
 * persistanceMode
 * 
 * SimpleJaaS {
 * org.jobjects.jaas.HttpJaasLoginModule required
 * persistanceMode=JDBC
 * dbDriver="org.apache.derby.jdbc.EmbeddedDriver"
 * dbURL="jdbc:derby:memory:MyDerbyDB;upgrade=true"
 * dbUser="sa"
 * dbPassword="manager"
 * userQuery="select username from MyDerbyDB.secu_user u where u.username=? and u.password=?"
 * roleQuery="select r.rolename from MyDerbyDB.secu_user u, MyDerbyDB.secu_user_role r where u.username=r.username and u.username=?"
 * debug=true;
 * };
 * @author Mickael
 *
 */
public enum PersistanceModeEnum {
	JDBC,
	ONLY_TRUE,
	ONLY_FALSE;
}
