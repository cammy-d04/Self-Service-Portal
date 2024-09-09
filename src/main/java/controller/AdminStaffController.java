package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AdminStaffController extends StaffController {


    public AdminStaffController(SharedContext SharedContext, View view,
                                AuthenticationService authenticationService,
                                EmailService emailService) {
        super(SharedContext, view, authenticationService, emailService);
        //may need more stuff here, depending on other methods.
    }

    public void manageInquiry() {
        List<Inquiry> inquiries = sharedContext.getInquiryList();
        if (inquiries.isEmpty()) {
            view.displayInfo("There are no inquiries in the list.");
            return; // Exit the method if there are no inquiries
        }
        Collection<String> titles = getInquiryTitles(inquiries);
        titles.forEach(title -> view.displayInfo(title));
        // use getInquiryTitle method to show the titles of inquiry to admin
        String selectedTitle = view.getInput("Please select the title of the inquiry to manage: ");
        Inquiry selectedInquiry = (Inquiry) inquiries.stream().filter(inquiry -> inquiry.getSubject()
                .equalsIgnoreCase(selectedTitle)).findFirst().orElse(null);
        // inquiry selected
        if (selectedInquiry != null) {
            view.displayInquiry(selectedInquiry);
            List<Inquiry> newInquiryList = sharedContext.getInquiryList();
            String action = view.getInput("Please type a for answering inquiry or type r for redirecting inquiry");
            if ("a".equalsIgnoreCase(action)) {
                respondToInquiry(selectedInquiry);
                // respond to inquiry with the sender as default(admin)
                newInquiryList.remove(selectedInquiry);
                sharedContext.setInquiryList(newInquiryList);
                // remove the inquiry has been answered from the general list
                view.displayInfo("The inquiry has been answered and removed from the list");
            } else if ("r".equalsIgnoreCase(action)) {
                redirectInquiry(selectedInquiry);
                newInquiryList.remove(selectedInquiry);
                sharedContext.setInquiryList(newInquiryList);
                // remove the redirected inquiry from the general list
            }
        } else {
            view.displayError("Inquiry title not found.");
        }
    }

    public void redirectInquiry(Inquiry inquiry) {
        String teachingStaffEmail = view.getInput("Please enter the email address of the teaching staff you want to redirect to.");
        while(teachingStaffEmail == null || teachingStaffEmail.trim().isEmpty()){
            teachingStaffEmail = super.view.getInput("A teaching staff email must be provided for redirecting inquiry.");
        }
        emailService.sendEmail(SharedContext.ADMIN_STAFF_EMAIL, teachingStaffEmail, inquiry.getSubject() + "The original sender's email:"
                + inquiry.getInquirerEmail(), inquiry.getContent());
        inquiry.setAssignedTo(teachingStaffEmail);
        //as the inquiry is redirected, the sender would be teaching staff when they finish answering it.
        List<Inquiry> newinquirylist = sharedContext.getRedirectedInquiryList();
        newinquirylist.add(inquiry);
        sharedContext.setRedirectedInquiryList(newinquirylist);
        // add the redirected inquiry to the redirected inquiry list

        view.displayInfo("The inquiry has been redirected.");
    }

    public void addPage() {
        String title = view.getInput("Enter page title");

        boolean wrongPath = true;
        while (wrongPath) {
            String contentAddress = view.getInput("Enter file address for page " +
                    "content");
            if (contentAddress.isEmpty()) {
                view.displayWarning("Please enter a valid file path");
                continue;
            }
            Scanner reader;
            try {
                File conentFile = new File(contentAddress);
                if (conentFile.length() == 0) {
                    view.displayWarning("Please enter a non-empty file");
                    continue;
                }
                if (!conentFile.canRead()) {
                    view.displayWarning("Please enter a valid file");
                    continue;
                }
                reader = new Scanner(conentFile);
                wrongPath = false;
            } catch (FileNotFoundException e) {
                view.displayException(e);
                continue;
            }
            StringBuilder content = new StringBuilder();
            while (reader.hasNext()) {
                content.append(reader.nextLine()).append("\n");
            }
            reader.close();
            boolean isPrivate = view.getYesNoInput("Should this page be private?");
            Collection<Page> pageList = sharedContext.getPageList();
            //checks for existing page
            for (Page page : pageList) {
                if (page.getTitle().equals(title)) {
                    //checks with user if they intend to overwrite
                    boolean overwrite =
                            view.getYesNoInput("Page " + page.getTitle() +
                                    " already exists. Overwrite with new page?");
                    if (!overwrite) {
                        view.displayInfo("Cancelled adding new page");
                        return;
                    }
                }
            }
            Page newPage = new Page(title, content.toString(), isPrivate);
            sharedContext.addPage(newPage);
            int status =
                    emailService.sendEmail(((AuthenticatedUser) sharedContext.getCurrentUser()).getEmail(), SharedContext.ADMIN_STAFF_EMAIL, title, content.toString());
            if (status == EmailService.STATUS_SUCCESS) {
                view.displaySuccess("Added page " + title + "");
            } else {
                view.displayWarning("Added page " + title + " but failed to send " +
                        "email notification!");
            }
        }
    }

    public void viewAllPages() {
        Collection<Page> pageList = sharedContext.getPageList();
        List<PageSearchResult> outputPages = new ArrayList<>();
        for (Page page : pageList) {
            outputPages.add(new PageSearchResult(page.getTitle()));
        }
        view.displaySearchResults(outputPages);
        boolean add = view.getYesNoInput("Do you wish to add a new webpage?");
        if (add) {
            addPage();
        }
    }
    /**
     * Manages the FAQ (Frequently Asked Questions) section of the application.
     * This method provides a loop-based UI for navigating through FAQ sections,
     * adding new questions and answers, and sending updates via email notifications
     * to subscribed users. It allows for adding, viewing, and updating FAQ items
     * and sections based on user input.
     */
    public void manageFAQ() {
        // Variable declarations and initial setup
        int optionNo;
        FAQSection currentSection = null;
        do {
            // Display current section or main FAQ depending on the context
            if (currentSection == null) {
                FAQ faq = sharedContext.getFAQ();
                view.displayFAQ(faq);
                view.displayInfo("[-1] to return to main menu");
                view.displayInfo("[-2] to add new QA pair");
            } else {
                view.displayFAQSection(currentSection);
                FAQSection parent = currentSection.getParent();

                if (parent == null) {
                    view.displayInfo("[-1] to return to FAQ");
                    view.displayInfo("[-2] to add new QA pair");
                } else {
                    String topic = parent.getTopic();
                    view.displayInfo("[-1] to return to " + topic);
                    view.displayInfo("[-2] to add new QA pair");
                }

            }
            // Get and validate user input
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

            if (optionNo != -1 && optionNo != - 2){
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
            // Process user selection for navigation or modification
            if (currentSection != null) {
                    if (optionNo == -2){
                        boolean userChoice = view.getYesNoInput("Would you like to add a subsection?");

                        if (userChoice){
                            String topic = view.getInput("Please enter the name of your new subtopic: ");
                            int i = 0;
                            boolean topicFound = false;

                            while (!(topicFound) && i < currentSection.getSubsections().size()) {
                                if (currentSection.getSubsections().get(i).getTopic().equals(topic)){
                                    topicFound = true;
                                }
                                else{i++;}
                            }
                            if (topicFound){
                                view.displayWarning("The inputted subtopic already exists in this section");
                                currentSection = currentSection.getSubsections().get(i);
                                addFAQItem(currentSection);
                            }
                            else{
                                FAQSection newSection = new FAQSection(topic);
                                currentSection.addSubsection(newSection);
                                newSection.setParent(currentSection);
                                currentSection = newSection;
                                addFAQItem(currentSection);
                            }
                        }
                        else{
                            addFAQItem(currentSection);
                        }
                    }
                    else if (optionNo == -1) {
                        currentSection = currentSection.getParent();
                        optionNo = 0;
                    }
                }
            // Special handling for adding new QA pairs
            if (currentSection == null && optionNo == -2){
                String topic = view.getInput("Please enter the new section's topic");

                int i = 0;
                boolean topicFound = false;

                while (!(topicFound) && i < sharedContext.getFAQ().getSections().size()) {
                    if (sharedContext.getFAQ().getSections().get(i).getTopic().equals(topic)){
                        topicFound = true;
                    }
                    else{i++;}
                }
                if (topicFound){
                    view.displayWarning("The inputted topic already exists in this FAQ");
                    currentSection = sharedContext.getFAQ().getSections().get(i);
                    addFAQItem(currentSection);
                }
                else{
                    FAQSection newSection = new FAQSection(topic);
                    sharedContext.getFAQ().addSection(newSection);
                    currentSection = newSection;
                    addFAQItem(currentSection);
                }
            }

            // Navigation back to parent sections or the main FAQ
        } while (!(currentSection == null && optionNo == -1));
    }

    /**
     * Adds a question and answer item to the specified FAQ section.
     * After adding the item, it sends an email notification to the
     * administrator and all users subscribed to updates on the topic.
     * It also provides feedback to the user about the success or
     * failure of the addition.
     *
     * @param section The FAQSection to which the new QA pair will be added.
     */
    private void addFAQItem(FAQSection section) {
        // Input collection for new QA pair
        String question = view.getInput("Please enter the (sub)section's question: ");
        String answer = view.getInput("Please enter the (sub)section's answer: ");
        section.addItem(question, answer);
        String currentAdminEmail=((AuthenticatedUser)sharedContext.getCurrentUser()).getEmail();
        StringBuilder AllFAQItems = getAllFAQItems(section);

        // Sending email notifications to admins and subscribed users
        emailService.sendEmail(currentAdminEmail, sharedContext.ADMIN_STAFF_EMAIL,
                "FAQ topic update: [Topic] " + section.getTopic() , AllFAQItems.toString());

        for (String email : sharedContext.usersSubscribedToFAQTopic(section.getTopic())) {
            emailService.sendEmail(sharedContext.ADMIN_STAFF_EMAIL, email,
                    "Update on the following topic: " + section.getTopic(), AllFAQItems.toString());
        }

        // Verifying addition and providing user feedback
        int j = 0;
        while (j < section.getFAQItems().size()) {
            if (section.getFAQItems().get(j).getQuestion().equals(question)
                    && section.getFAQItems().get(j).getAnswer().equals(answer)){
                view.displaySuccess("Successfully added question: \"" + question + "\" and answer: \"" + answer +
                        "\" to the topic: " + section.getTopic());
                return;
            }
            else{
                j++;
            }
        }
            view.displayError("Failed to add question \"" + question  + "\" and answer:  \"" + answer +
                    "\" to the topic: " + section.getTopic() + ". Please contact our technical support");
    }


    /**
     * Helper method to compile a list of all questions and answers within a section.
     * This method formats the QA pairs into a StringBuilder object for easy email
     * composition and further processing.
     *
     * @param section The FAQSection from which to extract all QA pairs.
     * @return A StringBuilder containing a formatted list of all questions and answers in the section.
     */
    private static StringBuilder getAllFAQItems(FAQSection section) {
        StringBuilder AllFAQItems = new StringBuilder();
        AllFAQItems.append("Updated Questions and Answers: ");
        int i = 0;
        for (FAQItem QAPair : section.getFAQItems()){
            AllFAQItems.append("[Question_");
            AllFAQItems.append(i);
            AllFAQItems.append("]: ");
            AllFAQItems.append(QAPair.getQuestion());
            AllFAQItems.append("\n \t\t\t\t\t\t\t\t[Answer_");
            AllFAQItems.append(i);
            AllFAQItems.append("]: ");
            AllFAQItems.append(QAPair.getAnswer());
            AllFAQItems.append("\n \t\t\t\t\t\t\t\t");
            i++;
        }
        return AllFAQItems;
    }
}
