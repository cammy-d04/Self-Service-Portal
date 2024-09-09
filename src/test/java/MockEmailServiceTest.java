import external.EmailService;
import external.MockEmailService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.lang.System.setOut;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockEmailServiceTest {
    private static EmailService service;
    private static PrintStream originalOut = System.out;
    @BeforeAll
    static void setUp(){
        service = new MockEmailService();
        //hide output from prints
        setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterAll
    static void returnOut(){
        setOut(originalOut);
    }

    @Test
    void successfulEmail(){
        String sender = "test@hindeburg.ac.uk";
        String receiver = "test2@hindeburg.ac.uk";
        String subject = "subject";
        String content = "content";
        assertEquals(0,service.sendEmail(sender,receiver,subject,content));
    }

    @Test
    void invalidSenderNoMatch(){
        String receiver = "test2@hindeburg.ac.uk";
        String subject = "subject";
        String content = "content";
        assertAll(
                () -> assertEquals(1,service.sendEmail("test@@hindeburg.ac" +
                                ".uk",receiver,
                        subject,content)),
                () -> assertEquals(1,service.sendEmail("test",receiver,subject,
                        content)),
                () -> assertEquals(1,service.sendEmail("@hindeburg.ac.uk",
                        receiver,subject, content)),
                () -> assertEquals(1,service.sendEmail("test@hindeburg",
                        receiver,subject,content)),
                () -> assertEquals(1,service.sendEmail("test@hindeburg.ac.u",
                        receiver, subject, content)),
                () -> assertEquals(1,service.sendEmail("!@hindeburg.ac.uk",
                        receiver,
                        subject, content)),
                () -> assertEquals(1,service.sendEmail("",receiver,subject,
                        content)),
                () -> assertEquals(1,service.sendEmail("a.@hindeburg.ac.uk",
                        receiver,subject,content)),
                () -> assertEquals(1,service.sendEmail(".a@hindeburg.ac.uk",
                        receiver,subject,
                        content))
        );

    }

    @Test
    void invalidRecipientNoMatch(){
        String sender = "test@hindeburg.ac.uk";
        String receiver = "test2@hindeburg.ac.u";
        String subject = "subject";
        String content = "content";
        assertAll(
                () -> assertEquals(2,service.sendEmail(sender,"test" +
                                "@@hindeburg" +
                                ".ac" +
                                ".uk",
                        subject,content)),
                () -> assertEquals(2,service.sendEmail(sender,"test",subject,
                        content)),
                () -> assertEquals(2,service.sendEmail(sender,"@hindeburg.ac" +
                                ".uk",
                        subject, content)),
                () -> assertEquals(2,service.sendEmail(sender,"test@hindeburg",
                        subject,content)),
                () -> assertEquals(2,service.sendEmail(sender,"test@hindeburg" +
                                ".ac.u",
                        subject, content)),
                () -> assertEquals(2,service.sendEmail(sender,"!@hindeburg.ac" +
                                ".uk",
                        subject, content)),
                () -> assertEquals(2,service.sendEmail(sender,"",subject,
                        content)),
                () -> assertEquals(2,service.sendEmail(sender,"a.@hindeburg" +
                                ".ac.uk",
                        subject,content)),
                () -> assertEquals(2,service.sendEmail(sender,".a@hindeburg" +
                                ".ac.uk",
                        subject,
                        content))
        );

    }

    @Test
    void invalidSenderNull(){
        String sender = null;
        String receiver = "test2@hindeburg.ac.uk";
        String subject = "subject";
        String content = "content";
        assertEquals(1,service.sendEmail(sender,receiver,subject,content));
    }

    @Test
    void invalidRecipientNull(){
        String sender = "test@hindeburg.ac.uk";
        String receiver = null;
        String subject = "subject";
        String content = "content";
        assertEquals(2,service.sendEmail(sender,receiver,subject,content));
    }
}
