package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import org.apache.lucene.queryparser.classic.ParseException;
import view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InquirerController extends Controller{




    public InquirerController(SharedContext sharedContext, View view,
                              AuthenticationService authenticationService,
                              EmailService emailService) {
        super(sharedContext,view,authenticationService,emailService);
        //may need more stuff here, depending on other methods.
    }


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
            try{Integer.parseInt(input);}
            catch (NumberFormatException nfe) {view.displayError("Invalid option: " + input);}

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
                    view.displayError("Invalid option" + optionNo);
                }
                else {
                    currentSection = sections.get(sections.size()-optionNo-1);
                }
            }

            if (currentSection != null) {
                    String topic = currentSection.getTopic();
                    if (currentUser instanceof Guest) {
                        if (optionNo == -2) {
                            requestFAQUpdates(null, topic);
                        } else if (optionNo == -3) {
                            stopFAQUpdates(null, topic);
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
                    }
                }
        } while (!(currentSection == null && optionNo == -1));
    }
    

    public void searchPages() {
        Collection<Page> pageList = super.sharedContext.getPageList();
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
        if(subject == null || subject.trim().isEmpty()){
            subject = super.view.getInput("A subject must be provided for the inquiry.");
        }
        String content =super.view.getInput("Please enter the content of your inquiry:");
        if(content == null || content.trim().isEmpty()){
            content = super.view.getInput("A content must be provided for the inquiry.");
        }
        String email;

        if(sharedContext.getCurrentUser() instanceof Guest){
            email =  view.getInput("Please enter your email address:");
            if(email == null || email.trim().isEmpty()){
                email = super.view.getInput("A email must be provided for the inquiry.");
            }
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if(!email.matches(emailRegex)){
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


    private void requestFAQUpdates(String email, String topic) {
        if (email.isEmpty()) {
            String userEmail = view.getInput("Please enter your email address");
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

    private void stopFAQUpdates(String email, String topic) {
        if (email.isEmpty()) {
            String userEmail = view.getInput("Please enter your email address");
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
