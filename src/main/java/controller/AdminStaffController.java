package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

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
        if(teachingStaffEmail == null || teachingStaffEmail.trim().isEmpty()){
            teachingStaffEmail = super.view.getInput("A teaching staff email must be provided for redirecting inquiry.");
        }
        emailService.sendEmail(SharedContext.ADMIN_STAFF_EMAIL, teachingStaffEmail, inquiry.getSubject() + "The original sender's email:"
                + inquiry.getInquirerEmail(), inquiry.getContent());
        sharedContext.getRedirectedInquiryList().add(inquiry);
        // add the redirected inquiry to the redirected inquiry list
        inquiry.setAssignedTo(teachingStaffEmail);
        //as the inquiry is redirected, the sender would be teaching staff when they finish answering it.
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
            outputPages.add(new PageSearchResult(page.getTitle() + "\n\n" + page.getContent()));
        }
        view.displaySearchResults(outputPages);
        boolean add = view.getYesNoInput("Do you wish to add a new webpage?");
        if (add) {
            addPage();
        }
    }

    public void manageFAQ() {
        int optionNo;
        FAQSection currentSection = null;
        do {
            if (currentSection == null) {
                FAQ faq = sharedContext.getFAQ();
                view.displayFAQ(faq);
                view.displayInfo("[-1] to return to main menu");
                view.displayInfo("[-2] to add a section");
            } else {
                view.displayFAQSection(currentSection);
                FAQSection parent = currentSection.getParent();

                if (parent == null) {
                    view.displayInfo("[-1] to return to FAQ");
                    view.displayInfo("[-2] to add new subsection");
                    view.displayInfo("[-3] to add new QA pair");
                } else {
                    String topic = parent.getTopic();
                    view.displayInfo("[-1] to return to " + topic);
                    view.displayInfo("[-2] to add new subsection");
                    view.displayInfo("[-3] to add new QA pair");
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
                    throw new IndexOutOfBoundsException("Invalid option: " + optionNo);

                }
                else {
                    currentSection = sections.get(sections.size()-optionNo-1);
                }
            }
            if (currentSection == null && optionNo == -2){
                String topic = view.getInput("Please enter the new section's topic");
                FAQSection newSection = new FAQSection(topic);
                sharedContext.getFAQ().addSection(newSection);
                if (sharedContext.getFAQ().getSections().contains(newSection)){
                    view.displaySuccess("Successfully added " + topic + " to the FAQ");
                }
                else{
                    view.displayError("Failed to add " + topic + " to the FAQ. Please contact our technical support");
                }
            }
            if (currentSection != null) {
                    if (optionNo == -2){
                        String topic = view.getInput("Please enter the new subsection's topic");
                        FAQSection newSection = new FAQSection(topic);
                        currentSection.addSubsection(newSection);
                        newSection.setParent(currentSection);
                        if (currentSection.getSubsections().contains(newSection)){
                            view.displaySuccess("Successfully added " + topic + "as a subtopic to the "
                                    + currentSection.getParent().getTopic());
                        }
                        else{
                            view.displayError("Failed to add " + topic + "as a subtopic to the "
                                    + currentSection.getParent().getTopic()
                                    + ". Please contact our technical support");
                        }
                    }
                    else if (optionNo == -3){
                        addFAQItem(currentSection);
                    }
                    else if (optionNo == -1) {
                        currentSection = currentSection.getParent();
                    }
                }
        } while (!(currentSection == null && optionNo == -1));
    }


    private void addFAQItem(FAQSection section) {
        String question = view.getInput("Please enter the (sub)section's question: ");
        String answer = view.getInput("Please enter the (sub)section's answer: ");
        FAQItem QAPair = new FAQItem(question,answer);
        section.addItem(question, answer);
        if (section.getFAQItems().contains(QAPair)){
            view.displaySuccess("Successfully added question: " + question + "\n and answer: " + " to the topic "
                    + section.getTopic());
        }
        else{
            view.displayError("Failed to add question " + question  + "\n and answer:  " + answer + " to the topic "
                    + section.getTopic()
                    + ". Please contact our technical support");
        }
    }
}
