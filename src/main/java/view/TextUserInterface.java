package view;

import model.*;

import java.util.List;
import java.util.Scanner;

public class TextUserInterface implements View {

    private Scanner scanner = new Scanner(System.in);


    @Override
    public String getInput(String question) {
        System.out.println(question);
        return scanner.nextLine();
    }

    @Override
    public boolean getYesNoInput(String question){
        System.out.println(question);
        String input = scanner.nextLine();
        return input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y");
    }

    @Override
    public void displayInfo(String info) {
        System.out.println(info);
    }

    @Override
    public void displaySuccess(String success) {
        System.out.println("SUCCESS: " + success);
    }

    @Override
    public void displayWarning(String warning) {
        System.out.println("WARNING: " + warning);
    }

    @Override
    public void displayError(String error) {
        System.out.println("ERROR: " + error);

    }

    @Override
    public void displayException(Exception exception) {
        System.out.println("EXCEPTION: " + exception.getMessage());
    //    System.out.println(faqSection.getTopic());
    }

    @Override
    public void displayDivider() {
        System.out.println("-----------------------------------------");
    }


    public void displayFAQ(FAQ faq) {
        int i = faq.getSections().size() - 1;
            for (FAQSection section : faq.getSections()) {
                System.out.println("[" + i + "] Topic: " + section.getTopic());
                i--;
            }
        }

    public void displayFAQSection(FAQSection FAQSection) {
        System.out.println("[Current Topic] " + FAQSection.getTopic());
        int i = FAQSection.getSubsections().size() - 1;
        int j = 0;
        if (FAQSection.getSubsections() != null){
            for (FAQSection subsection : FAQSection.getSubsections()){
                System.out.println("[" + i + "] [Subtopic_" + i + "] " + subsection.getTopic());
                i--;
            }
            for (FAQItem QAPair : FAQSection.getFAQItems()){
                System.out.println("    [Question_" + j + "] " + QAPair.getQuestion() + "\n");
                System.out.println("    [Answer_" + j + "] " + QAPair.getAnswer());
                j++;
            }
        }
    }

    @Override
    public void displayInquiry(Inquiry inquiry) {
        if (inquiry != null) {
            System.out.println("Subject: " + inquiry.getSubject());
            System.out.println("Content: " + inquiry.getContent());
            System.out.println("Inquirer's Email: " + inquiry.getInquirerEmail());
        } else {
            System.out.println("The inquiry is not available.");
        }
    }

    @Override
    public void displaySearchResults(List<PageSearchResult> results) {
        for (PageSearchResult result : results) {
            System.out.println(result.getFormattedContent());
        }
    }
}
