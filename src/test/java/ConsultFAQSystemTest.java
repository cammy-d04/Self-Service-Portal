import controller.InquirerController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.FAQSection;
import model.Guest;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;
import view.View;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;


/**
 * Tests the functionality of consulting the FAQ system as different types of users (e.g., students, guests).
 * This includes verifying that both authenticated users and guests can view all sections and subsections
 * of the FAQ, and can register for updates on specific FAQ topics.

 * Each test simulates the actions a user might take when interacting with the FAQ system, such as viewing
 * different sections, registering for updates, and verifying that the correct information is displayed or
 * that the correct actions are taken based on their inputs.
 */
public class ConsultFAQSystemTest {
    private static final PrintStream originalOut = System.out;

    private static ByteArrayOutputStream out = new ByteArrayOutputStream();
    private static final InputStream originalIn = System.in;

    private static SharedContext sharedContext;

    private static InquirerController inquirerController;

    /**
     * Prepares the testing environment by setting up the shared context before each test.
     *
     * @throws IOException If an I/O error occurs.
     * @throws URISyntaxException If a URI syntax error occurs.
     * @throws ParseException If parsing errors occur.
     */
    @BeforeEach
    public void setUp() throws IOException, URISyntaxException, ParseException {
        sharedContext = new SharedContext();
    }


    /**
     * Resets the system's input and output streams after each test to ensure no test interferes with another.
     *
     * @throws IOException If an I/O error occurs during reset.
     */
    @AfterEach
    void reset() throws IOException {
        out = new ByteArrayOutputStream();
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /**
     * Helper method to set up the shared context with predefined FAQ sections for tests that require viewing sections.
     *
     * @return The ByteArrayInputStream that simulates user input for navigating the FAQ system.
     */
    private static ByteArrayInputStream SeeAllsectionsSetUp() {
        FAQSection firstNewSection = new FAQSection("Fruits enjoyers");
        FAQSection secondNewSection = new FAQSection("Desserts enjoyers");
        FAQSection thirdNewSection = new FAQSection("Pancake enjoyers");

        var faq = sharedContext.getFAQ();

        faq.addSection(firstNewSection);
        faq.addSection(secondNewSection);
        faq.addSection(thirdNewSection);

        String userInput = "-1";
        return new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Helper method to set up the shared context with predefined FAQ sections and subsections for tests that require viewing subsections.
     */
    private static void SeeAllSubsectionsSetup(){

        FAQSection firstNewSection = new FAQSection("Fruits enjoyers");
        FAQSection secondNewSection = new FAQSection("Desserts enjoyers");
        FAQSection thirdNewSection = new FAQSection("Pancake enjoyers");

        var faq = sharedContext.getFAQ();
        faq.addSection(firstNewSection);
        faq.addSection(secondNewSection);
        faq.addSection(thirdNewSection);

        FAQSection firstSubsection = new FAQSection("Berry fruits enjoyers");
        FAQSection secondSubsection = new FAQSection("Cakes enjoyers");
        FAQSection thirdSubsection = new FAQSection("American pancakes enjoyers");

        faq.getSections().get(0).addSubsection(firstSubsection);
        faq.getSections().get(1).addSubsection(secondSubsection);
        faq.getSections().get(2).addSubsection(thirdSubsection);


    }

    /**
     * Tests that an authenticated student user can view all top-level FAQ sections.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
    @Test
    void StudentAbleToSeeAllSections() throws Exception {

        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk", "Student");
        sharedContext.setCurrentUser(testUser);

        ByteArrayInputStream in = SeeAllsectionsSetUp();
        System.setIn(in);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        inquirerController = new InquirerController(sharedContext, view, authService, emailService);
        inquirerController.consultFAQ();


        String expectedOutput = "[2] Topic: Fruits enjoyers\n" +
                "[1] Topic: Desserts enjoyers\n" +
                "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "Please choose an option\n";
        Assertions.assertEquals(expectedOutput, out.toString().replaceAll("\r\n","\n"));
    }

    /**
     * Tests that a guest user can view all top-level FAQ sections.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
    @Test
    void GuestAbleToSeeAllSections() throws Exception {

        Guest testUser = new Guest();
        sharedContext.setCurrentUser(testUser);

        ByteArrayInputStream in = SeeAllsectionsSetUp();
        System.setIn(in);

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        inquirerController = new InquirerController(sharedContext, view, authService, emailService);
        inquirerController.consultFAQ();


        String expectedOutput = "[2] Topic: Fruits enjoyers\n" +
                "[1] Topic: Desserts enjoyers\n" +
                "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "Please choose an option\n";
        Assertions.assertEquals(expectedOutput, out.toString().replaceAll("\r\n","\n"));
    }

    /**
     * Tests that an authenticated student user can view all subsections of a selected FAQ section.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
    @Test
    void StudentAbleToSeeAllSubsections() throws Exception {

        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk", "Student");
        sharedContext.setCurrentUser(testUser);

       SeeAllSubsectionsSetup();

        String userInput = "2\n" +
                "-1\n" +
                "-1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(in);

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        inquirerController = new InquirerController(sharedContext, view, authService, emailService);
        inquirerController.consultFAQ();


        String expectedOutput = "[2] Topic: Fruits enjoyers\n" +
                "[1] Topic: Desserts enjoyers\n" +
                "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "Please choose an option\n" +
                "[Current Topic] Fruits enjoyers\n" +
                "[0] [Subtopic_0] Berry fruits enjoyers\n" +
                "[-1] to return to FAQ\n" +
                "[-2] to request updates for this topic\n" +
                "Please choose an option\n" +
                "[2] Topic: Fruits enjoyers\n" +
                "[1] Topic: Desserts enjoyers\n" +
                "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "Please choose an option\n";

        Assertions.assertEquals(expectedOutput, out.toString().replaceAll("\r\n","\n"));
    }

    /**
     * Tests that a guest user can view all subsections of a selected FAQ section.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
    @Test
    void GuestsAbleToSeeAllSubsections() throws Exception {

        Guest testUser = new Guest();
        sharedContext.setCurrentUser(testUser);

        SeeAllSubsectionsSetup();

        String userInput = "2\n" +
                "-1\n" +
                "-1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(in);

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        inquirerController = new InquirerController(sharedContext, view, authService, emailService);
        inquirerController.consultFAQ();

        String expectedOutput = "[2] Topic: Fruits enjoyers\n" +
                "[1] Topic: Desserts enjoyers\n" +
                "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "Please choose an option\n" +
                "[Current Topic] Fruits enjoyers\n" +
                "[0] [Subtopic_0] Berry fruits enjoyers\n" +
                "[-1] to return to FAQ\n" +
                "[-2] to request updates for this topic\n" +
                "[-3] to stop receiving updates for this topic\n" +
                "Please choose an option\n" +
                "[2] Topic: Fruits enjoyers\n" +
                "[1] Topic: Desserts enjoyers\n" +
                "[0] Topic: Pancake enjoyers\n" +
                "[-1] to return to main menu\n" +
                "Please choose an option\n";

        Assertions.assertEquals(expectedOutput, out.toString().replaceAll("\r\n","\n"));
    }

    /**
     * Tests that a guest user can successfully register for updates on a specific FAQ topic.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
    @Test
    void GuestsAbleToRegisterForFAQ() throws Exception{
        Guest testUser = new Guest();
        sharedContext.setCurrentUser(testUser);

        SeeAllSubsectionsSetup();

        String userInput = "2\n" +
                "-2\n" +
                "test@hindeburg.ac.uk\n" +
                "-1\n" +
                "-1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(in);

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        inquirerController = new InquirerController(sharedContext, view, authService, emailService);
        inquirerController.consultFAQ();

        assertAll("Grouped Assertions of Requesting Updates as a Guest",
                () -> Assertions.assertTrue(out.toString().replaceAll("\r\n","\n").contains("SUCCESS: " +
                "Successfully registered test@hindeburg.ac.uk for updates on Fruits enjoyers\n"),
                "The success message should be display if succesfully registered for updates"),
                () -> Assertions.assertTrue(sharedContext.usersSubscribedToFAQTopic("Fruits enjoyers").contains("test@hindeburg.ac.uk"),
                        "Email should be within the generated hash map for subscribers")

        );
    }

    /**
     * Tests that an authenticated student user can successfully register for updates on a specific FAQ topic.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
    @Test
    void StudentAbleToRegisterForFAQ() throws Exception{
        AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk", "Student");
        sharedContext.setCurrentUser(testUser);

        SeeAllSubsectionsSetup();

        String userInput = "2\n" +
                "-2\n" +
                "-1\n" +
                "-1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(in);

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        View view = new TextUserInterface();
        AuthenticationService authService = new MockAuthenticationService();
        EmailService emailService = new MockEmailService();
        inquirerController = new InquirerController(sharedContext, view, authService, emailService);
        inquirerController.consultFAQ();

        assertAll("Grouped Assertions of Requesting Updates as a Guest",
                () -> Assertions.assertTrue(out.toString().replaceAll("\r\n","\n").contains("SUCCESS: " +
                                "Successfully registered test@hindeburg.ac.uk for updates on Fruits enjoyers\n"),
                        "The success message should be display if succesfully registered for updates"),
                () -> Assertions.assertTrue(sharedContext.usersSubscribedToFAQTopic("Fruits enjoyers").contains("test@hindeburg.ac.uk"),
                        "Email should be within the generated hash map for subscribers")

        );
    }
}
