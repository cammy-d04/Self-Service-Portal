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

class AnswerInquirySystemTest {
    private static final PrintStream originalOut = System.out;
    private static ByteArrayOutputStream out = new ByteArrayOutputStream();
    private static SharedContext context;

    private static StaffController staffController;

    @BeforeEach
    void setUp() throws Exception {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        context = new SharedContext();
        View view = new TextUserInterface();
        EmailService emailService = new MockEmailService();
        AuthenticationService authenticationService = new MockAuthenticationService();
        staffController = new StaffController(context,view,authenticationService,emailService);
        Inquiry testInquiry1 = new Inquiry("Test Inquiry1", "This is a test inquiry content1.", "testsender1@hindeburg.ac.uk");


    }

    @AfterEach
    void restoreSystem() throws IOException {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Test
    void answerInquiryMSS() throws Exception {
        Inquiry testInquiry1 = new Inquiry("Test Inquiry1", "This is a test inquiry content1.", "testsender1@hindeburg.ac.uk");
        String simulatedUserInput = "This is the answer to the inquiry.\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        setUp();
        staffController.respondToInquiry(testInquiry1);

        String expectedOutput = "Please enter your response to the inquiry:\n"+
                "\u001B[36mEmail from admin@hindeburg.ac.uk to testsender1@hindeburg.ac.uk\n"+
                "Re:"+
                "Test Inquiry1\n"+
                "This is the answer to the inquiry.\n"+
                "\u001B[0mYour response has been sent.";
        assertEquals(expectedOutput.replace("\r\n", "\n").replace("\r", "\n"),
                out.toString().trim().replace("\r\n", "\n").replace("\r", "\n"));

    }

    @Test
    void answerInquiry2a() throws Exception {
        Inquiry testInquiry1 = new Inquiry("Test Inquiry1", "This is a test inquiry content1.", "testsender1@hindeburg.ac.uk");
        String simulatedUserInput = "\nThis is the answer to the inquiry.\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        setUp();
        staffController.respondToInquiry(testInquiry1);

        String expectedOutput = "Please enter your response to the inquiry:\n"+
                "A response can not be empty.\n"+
                "\u001B[36mEmail from admin@hindeburg.ac.uk to testsender1@hindeburg.ac.uk\n"+
                "Re:"+
                "Test Inquiry1\n"+
                "This is the answer to the inquiry.\n"+
                "\u001B[0mYour response has been sent.";
        assertEquals(expectedOutput.replace("\r\n", "\n").replace("\r", "\n"),
                out.toString().trim().replace("\r\n", "\n").replace("\r", "\n"));

    }


}