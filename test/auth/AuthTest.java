package auth;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import info.joseluismartin.auth.AuthHashMD5;
import info.joseluismartin.auth.AuthManager;
import info.joseluismartin.auth.AuthPlain;
import info.joseluismartin.auth.AuthStrategy;
import info.joseluismartin.auth.Base64Coder;
import info.joseluismartin.dao.UserDao;
import info.joseluismartin.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import org.easymock.EasyMock;

/**
 * Test es.matchmind.auth package
 * 
 * @author Jose Luis Martin - (jolmarting@matchmind.es)
 */
public class AuthTest extends TestCase {
	/** test username */
	private static final String USERNAME = "test_username";
	/** test supplied password */
	private static final String SUPPLIED_PASS = "supplied_password";
	/** test stored password */
	private static final String STORED_PASS = "stored_password";
	// create mocks
	/** user dao mock */
	private UserDao userDao = EasyMock.createMock(UserDao.class);
	/** auth strategy mock */
	private AuthStrategy authStrategy = EasyMock.createMock(AuthStrategy.class);
	
	/** 
	 * Test AuthManager using mocks on  colaborators, userDao and User Strategy.
	 * 
	 */
	public void testAuthManagerValidate() {
		
		AuthManager authManager = newAuthManager();
		// ensure that mocks are reset
		reset(userDao);
		reset(authStrategy);
		// record expectations on userDao and authStrategy
		expect(userDao.findByUsername(USERNAME)).andReturn(newTestUser());
		expect(authStrategy.validate(SUPPLIED_PASS, STORED_PASS)).andReturn(true);
		// sets mocks in replay state
		replay(authStrategy);
		replay(userDao);
		// run the method that we are testing...
		boolean valid = authManager.validate(USERNAME, SUPPLIED_PASS);
		// test that mocks receive all configured expectantions
		verify(authStrategy);
		verify(userDao);
		// if don't get exceptions from mocks here, the test passed.
		assertTrue(valid);
	}
		
	/**
	 * Test Auth plain
	 */
	public void testAuthPlain()  {
		AuthPlain auth = new AuthPlain();
		assertTrue(auth.validate(SUPPLIED_PASS, SUPPLIED_PASS));
		assertFalse(auth.validate(SUPPLIED_PASS, STORED_PASS));
		assertFalse(auth.validate(null, null));
	}
	
	/**
	 * Test auth hash md5
	 */
	public void testAuthHashMD5() {
		AuthHashMD5 auth = new AuthHashMD5();
		try {
			assertTrue(auth.validate(SUPPLIED_PASS, hashmd5(SUPPLIED_PASS)));
		}
		catch (NoSuchAlgorithmException e) {
			fail(e.getMessage());
		}
		assertFalse(auth.validate(SUPPLIED_PASS, STORED_PASS));
		assertFalse(auth.validate(null, null));
	}
	/**
	 * 
	 * @param asuppliedPass pass
	 * @return base64 encode of hash md5
	 * @throws NoSuchAlgorithmException from MessageDigester
	 */
	private String hashmd5(
			String asuppliedPass) throws NoSuchAlgorithmException   {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(asuppliedPass.getBytes());
	
		return String.valueOf(Base64Coder.encode(md.digest()));
	}

	/**
	 * 
	 * @return new test user
	 */
	private User newTestUser() {
		User user = new User();
		user.setPassword(STORED_PASS);
		user.setUsername(USERNAME);
		return user;
		
	}

	/**
	 * 
	 * @return new auth manager
	 */
	private AuthManager newAuthManager() {
		AuthManager authManager = new AuthManager();
		reset(authStrategy);
		authManager.setAuthStrategy(authStrategy);
		reset(userDao);
		authManager.setUserDao(userDao);
		
		return authManager;
	}
}
