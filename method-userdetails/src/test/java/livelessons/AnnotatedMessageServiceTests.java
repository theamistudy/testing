package livelessons;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * @author Rob Winch
 * @since 5.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class AnnotatedMessageServiceTests {
	@Autowired
	MessageService messageService;

	@Test
	public void getMessageWhenNotAuthenticatedThenAuthenticationCredentialsNotFoundException() {
		assertThatCode(() -> this.messageService.getMessage())
				.isInstanceOf(AuthenticationCredentialsNotFoundException.class);
	}

	@Test
	@WithUserDetails
	public void getMessageWhenAuthorizedThenGranted() {
		assertThatCode(() -> this.messageService.getMessage())
				.doesNotThrowAnyException();
	}

	@Test
	@WithUserDetails("admin")
	public void getMessageWhenAdminThenGranted() {
		assertThatCode(() -> this.messageService.getMessage())
				.doesNotThrowAnyException();
	}

	@Autowired
	UserDetailsManager users;

	UserDetails test = User
			.withUsername("test")
			.password("password")
			.roles("TEST")
			.build();

	@Before
	public void setup() {
		this.users.createUser(this.test);
	}

	@After
	public void cleanup() {
		this.users.deleteUser(this.test.getUsername());
	}

	@Test
	// @WithUserDetails("test") // fails
	@WithUserDetails(value = "test",
		setupBefore = TestExecutionEvent.TEST_METHOD)
	public void testUser() {
		assertThatCode(() -> this.messageService.getMessage())
				.doesNotThrowAnyException();
	}
}
