import controller.AdminStaffController;
import controller.Controller;
import controller.InquirerController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.Guest;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;
import view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsultWebpagesSystemTest {
    /*
    * all of the bytearrays in this have a little bit extra to add a page to
    * the list that we can search.
    * */
    private static AdminStaffController controller;
    private static InquirerController inquirerController;
    private static final PrintStream originalOut = System.out;
    private static ByteArrayOutputStream out =
            new ByteArrayOutputStream();
    static void setUp(ByteArrayInputStream in) throws IOException,
            URISyntaxException,
            ParseException {
        System.setIn(in);
        SharedContext context = new SharedContext();
        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk","AdminStaff");
        context.setCurrentUser(testUser);
        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        controller = new AdminStaffController(context,view,authService,emailService);
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        controller.addPage();
        context.setCurrentUser(new AuthenticatedUser("test.student@hindeburg" +
                ".ac.uk","Student"));
        inquirerController = new InquirerController(context,view,authService,emailService);
        System.setOut(originalOut);
    }

    void setOut(){
        System.setOut(new PrintStream(out));
    }
    @AfterEach
    void clearOut() throws IOException {
        out = new ByteArrayOutputStream();
        System.setOut(originalOut);
    }
    @Test
    void consultWebpagesMSS() throws IOException, URISyntaxException, ParseException {
        ByteArrayInputStream in = new ByteArrayInputStream("Page 1\nsrc/test/java/page.txt\nyes\nparagraph*\n".getBytes());
        setUp(in);
        String correct = "Please enter your query:\n" +
                "SUCCESS: Results Found\n" +
                "Page 1\n" +
                "\n" +
                "I am a page.\n" +
                "A small page, and a page with two paragraphs.\n" +
                "Page 1\n" +
                "\n" +
                "This is the second paragraph. Awesome.\n" +
                "\n" +
                "-----------------------------------------\n";
        setOut();
        inquirerController.searchPages();
        assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
    }

    @Test
    void consultWebpages2() throws IOException, URISyntaxException, ParseException {
        ByteArrayInputStream in = new ByteArrayInputStream("Page 1\nsrc/test/java/page.txt\nyes\n\nparagraph AND\nparagraph*\n".getBytes());
        setUp(in);
        String correct = "Please enter your query:\n" +
                "WARNING: Empty Query\n" +
                "Please enter your query:\n" +
                "ERROR: Cannot parse your query. Please enter a correctly formatted query.\n" +
                "Please enter your query:\n" +
                "SUCCESS: Results Found\n" +
                "Page 1\n" +
                "\n" +
                "I am a page.\n" +
                "A small page, and a page with two paragraphs.\n" +
                "Page 1\n" +
                "\n" +
                "This is the second paragraph. Awesome.\n" +
                "\n" +
                "-----------------------------------------\n";
        setOut();
        inquirerController.searchPages();
        assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
    }

    @Test
    void consultWebpages3() throws IOException, URISyntaxException, ParseException {
        ByteArrayInputStream in = new ByteArrayInputStream("Page 1\nsrc/test/java/page.txt\nyes\nsupercalifragilisticexpialidocious\n".getBytes());
        setUp(in);
        String correct = "Please enter your query:\n" +
                "ERROR: No Results Found, please try a different query\n" +
                "-----------------------------------------\n";
        setOut();
        inquirerController.searchPages();
        assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
    }

}
