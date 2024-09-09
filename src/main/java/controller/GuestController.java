package controller;

import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import view.View;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class GuestController extends Controller {

    private AuthenticatedUser currentUser;

    public GuestController(SharedContext sharedContext, View view,
                           MockAuthenticationService authenticationService,
                           EmailService emailService) {
        super(sharedContext, view, authenticationService, emailService);
    }

    public void login() {
        JSONParser parser = new JSONParser();

        String[] allowedRoles = {"Student", "AdminStaff", "TeachingStaff"};
        String usernamePrompt = "Please enter your username.";
        String passwordPrompt = "Please enter your password.";

        String username = view.getInput(usernamePrompt);
        String password = view.getInput(passwordPrompt);

        try {
            //parse JSON string to JSON Object
            Object userObject = parser.parse(authenticationService.login(username, password));
            JSONObject user = (JSONObject) userObject;

            // check if authService returned an error
            if (user.containsKey("error")) {
                view.displayError((String) user.get("error"));
                return;
            }

            String jsonEmail = (String) user.get("email");
            String jsonRole = (String) user.get("role");


            if (jsonEmail == null) {
                throw new IllegalArgumentException("User email cannot be null.");
            }

            if (!Arrays.asList(allowedRoles).contains(jsonRole)) {
                throw new IllegalArgumentException("Unsupported user role.");
            }

            //set current user to new logged in user
            currentUser = new AuthenticatedUser(jsonEmail, jsonRole);
            sharedContext.setCurrentUser(currentUser);

            view.displaySuccess("Log in successful.");

        } catch (ParseException | IllegalArgumentException e) {
            view.displayException(e);
        }
    }
}