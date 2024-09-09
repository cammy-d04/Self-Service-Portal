package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import org.apache.lucene.queryparser.classic.ParseException;
import view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.List;

public class InquirerController extends Controller{




    public InquirerController(SharedContext sharedContext, View view,
                              AuthenticationService authenticationService,
                              EmailService emailService) {
        super(sharedContext,view,authenticationService,emailService);
        //may need more stuff here, depending on other methods.
    }


    /**
     * Facilitates user interaction with the FAQ system, allowing them to browse FAQ sections,
     * request updates on specific topics, or stop receiving updates. This method supports both
     * authenticated users and guests, adjusting available options accordingly.
     * For authenticated users, the email associated with their account is used for requesting or
     * stopping updates. Guests must provide an email address for these actions.
     * The method displays the main FAQ or a specific section based on user navigation. It offers
     * options to return to the previous menu, request updates, or stop receiving updates on topics.
     * Users navigate through the FAQ by entering option numbers corresponding to their choices.
     * Invalid input handling ensures users can only make selections that correspond to available
     * options or actions. The process repeats until the user chooses to return to the main menu
     * from the top level of the FAQ.
     * Uses the shared context to access current user details, FAQ data, and subscription information.
     */
    public void consultFAQ() {
        int optionNo;
        FAQSection currentSection = null;
        String userEmail = null;
        User currentUser = sharedContext.getCurrentUser();

        if (currentUser instanceof AuthenticatedUser) {
            userEmail = ((AuthenticatedUser) currentUser).getEmail();
        }

        do {
            if (currentSection == null) {
                FAQ faq = sharedContext.getFAQ();
                view.displayFAQ(faq);
                view.displayInfo("[-1] to return to main menu");
            } else {
                view.displayFAQSection(currentSection);
                FAQSection parent = currentSection.getParent();

                if (parent == null) {
                    view.displayInfo("[-1] to return to FAQ");
                } else {
                    String topic = parent.getTopic();
                    view.displayInfo("[-1] to return to " + topic);
                }

                if (currentUser instanceof Guest) {
                    view.displayInfo("[-2] to request updates for this topic");
                    view.displayInfo("[-3] to stop receiving updates for this topic");
                } else {
                    String topic = currentSection.getTopic();
                    Collection<String> subscribers = sharedContext.usersSubscribedToFAQTopic(topic);
                    if (subscribers.contains(userEmail)) {
                        view.displayInfo("[-2] to stop receiving updates for this topic");
                    } else {
                        view.displayInfo("[-2] to request updates for this topic");
                    }
                }
            }

            String input = view.getInput("Please choose an option");

            while (true){
                try{
                    Integer.parseInt(input);
                    break;
                }
                catch (InputMismatchException | NumberFormatException nfe) {
                    view.displayError("Invalid option: " + input);
                    input = view.getInput("Please choose an option");
                }
            }

            optionNo = Integer.parseInt(input);

            if (optionNo != -1 && optionNo != - 2 && optionNo != - 3){
                ArrayList<FAQSection> sections;
                if (currentSection == null){
                    FAQ faq = sharedContext.getFAQ();
                    sections = faq.getSections();
                }
                else{
                    sections = currentSection.getSubsections();
                }
                if (optionNo>sections.size()){
                    view.displayError("Invalid option: " + optionNo);
                    input = view.getInput("Please choose an option: ");
                    boolean correctInput = false;
                    while (optionNo>sections.size() && !correctInput){
                        try{
                            Integer.parseInt(input);
                            correctInput = true;
                        }
                        catch (InputMismatchException | NumberFormatException nfe) {
                            view.displayError("Invalid option: " + input);
                            input = view.getInput("Please choose an option");
                        }
                    }
                }
                else {
                    currentSection = sections.get(sections.size()-optionNo-1);
                }
            }

            if (currentSection != null) {
                    String topic = currentSection.getTopic();
                    if (currentUser instanceof Guest) {
                        if (optionNo == -2) {
                            String guestEmail = view.getInput("Please enter your email to receive FAQ updates");
                            requestFAQUpdates(guestEmail, topic);
                        } else if (optionNo == -3) {
                            String guestEmail = view.getInput("Please enter your email to receive FAQ updates");
                            stopFAQUpdates(guestEmail, topic);
                        }
                    }
                    if (currentUser instanceof AuthenticatedUser && optionNo == -2) {
                        Collection<String> subscribers = sharedContext.usersSubscribedToFAQTopic(topic);
                        userEmail = ((AuthenticatedUser) currentUser).getEmail();

                        if (subscribers.contains(userEmail)) {
                            stopFAQUpdates(userEmail, topic);
                        } else {
                            requestFAQUpdates(userEmail, topic);
                        }
                    } else if (optionNo == -1) {
                        currentSection = currentSection.getParent();
                        optionNo = 0;
                    }
                }
        } while (!(currentSection == null && optionNo == -1));
    }
    

    public void searchPages() {
        List<Page> pageList = new ArrayList<>(List.copyOf(super.sharedContext.getPageList()));
        if (super.sharedContext.getCurrentUser() instanceof Guest){
            pageList.removeIf(Page::isPrivate);
        }
        boolean validQuery = false;
        while (!validQuery){
            try {
                String query = super.view.getInput("Please enter your query:");
                if (query.isEmpty()){
                    view.displayWarning("Empty Query");
                    continue;
                }
                PageSearch searcher = new PageSearch(pageList);
                List<PageSearchResult> results = searcher.search(query);
                validQuery = true;
                if (results == null || results.isEmpty()){
                    super.view.displayError("No Results Found, please try a different query");
                    super.view.displayDivider();
                    return;
                }
                super.view.displaySuccess("Results Found");
                super.view.displaySearchResults(results);
                super.view.displayDivider();
                return;
            }
            catch (ParseException | IOException exception){
                if (exception instanceof ParseException){
                    view.displayError("Cannot parse your query. Please enter a correctly formatted query.");
                    continue;
                }
                super.view.displayException(exception);
                break;
            }
        }

    }
    public void contactStaff(){
        String subject =super.view.getInput("Please enter the subject of your inquiry:");
        while(subject == null || subject.trim().isEmpty()){
            subject = super.view.getInput("A subject must be provided for the inquiry.");
        }
        String content =super.view.getInput("Please enter the content of your inquiry:");
        while(content == null || content.trim().isEmpty()){
            content = super.view.getInput("A content must be provided for the inquiry.");
        }
        String email;

        if(sharedContext.getCurrentUser() instanceof Guest){
            email =  view.getInput("Please enter your email address:");
            while(email == null || email.trim().isEmpty()){
                email = super.view.getInput("A email must be provided for the inquiry.");
            }
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            while(!email.matches(emailRegex)){
                email = super.view.getInput("Please enter your email address in a correct form:");
            }

        }
        else{
            email=((AuthenticatedUser)sharedContext.getCurrentUser()).getEmail();
        }

        Inquiry inquiry =  new Inquiry(subject, content, email);
        List<Inquiry> newinquirylist = sharedContext.getInquiryList();
        newinquirylist.add(inquiry);
        sharedContext.setInquiryList(newinquirylist);
        // the new inquiry is added to the admin list of inquiry, with
        // the default assignTo as admin
        int emailResponse = emailService.sendEmail(email, sharedContext.ADMIN_STAFF_EMAIL , subject,
                content);
        if(emailResponse == 0) {
            view.displaySuccess("Your inquiry has been sent to the administration staff.");
        }
        else if (emailResponse == 1){
            view.displayError("Invalid sender email. Please try again later.");
        }
        else if(emailResponse == 2){
            view.displayError("Invalid Recipient email. Please try again later.");
        }
        else{
            view.displayError("Unknown error. Please try again later.");
        }
    }


    /**
     * Registers a given email address for updates on a specified topic.
     * If the email address is initially empty or invalid, it prompts the user to input a valid email address.
     * The method recursively calls itself with the correct email address if the initial attempt is made with an empty or invalid one.
     * Upon successful registration, displays a success message. If the registration fails, possibly due to the email
     * already being registered for updates on the topic, displays an error message.
     *
     * @param email The email address to register for FAQ updates. If empty or invalid, the user will be prompted to provide a valid one.
     * @param topic The topic for which the email address is registering to receive updates.
     */
    private void requestFAQUpdates(String email, String topic) {
        if (email.isEmpty()) {
            String userEmail = view.getInput("Please enter your email address");
            while(userEmail == null || userEmail.trim().isEmpty()){
                userEmail = super.view.getInput("Please enter your email address: ");
            }
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            while(!userEmail.matches(emailRegex)){
                userEmail = super.view.getInput("Please enter your email address in a correct form:");
            }
            requestFAQUpdates(userEmail, topic);
        }
        if (sharedContext.registerForFAQUpdates(email, topic)) {
            view.displaySuccess("Successfully registered " + email + " for updates on " + topic);
        }
        else {
            view.displayError("Failed to register " + email + " for updates on " + topic
                    + ". Perhaps this email was already registered?");
        }
    }

    /**
     * Unregisters a given email address from updates on a specified topic.
     * Similar to registration, if the email address provided is empty or invalid, it prompts the user to input a valid email address.
     * The method recursively calls itself with the correct email if the initial input is empty or invalid.
     * Upon successful unregistration, displays a success message. If the unregistration fails, possibly due to the email
     * not being registered for updates on the topic, displays an error message.
     *
     * @param email The email address to unregister from FAQ updates. If empty or invalid, the user will be prompted to provide a valid one.
     * @param topic The topic from which the email address is unregistering to stop receiving updates.
     */
    private void stopFAQUpdates(String email, String topic) {
        if (email.isEmpty()) {
            String userEmail = view.getInput("Please enter your email address");
            while(userEmail == null || userEmail.trim().isEmpty()){
                userEmail = super.view.getInput("Please enter your email address: ");
            }
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            while(!userEmail.matches(emailRegex)){
                userEmail = super.view.getInput("Please enter your email address in a correct form:");
            }
            stopFAQUpdates(userEmail, topic);
        }
        if (sharedContext.unregisterForFAQUpdates(email, topic)) {
            view.displaySuccess("Successfully unregistered " + email + " for updates on " + topic);
        }
        else {
            view.displayError("Failed to unregister " + email + " for updates on "
                    + topic + ". Perhaps this email was not registered?");
        }
    }
}
