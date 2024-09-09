import controller.AdminStaffController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;
import view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrowseWebpagesSystemTest {
    /*
    * all input bytearrays contain a little extra to add a page to the list
    * that we can view
    * */
    private static AdminStaffController adminController;
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
        adminController = new AdminStaffController(context,view,authService,emailService);
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        adminController.addPage();
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
    void browseWebpagesMSS() throws IOException, URISyntaxException, ParseException {
        ByteArrayInputStream in = new ByteArrayInputStream(("Page 1\nsrc/test" +
                "/java/page.txt\nyes\nno\n").getBytes());
        setUp(in);
        setOut();
        adminController.viewAllPages();
        String correct = "Page 1\n" +
                "\n" +
                "I am a page.\n" +
                "A small page, and a page with two paragraphs.\n" +
                "\n" +
                "This is the second paragraph. Awesome.\n" +
                "\n" +
                "Do you wish to add a new webpage?\n";
        assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
    }

    @Test
    void browseWebpages2() throws IOException, URISyntaxException, ParseException {
        ByteArrayInputStream in = new ByteArrayInputStream(("Page 1\nsrc/test" +
                "/java/page.txt\nyes\nyes\nPage 2\nsrc/test/java/page.txt\nno\n").getBytes());
        setUp(in);
        setOut();
        adminController.viewAllPages();
        String correct = "Page 1\n" +
                "\n" +
                "I am a page.\n" +
                "A small page, and a page with two paragraphs.\n" +
                "\n" +
                "This is the second paragraph. Awesome.\n" +
                "\n" +
                "Do you wish to add a new webpage?\n" +
                "Enter page title\n" +
                "Enter file address for page content\n" +
                "Should this page be private?\n" +
                "\u001B[36mEmail from test@hindeburg.ac.uk to admin@hindeburg.ac.uk\n" +
                "Page 2\n" +
                "I am a page.\n" +
                "A small page, and a page with two paragraphs.\n" +
                "\n" +
                "This is the second paragraph. Awesome.\n" +
                "\n" +
                "\u001B[0mSUCCESS: Added page Page 2\n";
        assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
    }
}
