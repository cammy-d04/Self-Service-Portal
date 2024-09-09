package controller;

import external.AuthenticationService;
import external.EmailService;
import model.Guest;
import model.SharedContext;
import view.View;

public class AuthenticatedUserController extends Controller{

    private Guest currentUser;

    public AuthenticatedUserController(SharedContext sharedContext,
                                       View view,
                                       AuthenticationService authenticationService,
                                       EmailService emailService) {
        super(sharedContext, view, authenticationService, emailService);
    }

    public void logout(){

        currentUser = new Guest();
        sharedContext.setCurrentUser(currentUser);

        view.displaySuccess("Log out successful");

    }


}
