/**
 * 
 */
package org.jobjects.jaas;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyFalse;
import org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyTrue;
import org.junit.Assert;
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

  private static Logger LOGGER = Logger.getLogger(HttpJaasLoginModuleTest.class
      .getName());

  HttpJaasLoginModule instance = new HttpJaasLoginModule();
  @Mock
  private CallbackHandler callbackHandler;
  private Subject subject = new Subject();
  @Mock
  private Map<String, ?> sharedState;
  private Map<String, String> options;

  /**
   * Test method for
   * {@link org.jobjects.jaas.HttpJaasLoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)}
   * .
   */
  @Test
  public void testInitialize() {
    LOGGER.finest("HttpJaasLoginModuleTest#testInitialize");
    try {
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation) {

          Object[] args = invocation.getArguments();
          Callback[] callbacks = (Callback[]) args[0];
          ((NameCallback) callbacks[0]).setName("mon nom");
          ((PasswordCallback) callbacks[1]).setPassword("password"
              .toCharArray());

          // Mock mock = (Mock) invocation.getMock();
          return null;
        }
      }).when(callbackHandler).handle(any(Callback[].class));
    } catch (IOException e1) {
      LOGGER.log(Level.SEVERE, "Erreur dans le test", e1);
    } catch (UnsupportedCallbackException e1) {
      LOGGER.log(Level.SEVERE, "Erreur dans le test", e1);
    }

    options = new HashMap<String, String>();
    options.put("persistanceMode", "ONLY_TRUE");
    options.put("finest", "true");
    instance.setUserRoleInformation(new UserRoleInformationOnlyTrue());
    instance.initialize(subject, callbackHandler, sharedState, options);
  }

  /**
   * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#login()}.
   */
  @Test(timeout = 1000)
  public void testLoginFalse() {
    LOGGER.finest("HttpJaasLoginModuleTest#testLoginFalse");
    try {
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation) {

          Object[] args = invocation.getArguments();
          Callback[] callbacks = (Callback[]) args[0];
          ((NameCallback) callbacks[0]).setName("mon nom");
          ((PasswordCallback) callbacks[1]).setPassword("password"
              .toCharArray());

          // Mock mock = (Mock) invocation.getMock();
          return null;
        }
      }).when(callbackHandler).handle(any(Callback[].class));

      options = new HashMap<String, String>();
      options.put("persistanceMode", "ONLY_TRUE");
      options.put("finest", "true");
      instance.setUserRoleInformation(new UserRoleInformationOnlyFalse());
      instance.initialize(subject, callbackHandler, sharedState, options);
      Assert.assertFalse(instance.login());
    } catch (Exception e1) {
      LOGGER.log(Level.SEVERE, "Erreur dans le test", e1);
      fail(e1.getLocalizedMessage());
    }
  }

  /**
   * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#login()}.
   */
  @Test(timeout = 1000)
  public void testLoginTrue() {
    LOGGER.finest("HttpJaasLoginModuleTest#testLoginTrue");
    try {
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation) {

          Object[] args = invocation.getArguments();
          Callback[] callbacks = (Callback[]) args[0];
          ((NameCallback) callbacks[0]).setName("mon nom");
          ((PasswordCallback) callbacks[1]).setPassword("password"
              .toCharArray());

          // Mock mock = (Mock) invocation.getMock();
          return null;
        }
      }).when(callbackHandler).handle(any(Callback[].class));

      options = new HashMap<String, String>();
      options.put("persistanceMode", "ONLY_TRUE");
      options.put("finest", "true");
      instance.setUserRoleInformation(new UserRoleInformationOnlyTrue());
      instance.initialize(subject, callbackHandler, sharedState, options);
      Assert.assertTrue(instance.login());
    } catch (Exception e1) {
      LOGGER.log(Level.SEVERE, "Erreur dans le test", e1);
      fail(e1.getLocalizedMessage());
    }
  }

  /**
   * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#commit()}.
   */
  @Test
  public void testCommit() {
    LOGGER.finest("HttpJaasLoginModuleTest#testCommit");
    try {
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation) {

          Object[] args = invocation.getArguments();
          Callback[] callbacks = (Callback[]) args[0];
          ((NameCallback) callbacks[0]).setName("mon nom");
          ((PasswordCallback) callbacks[1]).setPassword("password"
              .toCharArray());

          // Mock mock = (Mock) invocation.getMock();
          return null;
        }
      }).when(callbackHandler).handle(any(Callback[].class));

      options = new HashMap<String, String>();
      options.put("persistanceMode", "ONLY_TRUE");
      options.put("finest", "true");
      instance.setUserRoleInformation(new UserRoleInformationOnlyTrue());
      instance.initialize(subject, callbackHandler, sharedState, options);
      instance.login();
      Assert.assertTrue(instance.commit());
    } catch (Exception e1) {
      LOGGER.log(Level.SEVERE, "Erreur dans le test", e1);
      fail(e1.getLocalizedMessage());
    }
  }

  /**
   * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#abort()}.
   */
  @Test
  public void testAbort() {
    LOGGER.finest("HttpJaasLoginModuleTest#testAbort");
    try {
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation) {

          Object[] args = invocation.getArguments();
          Callback[] callbacks = (Callback[]) args[0];
          ((NameCallback) callbacks[0]).setName("mon nom");
          ((PasswordCallback) callbacks[1]).setPassword("password"
              .toCharArray());

          // Mock mock = (Mock) invocation.getMock();
          return null;
        }
      }).when(callbackHandler).handle(any(Callback[].class));

      options = new HashMap<String, String>();
      options.put("persistanceMode", "ONLY_TRUE");
      options.put("finest", "true");
      instance.setUserRoleInformation(new UserRoleInformationOnlyTrue());
      instance.initialize(subject, callbackHandler, sharedState, options);
      Assert.assertFalse(instance.abort());
    } catch (Exception e1) {
      LOGGER.log(Level.SEVERE, "Erreur dans le test", e1);
      fail(e1.getLocalizedMessage());
    }
  }

  /**
   * Test method for {@link org.jobjects.jaas.HttpJaasLoginModule#logout()}.
   */
  @Test
  public void testLogout() {
    LOGGER.finest("HttpJaasLoginModuleTest#testLogout");
    try {
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation) {

          Object[] args = invocation.getArguments();
          Callback[] callbacks = (Callback[]) args[0];
          ((NameCallback) callbacks[0]).setName("mon nom");
          ((PasswordCallback) callbacks[1]).setPassword("password"
              .toCharArray());

          // Mock mock = (Mock) invocation.getMock();
          return null;
        }
      }).when(callbackHandler).handle(any(Callback[].class));

      options = new HashMap<String, String>();
      options.put("persistanceMode", "ONLY_TRUE");
      options.put("finest", "true");
      instance.setUserRoleInformation(new UserRoleInformationOnlyTrue());
      instance.initialize(subject, callbackHandler, sharedState, options);
      Assert.assertTrue(instance.logout());
    } catch (Exception e1) {
      LOGGER.log(Level.SEVERE, "Erreur dans le test", e1);
      fail(e1.getLocalizedMessage());
    }
  }

}
