package view;

import model.Inquiry;
import model.PageSearchResult;
import model.FAQ;
import model.FAQSection;

import java.util.Collection;
import java.util.List;

public interface View {

    String getInput(String input);
    boolean getYesNoInput(String input);

    void displayInfo(String info);

    void displaySuccess(String success);

    void displayWarning(String warning);

    void displayError(String error);

    void displayException(Exception exception);

    void displayDivider();

    void displayFAQ(FAQ faq);

    void displayFAQSection(FAQSection faqSection);

    void displayInquiry(Inquiry inquiry);

    void displaySearchResults(List<PageSearchResult> results);

}
