import controller.AdminStaffController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.FAQSection;
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

import static org.junit.jupiter.api.Assertions.assertAll;


/**
 * Tests the functionality of adding question and answer pairs to the FAQ system by an admin user.
 * It verifies that new FAQ sections can be added, and within these sections,
 * new question and answer pairs can be correctly inserted.
 * The test simulates user input to navigate through the admin interface of the FAQ management system,
 * adding new QA pairs and validating the outcome.
 */
public class AddFAQQASystemTest {

        private static final PrintStream originalOut = System.out;

        private static ByteArrayOutputStream out = new ByteArrayOutputStream();
        private static final InputStream originalIn = System.in;

        private static SharedContext sharedContext;

        private static AdminStaffController adminController;

    /**
     * Sets up the test environment before each test method.
     * This includes initializing the shared context, setting the current user as an authenticated admin user,
     * and configuring system input and output streams for test interaction and verification.
     *
     * @throws IOException If an I/O error occurs.
     * @throws URISyntaxException If a string could not be parsed as a URI reference.
     * @throws ParseException If the response could not be parsed.
     */
        @BeforeEach
        public void setUp() throws IOException, URISyntaxException, ParseException {
            sharedContext = new SharedContext();
            AuthenticatedUser testUser = new AuthenticatedUser("test@hindeburg.ac.uk", "AdminStaff");
            sharedContext.setCurrentUser(testUser);
        }

    /**
     * Resets the system output and input streams to their original values after each test method.
     * This is necessary to ensure that changes to the streams do not affect subsequent tests.
     *
     * @throws IOException If an I/O error occurs during the reset.
     */
        @AfterEach
        void reset() throws IOException {
            out = new ByteArrayOutputStream();
            System.setOut(originalOut);
            System.setIn(originalIn);
        }


    /**
     * Tests that an admin user is able to add question and answer pairs to the FAQ system.
     * The test simulates the process of adding three new sections to the FAQ,
     * and within one of these sections, adding a new question and answer pair.
     *
     * The test validates the addition by checking the FAQ's state before and after the operation,
     * and by examining the system's output to confirm that the operation's success message is displayed.
     *
     * @throws Exception If an unexpected exception occurs during the test.
     */
        @Test
        void AdminAbleToAddQA() throws Exception {
            FAQSection firstNewSection = new FAQSection("Fruits enjoyers");
            FAQSection secondNewSection = new FAQSection("Desserts enjoyers");
            FAQSection thirdNewSection = new FAQSection("Pancake enjoyers");

            var faq = sharedContext.getFAQ();

            faq.addSection(firstNewSection);
            faq.addSection(secondNewSection);
            faq.addSection(thirdNewSection);


            String userInput = "2\n" +
                    "-2\n" +
                    "no\n" +
                    "Which berry fruit is your favourite?\n" +
                    "I love blueberries!\n" +
                    "-1\n" +
                    "-1\n";

            ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes());

            System.setIn(in);

            View view = new TextUserInterface();

            AuthenticationService authService = new MockAuthenticationService();
            EmailService emailService = new MockEmailService();

            adminController = new AdminStaffController(sharedContext, view, authService, emailService);

            out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));

            adminController.manageFAQ();


            String question = faq.getSections().get(0).getFAQItems().get(0).getQuestion();
            String answer = faq.getSections().get(0).getFAQItems().get(0).getAnswer();

            String expectedOutput = "[2] Topic: Fruits enjoyers\n" +
                    "[1] Topic: Desserts enjoyers\n" +
                    "[0] Topic: Pancake enjoyers\n" +
                    "[-1] to return to main menu\n" +
                    "[-2] to add new QA pair\n" +
                    "Please choose an option\n" +
                    "[Current Topic] Fruits enjoyers\n" +
                    "[-1] to return to FAQ\n" +
                    "[-2] to add new QA pair\n" +
                    "Please choose an option\n" +
                    "Would you like to add a subsection?\n"+
                    "Please enter the (sub)section's question: \n" +
                    "Please enter the (sub)section's answer: \n" +
                    "\u001B[36mEmail from test@hindeburg.ac.uk to admin@hindeburg.ac.uk\n"+
                    "FAQ topic update: [Topic] Fruits enjoyers\n"+
                    "Updated Questions and Answers: [Question_0]: Which berry fruit is your favourite?\n" +
                    " \t\t\t\t\t\t\t\t[Answer_0]: I love blueberries!\n" +
                    " \t\t\t\t\t\t\t\t\n" +
                    "\u001B[0mSUCCESS: Successfully added question: " +
                    "\"Which berry fruit is your favourite?\" and answer: " +
                    "\"I love blueberries!\" to the topic: Fruits enjoyers\n" +
                    "[Current Topic] Fruits enjoyers\n" +
                    "    [Question_0] Which berry fruit is your favourite?\n" +
                    "\n" +
                    "    [Answer_0] I love blueberries!\n" +
                    "[-1] to return to FAQ\n" +
                    "[-2] to add new QA pair\n" +
                    "Please choose an option\n" +
                    "[2] Topic: Fruits enjoyers\n" +
                    "[1] Topic: Desserts enjoyers\n" +
                    "[0] Topic: Pancake enjoyers\n" +
                    "[-1] to return to main menu\n" +
                    "[-2] to add new QA pair\n" +
                    "Please choose an option\n";

            assertAll("Grouped Assertions of Adding QA",
                    () ->  Assertions.assertEquals(expectedOutput,
                            out.toString().replaceAll("\r\n","\n"), "Admin should be able to view the changes"),
                    () ->  Assertions.assertEquals("Which berry fruit is your favourite?",
                            question, "Correct question added to the FAQ"),
                    () ->  Assertions.assertEquals("I love blueberries!",
                            answer, "Correct answer added to the FAQ"),
                    () ->  Assertions.assertTrue(out.toString().replaceAll("\r\n","\n").contains(
                            "\u001B[0mSUCCESS: Successfully added question: " +
                            "\"Which berry fruit is your favourite?\" and answer: " +
                            "\"I love blueberries!\" to the topic: Fruits enjoyers\n") ,
                            "The output should contain the confirmation of successful addition")
            );


        }
    }
