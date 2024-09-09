import controller.AdminStaffController;
import controller.StaffController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.Inquiry;
import model.SharedContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;
import view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;



import static org.junit.jupiter.api.Assertions.assertEquals;

class RedirectInquiryUseCaseSystemTest {
    private static final PrintStream originalOut = System.out;
    private static ByteArrayOutputStream out = new ByteArrayOutputStream();
    private static SharedContext context;
    private static AdminStaffController adminController;

    private static StaffController staffController;

    @BeforeEach
    void setUp() throws Exception {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        context = new SharedContext();
        View view = new TextUserInterface();
        EmailService emailService = new MockEmailService();
        AuthenticationService authenticationService = new MockAuthenticationService();
        adminController = new AdminStaffController(context, view, authenticationService, emailService);
        staffController = new StaffController(context,view,authenticationService,emailService);



    }

    @AfterEach
    void restoreSystem() throws IOException {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Test
    void redirectInquiryMSS() throws Exception {
        Inquiry testInquiry1 = new Inquiry("Test Inquiry1", "This is a test inquiry content1.", "testsender1@hindeburg.ac.uk");
        String simulatedUserInput = "teachingstaff@hindeburg.ac.uk\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        setUp();
        adminController.redirectInquiry(testInquiry1);
        String expectedOutput = "Please enter the email address of the teaching staff you want to redirect to.\n"+
        "\u001B[36mEmail from admin@hindeburg.ac.uk to teachingstaff@hindeburg.ac.uk\n"+
        "Test Inquiry1"+
        "The original sender's email:testsender1@hindeburg.ac.uk\n"+
        "This is a test inquiry content1.\n"+
        "\u001B[0mThe inquiry has been redirected.\n";
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));

    }
    @Test
    void redirectInquiry2a() throws Exception {
        Inquiry testInquiry1 = new Inquiry("Test Inquiry1", "This is a test inquiry content1.", "testsender1@hindeburg.ac.uk");
        String simulatedUserInput = "\nteachingstaff@hindeburg.ac.uk\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        setUp();
        adminController.redirectInquiry(testInquiry1);
        String expectedOutput = "Please enter the email address of the teaching staff you want to redirect to.\n"+
                "A teaching staff email must be provided for redirecting inquiry.\n"+
                "\u001B[36mEmail from admin@hindeburg.ac.uk to teachingstaff@hindeburg.ac.uk\n"+
                "Test Inquiry1"+
                "The original sender's email:testsender1@hindeburg.ac.uk\n"+
                "This is a test inquiry content1.\n"+
                "\u001B[0mThe inquiry has been redirected.\n";
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));

    }
}