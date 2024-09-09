package model;

import java.util.ArrayList;
public class FAQ {

    private ArrayList<FAQSection> sections;

    public FAQ() {
        this.sections = new ArrayList<>();
    }

    public void addSection(FAQSection section) {
        if (section != null) {
            sections.add(section);
        }
    }

    public ArrayList<FAQSection> getSections(){return sections;}

}
