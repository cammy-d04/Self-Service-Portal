package model;

import java.time.LocalDateTime;

public class Inquiry {
    private LocalDateTime createdAt;

    private String inquirerEmail;

    private String subject;

    private String content;

    private String assignedTo;

    public Inquiry(String subject, String content, String inquirerEmail) {
        this.subject = subject;
        this.content = content;
        this.inquirerEmail = inquirerEmail;
        this.assignedTo = "admin@hindeburg.ac.uk";// set the assignedTo to admin as default
    }
    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getInquirerEmail() {
        return inquirerEmail;
    }

    public void setInquirerEmail(String inquirerEmail) {
        this.inquirerEmail = inquirerEmail;
    }
    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo ) {
        this.assignedTo = assignedTo;
    }


}
