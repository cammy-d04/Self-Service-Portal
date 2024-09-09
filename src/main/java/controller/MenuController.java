package controller;


import external.MockAuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import view.View;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class MenuController extends Controller{
    public enum GuestMainMenuOption {
        LOGIN ,CONSULT_FAQ, SEARCH_PAGES, CONTACT_STAFF
    }
    public enum TeachingStaffMainMenuOption {
        LOGOUT, MANAGE_RECEIVED_QUERIES
    }
    public enum StudentMainMenuOption {
        LOGOUT, CONSULT_FAQ, SEARCH_PAGES, CONTACT_STAFF
    }
    public enum AdminStaffMainMenuOption {
        LOGOUT, MANAGE_QUERIES, ADD_PAGE, SEE_ALL_PAGES, MANAGE_FAQ
    }

    private GuestController guestController;
    private InquirerController inquirerController;
    private TeachingStaffController teachingStaffController;
    private AdminStaffController adminStaffController;
    private AuthenticatedUserController authenticatedUserController;


    public MenuController(SharedContext sharedContext, View view,
                          MockAuthenticationService authenticationService,
                          EmailService emailService){

        super(sharedContext, view, authenticationService, emailService);

        guestController = new GuestController(sharedContext, view, authenticationService, emailService);
        inquirerController = new InquirerController(sharedContext, view, authenticationService, emailService);
        teachingStaffController = new TeachingStaffController(sharedContext, view, authenticationService, emailService);
        adminStaffController = new AdminStaffController(sharedContext, view, authenticationService, emailService);
        authenticatedUserController = new AuthenticatedUserController(sharedContext, view, authenticationService, emailService);
    }

    private boolean handleGuestMainMenu() throws FileNotFoundException {

        List<GuestMainMenuOption> options = Arrays.asList(GuestMainMenuOption.values());
        int choice = selectFromMenu(options);

        switch(choice){

            case 1:
                guestController.login();
                mainMenu();
                break;

            case 2:
                inquirerController.consultFAQ();
                mainMenu();
                break;

            case 3:
                inquirerController.searchPages();
                mainMenu();
                break;

            case 4:
                inquirerController.contactStaff();
                mainMenu();
                break;
        }

        return true;
    }

    private boolean handleStudentMainMenu() throws FileNotFoundException {
        List<StudentMainMenuOption> options = Arrays.asList(StudentMainMenuOption.values());
        int choice = selectFromMenu(options);

        switch(choice){

            case 1:
                authenticatedUserController.logout();
                mainMenu();
                break;

            case 2:
                inquirerController.consultFAQ();
                mainMenu();
                break;

            case 3:
                inquirerController.searchPages();
                mainMenu();
                break;

            case 4:
                inquirerController.contactStaff();
                mainMenu();
                break;
        }

        return true;
    }
    private boolean handleTeachingStaffMainMenu() throws FileNotFoundException {
        List<TeachingStaffMainMenuOption> options = Arrays.asList(TeachingStaffMainMenuOption.values());
        int choice = selectFromMenu(options);

        switch (choice) {

            case 1:
                authenticatedUserController.logout();
                mainMenu();
                break;

            case 2:
                teachingStaffController.manageReceivedInquiries();
                mainMenu();
                break;

        }
        return true;
    }

    private boolean handleAdminStaffMainMenu() throws FileNotFoundException {
        List<AdminStaffMainMenuOption> options = Arrays.asList(AdminStaffMainMenuOption.values());
        int choice = selectFromMenu(options);

        switch(choice){

            case 1:
                authenticatedUserController.logout();
                mainMenu();
                break;

            case 2:
                adminStaffController.manageInquiry();
                mainMenu();
                break;

            case 3:
                adminStaffController.addPage();
                mainMenu();
                break;

            case 4:
                adminStaffController.viewAllPages();
                mainMenu();
                break;

            case 5:
                adminStaffController.manageFAQ();
                mainMenu();
                break;
        }

        return true;
        }




    public void mainMenu() throws FileNotFoundException {

        if (sharedContext.getCurrentUser() instanceof AuthenticatedUser) {
            //call the appropriate handle...MainMenu() function based on the user's role
            switch (((AuthenticatedUser) sharedContext.getCurrentUser()).getRole()) {

                case "Student":
                    handleStudentMainMenu();
                    break;
                case "TeachingStaff":
                    handleTeachingStaffMainMenu();
                    break;
                case "AdminStaff":
                    handleAdminStaffMainMenu();
                    break;
            }
        }else{
            handleGuestMainMenu();
        }
    }



}
