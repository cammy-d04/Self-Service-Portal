import controller.AuthenticatedUserController;
import controller.GuestController;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;
import view.View;

import java.io.*;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogOutSystemTest {

    private static GuestController guestController;
    private static AuthenticatedUserController authController;


    private static final PrintStream originalOut = System.out;
    private static ByteArrayOutputStream out =
            new ByteArrayOutputStream();
    private static SharedContext context;

    @Before
    public void setUp(ByteArrayInputStream in) throws IOException,
            URISyntaxException,
            ParseException {
        System.setIn(in);
        context = new SharedContext();
        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk", "AdminStaff");
        context.setCurrentUser(testUser);
        View view = new TextUserInterface();
        MockAuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        guestController = new GuestController(context, view, authService, emailService);
        authController = new AuthenticatedUserController(context, view, authService, emailService);
    }

    @BeforeEach
    void setOut() {
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void clearOut() {
        out = new ByteArrayOutputStream();
        System.setOut(originalOut);
    }

    @Test
    public void logOutTest() throws Exception {

        JSONArray usersArray = (JSONArray) new JSONParser().parse(new FileReader("src/main/resources/MockUserDataGroups4.json"));

        // iterate over each user in the mockauthenticationservice
        for (Object user : usersArray) {
            JSONObject userJSON = (JSONObject) user;
            String username = (String) userJSON.get("username");
            String password = (String) userJSON.get("password");
            String correct = "Please enter your username.\n" +
                    "Please enter your password.\n" +
                    "SUCCESS: Log in successful.\n" +
                    "SUCCESS: Log out successful\n";

            ByteArrayInputStream in = new ByteArrayInputStream((username + "\n" + password + "\n").getBytes());
            setUp(in);
            setOut();

            //test login
            guestController.login();

            authController.logout();


            //verify successful
            assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
            clearOut();
        }
    }
}