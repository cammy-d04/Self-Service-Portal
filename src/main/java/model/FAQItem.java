package model;

public class FAQItem {
    private String question;
    private String answer;

    public FAQItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getAnswer(){return answer;}

    public String getQuestion(){return question;}

}
