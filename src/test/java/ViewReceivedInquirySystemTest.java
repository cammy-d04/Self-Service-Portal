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

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
class ViewReceivedInquirySystemTest {

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
        Inquiry testInquiry1 = new Inquiry("Test Inquiry1", "This is a test inquiry content1.", "testsender1@hindeburg.ac.uk");
        Inquiry testInquiry2 = new Inquiry("Test Inquiry2", "This is a test inquiry content2.", "testsender2@hindeburg.ac.uk");
        List<Inquiry> newinquirylist11= context.getInquiryList();
        newinquirylist11.add(testInquiry1);
        newinquirylist11.add(testInquiry2);
        context.setInquiryList(newinquirylist11);


    }


    @AfterEach
    void restoreSystem() throws IOException {
        System.setOut(originalOut);
        System.setIn(System.in);
    }
    @Test
    void manageInquiryWithNoInquiriesTest() throws Exception {
        context.getInquiryList().clear();
        String simulatedUserInput = "Test Inquiry\na\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        adminController.manageInquiry();
        String expectedOutput = "There are no inquiries in the list.\n";
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));
    }

    @Test
    void manageInquiryRespondTest() throws Exception {
        String simulatedUserInput = "Test Inquiry1\n a\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        setUp();
        adminController.manageInquiry();
        String expectedOutput = "Test Inquiry1\n"+
        "Test Inquiry2\n"+
        "Please select the title of the inquiry to manage: \n"+
        "Subject: Test Inquiry1\n"+
        "Content: This is a test inquiry content1.\n"+
        "Inquirer's Email: testsender1@hindeburg.ac.uk\n"+
        "Please type a for answering inquiry or type r for redirecting inquiry\n";
        assertEquals(expectedOutput, out.toString().replaceAll("\r\n", "\n"));
    }

}

