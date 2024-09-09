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

public class ConsultMemberOfStaffSystemTest {

    private static InquirerController inquirerController;
    private static final PrintStream originalOut = System.out;
    private static ByteArrayOutputStream out = new ByteArrayOutputStream();
    private static SharedContext context;

    private static void setUp(String userType, ByteArrayInputStream in) throws IOException, URISyntaxException, ParseException {
        System.setIn(in);
        context = new SharedContext();
        if ("Guest".equals(userType)) {
            context.setCurrentUser(new Guest());
        } else {
            context.setCurrentUser(new AuthenticatedUser("student@hindeburg.ac.uk", "Student"));
        }
        View view = new TextUserInterface();
        AuthenticationService authenticationService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        inquirerController = new InquirerController(context, view,authenticationService, emailService);
    }

    @BeforeEach
    void setOut() {
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void restoreSystem() throws IOException {
        out = new ByteArrayOutputStream();
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Test
    void contactStaffMSSAsStudentTest() throws IOException, URISyntaxException, ParseException {
        String simulatedUserInput = "Inquiry Subject\nInquiry Content\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        setUp("Student", in);
        inquirerController.contactStaff();

        // Verify the output contains confirmation and checks the inquiry list size
        String expectedOutput = "Please enter the subject of your inquiry:\n" +
                "Please enter the content of your inquiry:\n" +
                "\u001B[36mEmail from student@hindeburg.ac.uk to admin@hindeburg.ac.uk\n" +
                "Inquiry Subject\n"+
                "Inquiry Content\n"+
                "\u001B[0mSUCCESS: Your inquiry has been sent to the administration staff.\n" ;
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));
    }

    @Test
    void contactStaff2aNoSubjectTest() throws IOException, URISyntaxException, ParseException {
        String simulatedUserInput = "\nInquiry Subject\nInquiry Content\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        setUp("Student", in);
        inquirerController.contactStaff();

        // Verify the output contains confirmation and checks the inquiry list size
        String expectedOutput = "Please enter the subject of your inquiry:\n" +
                "A subject must be provided for the inquiry.\n"+
                "Please enter the content of your inquiry:\n" +
                "\u001B[36mEmail from student@hindeburg.ac.uk to admin@hindeburg.ac.uk\n" +
                "Inquiry Subject\n"+
                "Inquiry Content\n"+
                "\u001B[0mSUCCESS: Your inquiry has been sent to the administration staff.\n" ;
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));
    }

    @Test
    void contactStaff2aNoContentTest() throws IOException, URISyntaxException, ParseException {
        String simulatedUserInput = "Inquiry Subject\n\nInquiry Content\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        setUp("Student", in);
        inquirerController.contactStaff();

        // Verify the output contains confirmation and checks the inquiry list size
        String expectedOutput = "Please enter the subject of your inquiry:\n" +
                "Please enter the content of your inquiry:\n" +
                "A content must be provided for the inquiry.\n"+
                "\u001B[36mEmail from student@hindeburg.ac.uk to admin@hindeburg.ac.uk\n" +
                "Inquiry Subject\n"+
                "Inquiry Content\n"+
                "\u001B[0mSUCCESS: Your inquiry has been sent to the administration staff.\n" ;
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));
    }

    @Test
    void contactStaff3aAsGuestTest() throws IOException, URISyntaxException, ParseException {
        String simulatedUserInput = "Inquiry Subject\nInquiry Content\nguest@example.com\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        setUp("Guest", in);
        inquirerController.contactStaff();

        // Verify the output contains confirmation and checks the inquiry list size
        String expectedOutput = "Please enter the subject of your inquiry:\n" +
                "Please enter the content of your inquiry:\n" +
                "Please enter your email address:\n" +
                "\u001B[36mEmail from guest@example.com to admin@hindeburg.ac.uk\n"+
                "Inquiry Subject\n"+
                "Inquiry Content\n"+
                "\u001B[0mSUCCESS: Your inquiry has been sent to the administration staff.\n" ;
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));
    }

    @Test
    void contactStaff3a3aAsGuestNoEmailTest() throws IOException, URISyntaxException, ParseException {
        String simulatedUserInput = "Inquiry Subject\nInquiry Content\n\nguest@example.com\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        setUp("Guest", in);
        inquirerController.contactStaff();

        // Verify the output contains confirmation and checks the inquiry list size
        String expectedOutput = "Please enter the subject of your inquiry:\n" +
                "Please enter the content of your inquiry:\n" +
                "Please enter your email address:\n" +
                "A email must be provided for the inquiry.\n"+
                "\u001B[36mEmail from guest@example.com to admin@hindeburg.ac.uk\n"+
                "Inquiry Subject\n"+
                "Inquiry Content\n"+
                "\u001B[0mSUCCESS: Your inquiry has been sent to the administration staff.\n" ;
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));
    }

    @Test
    void contactStaff3a3bAsGuestWrongEmailTest() throws IOException, URISyntaxException, ParseException {
        String simulatedUserInput = "Inquiry Subject\nInquiry Content\n1234567890\nguest@example.com\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        setUp("Guest", in);
        inquirerController.contactStaff();

        // Verify the output contains confirmation and checks the inquiry list size
        String expectedOutput = "Please enter the subject of your inquiry:\n" +
                "Please enter the content of your inquiry:\n" +
                "Please enter your email address:\n" +
                "Please enter your email address in a correct form:\n"+
                "\u001B[36mEmail from guest@example.com to admin@hindeburg.ac.uk\n"+
                "Inquiry Subject\n"+
                "Inquiry Content\n"+
                "\u001B[0mSUCCESS: Your inquiry has been sent to the administration staff.\n" ;
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));
    }





}