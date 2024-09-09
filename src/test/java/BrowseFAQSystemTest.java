import controller.AdminStaffController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.FAQSection;
import model.SharedContext;


import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;
import view.TextUserInterface;
import view.View;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class BrowseFAQSystemTest {

    private static final PrintStream originalOut = System.out;

    private static ByteArrayOutputStream out = new ByteArrayOutputStream();

    private static SharedContext sharedContext;

    private static AdminStaffController adminController;

    @BeforeEach
    public void setUp() throws IOException, URISyntaxException, ParseException {
        sharedContext = new SharedContext();
        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk", "AdminStaff");
        sharedContext.setCurrentUser(testUser);
    }

    @AfterEach
    void reset() throws IOException {
        out = new ByteArrayOutputStream();
        System.setOut(originalOut);
        System.in.reset();
    }


    @Test
    void AnyVariableOtherThanIntegerShouldReturnNumberFormatException() throws Exception{
        var userInput = "Pancakes";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(in);
        setUp();
        assertThrows(NumberFormatException.class,
                () -> adminController.manageFAQ(),
                "Invalid option: Pancakes");
    }

    @Test
    void UserInputWithinBoundaries() throws Exception{
        var userInput = "100";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(in);
        setUp();
        assertThrows(IndexOutOfBoundsException.class,
                () -> adminController.manageFAQ(),
                "Invalid option: 100");
    }

    @Test
    void AdminAbleToSeeAllSections() throws Exception {
        FAQSection firstNewSection = new FAQSection("Fruits enjoyers");
        FAQSection secondNewSection = new FAQSection("Desserts enjoyers");
        FAQSection thirdNewSection = new FAQSection("Pancake enjoyers");

        sharedContext.getFAQ().addSection(firstNewSection);
        sharedContext.getFAQ().addSection(secondNewSection);
        sharedContext.getFAQ().addSection(thirdNewSection);

        String userInput = "-1";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(in);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        adminController = new AdminStaffController(sharedContext, view, authService, emailService);
        adminController.manageFAQ();


        String expectedOutput = "[2] Topic: Fruits enjoyers\n" +
                "[1] Topic: Desserts enjoyers\n" +
                "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "[-2] to add a section\n" +
                "Please choose an option\n";
        Assertions.assertEquals(expectedOutput, out.toString().replaceAll("\r\n","\n"));
    }
    @Test
    void AdminAbleToSelectASectionAndSeeSubsections() throws IOException, URISyntaxException, ParseException{

        FAQSection newSection = new FAQSection("Pancake enjoyers");
        FAQSection newSubsection = new FAQSection("Pancake recipes");
        sharedContext.getFAQ().addSection(newSection);
        newSection.addSubsection(newSubsection);

        String userInput = "0\n" +
                "-1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(in);

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        adminController = new AdminStaffController(sharedContext, view, authService, emailService);
        adminController.manageFAQ();


        String expectedOutput = "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "[-2] to add a section\n" +
                "Please choose an option\n" +
                "[Current Topic] Pancake enjoyers\n" +
                "[0] [Subtopic_0] Pancake recipes\n" +
                "[-1] to return to FAQ\n" +
                "[-2] to add new subsection\n" +
                "[-3] to add new QA pair\n" +
                "Please choose an option\n";
        Assertions.assertEquals(expectedOutput, out.toString().replaceAll("\r\n","\n"));
    }
}