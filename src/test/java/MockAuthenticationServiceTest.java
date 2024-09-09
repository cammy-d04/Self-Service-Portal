import external.MockAuthenticationService;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class MockAuthenticationServiceTest {

    private MockAuthenticationService authService;

    @BeforeEach
    void setUp() throws URISyntaxException, IOException, ParseException {
        // Initialize MockAuthenticationService with some predefined data
        try {
            authService = new MockAuthenticationService();
        } catch (NullPointerException e) {
            fail("The required JSON resource file was not found.", e);
        } catch (Exception e) {
            fail("An error occurred while initializing MockAuthenticationService", e);
        }

    }

    @Test
    void testLoginSuccessful() {
        // Test for successful login with correct username and password
        String expectedResult = "{\"password\":\"catch me if u can\",\"role\":\"AdminStaff\",\"email\":\"jack.tr@hindenburg.ac.uk\",\"username\":\"JackTheRipper\"}";
        String result = authService.login("JackTheRipper", "catch me if u can");
        assertNotNull(result, "The result should not be null for a successful login");
        assertEquals(expectedResult, result, "The login result does not match the expected JSON string.");
    }

    @Test
    void testLoginFailureIncorrectPassword() {
        // Test for login failure with correct username but incorrect password
        String result = authService.login("JackTheRipper", "wrongPassword");
        assertNotNull(result);
        // Check if the result contains the error message
        assertTrue(result.contains("\"error\":\"Wrong username or password\""));
    }

    @Test
    void testLoginFailureUsernameNotFound() {
        // Test for login failure with username that doesn't exist
        String result = authService.login("nonExistentUsername", "anyPassword");
        assertNotNull(result);
        // Check if the result contains the error message
        assertTrue(result.contains("\"error\":\"Wrong username or password\""));
    }
}

