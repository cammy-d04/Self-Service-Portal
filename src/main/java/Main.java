import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import view.TextUserInterface;
import view.View;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {

        SharedContext sharedContext = new SharedContext();
        TextUserInterface textUserInterface = new TextUserInterface();
        MockEmailService mockEmailService = new MockEmailService();
        MockAuthenticationService mockAuthenticationService = new MockAuthenticationService();
        MenuController menuController = new MenuController(sharedContext, textUserInterface, mockAuthenticationService, mockEmailService);


        menuController.mainMenu();
    }
}
