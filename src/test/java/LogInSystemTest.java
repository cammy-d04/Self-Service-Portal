import controller.GuestController;
import external.MockEmailService;
import model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import view.TextUserInterface;
import view.View;
import external.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import external.MockAuthenticationService;

import java.io.*;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class LogInSystemTest{

    private static GuestController guestController;
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
        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk","AdminStaff");
        context.setCurrentUser(testUser);
        View view = new TextUserInterface();
        MockAuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        guestController = new GuestController(context, view, authService, emailService);
    }

    @BeforeEach
    void setOut(){
        System.setOut(new PrintStream(out));
    }
    @AfterEach
    void clearOut(){
        out = new ByteArrayOutputStream();
        System.setOut(originalOut);
    }

    @Test
    public void testCorrect() throws Exception {

        JSONArray usersArray = (JSONArray) new JSONParser().parse(new FileReader("src/main/resources/MockUserDataGroups4.json"));

        // iterate over each user in the mockauthenticationservice
        for (Object user : usersArray) {
            JSONObject userJSON = (JSONObject) user;
            String username = (String) userJSON.get("username");
            String password = (String) userJSON.get("password");
            String correct = "Please enter your username.\n" +
                    "Please enter your password.\n" +
                    "SUCCESS: Log in successful.\n";

            ByteArrayInputStream in = new ByteArrayInputStream((username + "\n" + password).getBytes());
            setUp(in);
            setOut();

            //test login
            guestController.login();
            //verify successful
            assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
            clearOut();
        }
    }
    @Test
    public void testIncorrectPassword() throws Exception {

        JSONArray usersArray = (JSONArray) new JSONParser().parse(new FileReader("src/main/resources/MockUserDataGroups4.json"));

        // iterate over each user in the mockauthenticationservice
        for (Object user : usersArray) {
            JSONObject userJSON = (JSONObject) user;
            String username = (String) userJSON.get("username");
            String password = ("wrongPassword");
            String correct = "Please enter your username.\n" +
                    "Please enter your password.\n" +
                    "ERROR: Wrong username or password\n";

            ByteArrayInputStream in = new ByteArrayInputStream((username + "\n" + password).getBytes());
            setUp(in);
            setOut();

            //test login
            guestController.login();

            //verify exception thrown
            assertEquals(correct, out.toString().replaceAll("\r\n", "\n"));
            clearOut();
        }
    }

        @Test
        public void testIncorrectUsername() throws Exception {

            JSONArray usersArray = (JSONArray) new JSONParser().parse(new FileReader("src/main/resources/MockUserDataGroups4.json"));

            // iterate over each user in the mockauthenticationservice

                String username = ("wrongUsername");
                String password = ("wrongPassword");
                String correct = "Please enter your username.\n" +
                        "Please enter your password.\n" +
                        "ERROR: Wrong username or password\n";

                ByteArrayInputStream in = new ByteArrayInputStream((username + "\n" + password).getBytes());
                setUp(in);
                setOut();

                //test login
                guestController.login();

                //verify exception thrown
                assertEquals(correct, out.toString().replaceAll("\r\n", "\n"));
                clearOut();

        }

}
