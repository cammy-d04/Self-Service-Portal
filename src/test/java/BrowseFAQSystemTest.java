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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the functionality related to browsing the FAQ system within the application.
 * This includes verifying the behavior of the system when handling both valid and invalid inputs during navigation,
 * the visibility of FAQ sections and subsections to admin users, and the system's response to navigation commands.
 */
public class BrowseFAQSystemTest {

    private static final PrintStream originalOut = System.out;

    private static ByteArrayOutputStream out = new ByteArrayOutputStream();

    private static SharedContext sharedContext;

    private static AdminStaffController adminController;

    /**
     * Initializes the test environment before each test method.
     * This setup includes creating a shared context, setting a test user as the current user,
     * and preparing the system's input and output streams for capturing and simulating user interactions.
     *
     * @throws IOException If an I/O error occurs.
     * @throws URISyntaxException If a URI syntax error occurs.
     * @throws ParseException If parsing errors occur.
     */
    @BeforeEach
    public void setUp() throws IOException, URISyntaxException, ParseException {
        sharedContext = new SharedContext();
        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk", "AdminStaff");
        sharedContext.setCurrentUser(testUser);
    }

    /**
     * Resets the test environment after each test method.
     * This includes resetting the system's input and output streams to their original state
     * to avoid interference with other tests.
     *
     * @throws IOException If an I/O error occurs during the reset.
     */
    @AfterEach
    void reset() throws IOException {
        out = new ByteArrayOutputStream();
        System.setOut(originalOut);
        System.in.reset();
    }

    /**
     * Tests that entering a non-integer input for navigation options throws a NoSuchElementException.
     * This simulates the scenario where a user enters an invalid option, such as a string,
     * when prompted to choose a navigation option in the FAQ system.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
    @Test
    void AnyVariableOtherThanIntegerShouldReturnNumberFormatException() throws Exception{
        var userInput = "Pancakes";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(in);
        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        adminController = new AdminStaffController(sharedContext, view, authService, emailService);
        assertThrows(NoSuchElementException.class,
                () -> adminController.manageFAQ(),
                "Invalid option: Pancakes");
    }

    /**
     * Verifies that an admin user can view all top-level FAQ sections.
     * This test adds several sections to the FAQ system and then simulates an admin user accessing
     * the FAQ management interface to ensure that all added sections are visible and correctly listed.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
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
                "[-2] to add new QA pair\n" +
                "Please choose an option\n";
        Assertions.assertEquals(expectedOutput, out.toString().replaceAll("\r\n","\n"));
    }

    /**
     * Tests that an admin user can navigate to a specific FAQ section and view its subsections.
     * This test adds a section with a subsection to the FAQ system and simulates an admin user navigating
     * to this section to verify that the subsection is correctly listed and accessible.
     *
     * @throws IOException If an I/O error occurs.
     * @throws URISyntaxException If a URI syntax error occurs.
     * @throws ParseException If parsing errors occur.
     */
    @Test
    void AdminAbleToSelectASectionAndSeeSubsections() throws IOException, URISyntaxException, ParseException{

        FAQSection newSection = new FAQSection("Pancake enjoyers");
        FAQSection newSubsection = new FAQSection("Pancake recipes");
        sharedContext.getFAQ().addSection(newSection);
        newSection.addSubsection(newSubsection);

        String userInput = "0\n" +
                "-1\n" +
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
                "[-2] to add new QA pair\n" +
                "Please choose an option\n" +
                "[Current Topic] Pancake enjoyers\n" +
                "[0] [Subtopic_0] Pancake recipes\n" +
                "[-1] to return to FAQ\n" +
                "[-2] to add new QA pair\n" +
                "Please choose an option\n" +
                "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "[-2] to add new QA pair\n" +
                "Please choose an option\n";
        Assertions.assertEquals(expectedOutput, out.toString().replaceAll("\r\n","\n"));
    }
}