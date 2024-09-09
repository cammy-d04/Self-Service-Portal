package model;
import java.util.ArrayList;

public class FAQSection {
    private String topic;

    private final ArrayList<FAQItem> FAQItems;

    private final ArrayList<FAQSection> subsections;

    private FAQSection parent;



    public FAQSection(String topic) {
        this.topic = topic;
        this.FAQItems = new ArrayList<>();
        this.subsections = new ArrayList<>();
    }
    public void addSubsection(FAQSection subsection) {
        subsections.add(subsection);
    }

    public void addItem(String question, String answer) {this.FAQItems.add(new FAQItem(question, answer));}

    public String getTopic(){return this.topic;}

    public ArrayList<FAQItem> getFAQItems() {return this.FAQItems;}

    public ArrayList<FAQSection> getSubsections() {return this.subsections;}

    public FAQSection getParent(){return this.parent;}

    public void setParent(FAQSection parent){
        this.parent = parent;
    }

}



