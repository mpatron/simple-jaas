/**
 * 
 */
package org.jobjects.jaas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * http://www.javacodegeeks.com/2012/06/java-jaas-form-based-authentication.html?m=1
 * @author Mickael
 *
 */
public class HttpJaasLoginModule implements LoginModule {

	private static Logger LOGGER = Logger.getLogger(HttpJaasLoginModule.class.getName()); 
	 
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
    
    //user credentials
    private String username = null;
    private char[] password = null;
    
    //user principle
    private HttpJaasUserPrincipal userPrincipal = null;
    private HttpJaasPasswordPrincipal passwordPrincipal = null;
    
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
  
        {
            StringBuffer sb = new StringBuffer(); 
			sb.append("Jaas sharedState"+"\n");
            Set<String> keys= this.sharedState.keySet();
            for (String key : keys) {
    			sb.append(key+"="+this.sharedState.get(key)+"\n");
    		}
            LOGGER.finest(sb.toString());
        }
        {
            StringBuffer sb = new StringBuffer(); 
			sb.append("Jaas options"+"\n");
            Set<String> keys= this.options.keySet();
            for (String key : keys) {
    			sb.append(key+"="+this.options.get(key)+"\n");
    		}
            LOGGER.finest(sb.toString());
        }
        
        finest = "true".equalsIgnoreCase((String)options.get("finest")); 
    }

    @Override
    public boolean login() throws LoginException {
  
        if (callbackHandler == null){
            throw new LoginException("Error: no CallbackHandler available " +
            "to garner authentication information from the user");
        }
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("username");
        callbacks[1] = new PasswordCallback("password: ", false);
  
        try {
   
            callbackHandler.handle(callbacks);
            username = ((NameCallback)callbacks[0]).getName();
            password = ((PasswordCallback)callbacks[1]).getPassword();
   
            if (finest) {
                LOGGER.finest("Username :" + username);
                LOGGER.finest("Password : " + new String(password));
            }
   
            if (username == null || password == null) {
                LOGGER.severe("Callback handler does not return login data properly");
                throw new LoginException("Callback handler does not return login data properly"); 
            }
   
            if (isValidUser()) { //validate user.
                succeeded = true;
                return true;
            } 
   
        } catch (IOException e) { 
             e.printStackTrace();
        } catch (UnsupportedCallbackException e) {
             e.printStackTrace();
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
            passwordPrincipal = new HttpJaasPasswordPrincipal(new String(password)); 
            if (!subject.getPrincipals().contains(passwordPrincipal)) {
                subject.getPrincipals().add(passwordPrincipal);
                LOGGER.finest("Password principal added: " + passwordPrincipal);
            }
      
            //populate subject with roles.
            List<String> roles = getRoles();
            for (String role: roles) {
                HttpJaasRolePrincipal rolePrincipal = new HttpJaasRolePrincipal(role);
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
            for (int i = 0; i < password.length; i++){
                password[i] = ' ';
            }
            password = null;
        }
        userPrincipal = null;
        return true;
   }
 
   private boolean isValidUser() throws LoginException {

      String sql = (String)options.get("userQuery");
      Connection con = null;
      ResultSet rs = null;
      PreparedStatement stmt = null;
  
      try {
          con = getConnection();
          stmt = con.prepareStatement(sql);
          stmt.setString(1, username);
          stmt.setString(2, new String(password));
   
          rs = stmt.executeQuery();
   
          if (rs.next()) { //User exist with the given user name and password.
              return true;
          }
       } catch (Exception e) {
           LOGGER.severe("Error when loading user from the database " + e);
           e.printStackTrace();
       } finally {
           try {
               rs.close();
           } catch (SQLException e) {
               LOGGER.severe("Error when closing result set." + e);
           }
           try {
               stmt.close();
           } catch (SQLException e) {
               LOGGER.severe("Error when closing statement." + e);
           }
           try {
               con.close();
           } catch (SQLException e) {
               LOGGER.severe("Error when closing connection." + e);
           }
       }
       return false;
   }

 /**
  * Returns list of roles assigned to authenticated user.
  * @return
  */
  private List<String> getRoles() { 
  
      Connection con = null;
      ResultSet rs = null;
      PreparedStatement stmt = null;
  
      List<String> roleList = new ArrayList<String>(); 
  
      try {
          con = getConnection();
          String sql = (String)options.get("roleQuery");
          stmt = con.prepareStatement(sql);
          stmt.setString(1, username);
   
          rs = stmt.executeQuery();
   
          if (rs.next()) { 
              roleList.add(rs.getString("rolename")); 
          }
      } catch (Exception e) {
          LOGGER.severe("Error when loading user from the database " + e);
          e.printStackTrace();
      } finally {
           try {
               rs.close();
           } catch (SQLException e) {
               LOGGER.severe("Error when closing result set." + e);
           }
           try {
               stmt.close();
           } catch (SQLException e) {
               LOGGER.severe("Error when closing statement." + e);
           }
           try {
               con.close();
           } catch (SQLException e) {
               LOGGER.severe("Error when closing connection." + e);
           }
       }
       return roleList;
 }
 
 /**
  * Returns JDBC connection
  * @return
  * @throws LoginException
  */
  private Connection getConnection() throws LoginException {
  
      String dBUser = (String)options.get("dbUser");
      String dBPassword = (String)options.get("dbPassword");
      String dBUrl = (String)options.get("dbURL");
      String dBDriver = (String)options.get("dbDriver");

      Connection con = null;
      try {
         //loading driver
         Class.forName (dBDriver).newInstance();
         con = DriverManager.getConnection (dBUrl, dBUser, dBPassword);
      } 
      catch (Exception e) {
         LOGGER.severe("Error when creating database connection" + e);
         e.printStackTrace();
      } finally {
      }
      return con;
   }
//	Subject subject;
//	CallbackHandler callbackHandler;
//	Map<String, ?> sharedState;
//	Map<String, ?> options;
//	
//	/* (non-Javadoc)
//	 * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
//	 */
//	public void initialize(Subject subject, CallbackHandler callbackHandler,
//			Map<String, ?> sharedState, Map<String, ?> options) {
//		// TODO Auto-generated method stub
//		this.subject=subject;
//		this.callbackHandler=callbackHandler;
//		this.sharedState=sharedState;
//		this.options=options;
//	}
//
//	/* (non-Javadoc)
//	 * @see javax.security.auth.spi.LoginModule#login()
//	 */
//	public boolean login() throws LoginException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see javax.security.auth.spi.LoginModule#commit()
//	 */
//	public boolean commit() throws LoginException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see javax.security.auth.spi.LoginModule#abort()
//	 */
//	public boolean abort() throws LoginException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see javax.security.auth.spi.LoginModule#logout()
//	 */
//	public boolean logout() throws LoginException {
//		// TODO Auto-generated method stub
//		return false;
//	}

}
