import controller.AdminStaffController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;
import view.View;

import java.io.*;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddWebPageSystemTest {
    private static AdminStaffController adminController;
    private static final PrintStream originalOut = System.out;
    private static ByteArrayOutputStream out =
            new ByteArrayOutputStream();
    private static SharedContext context;
    static void setUp(ByteArrayInputStream in) throws IOException,
            URISyntaxException,
            ParseException {
        System.setIn(in);
        context = new SharedContext();
        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk","AdminStaff");
        context.setCurrentUser(testUser);
        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        adminController = new AdminStaffController(context,view,authService,emailService);

    }

    @BeforeEach
    void setOut(){
        System.setOut(new PrintStream(out));
    }
    @AfterEach
    void clearOut() throws IOException {
        out = new ByteArrayOutputStream();
        System.setOut(originalOut);
    }
    @Test
    void addPageMSSTest() throws IOException, URISyntaxException, ParseException {
        ByteArrayInputStream in = new ByteArrayInputStream("Page 1\nsrc/test/java/page.txt\nyes\n".getBytes());
        setUp(in);
        System.out.println(context.getPageList().isEmpty());
        adminController.addPage();
        //correct string is checking for true for empty page list at
        // beginning, then correct output given inputs above, then false for
        // non-empty pageList
        String correct = "true\nEnter page title\n" +
                "Enter file address for page content\n" +
                "Should this page be private?\n" +
                "\u001B[36mEmail from test@hindeburg.ac.uk to admin@hindeburg.ac.uk\n" +
                "Page 1\n" +
                "I am a page.\n" +
                "A small page, and a page with two paragraphs.\n" +
                "\n" +
                "This is the second paragraph. Awesome.\n" +
                "\n" +
                "\u001B[0mSUCCESS: Added page Page 1\nfalse\n";
        System.out.println(context.getPageList().isEmpty());
        assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
    }

    @Test
    void addPage3aTest() throws IOException, URISyntaxException, ParseException {
        ByteArrayInputStream in = new ByteArrayInputStream(("Page 1\nsrc/test" +
                "/java/empty page.txt\nsrc/test/java/page.txt\nyes\n").getBytes());
        setUp(in);
        System.out.println(context.getPageList().isEmpty());
        //correct string is true for empty page list at beginning, then
        // checking for correct output given the inputs above, then checking
        // for only one page in pageList
        String correct = "true\nEnter page title\n" +
                "Enter file address for page content\n" +
                "WARNING: Please enter a non-empty file\n" +
                "Enter file address for page content\n" +
                "Should this page be private?\n" +
                "\u001B[36mEmail from test@hindeburg.ac.uk to admin@hindeburg.ac.uk\n" +
                "Page 1\n" +
                "I am a page.\n" +
                "A small page, and a page with two paragraphs.\n" +
                "\n" +
                "This is the second paragraph. Awesome.\n" +
                "\n" +
                "\u001B[0mSUCCESS: Added page Page 1\n1\n";
        adminController.addPage();
        System.out.println(context.getPageList().toArray().length);
        assertEquals(correct,out.toString().replaceAll("\r\n","\n"));
    }

}
