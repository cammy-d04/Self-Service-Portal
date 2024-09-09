package model;

import java.io.IOException;
import java.util.*;

public class SharedContext {
    public static final String ADMIN_STAFF_EMAIL = "admin@hindeburg.ac.uk";

    private FAQ faq;

    private Map<String, Collection<String>> faqTopicsUpdateSubscribers;

    private HashMap<String, Page> pageList;

    private User currentUser = new Guest();

    private List<Inquiry> inquiryList;
    // as the general list of inquiry that admin see
    private List<Inquiry> redirectedInquiryList;

    //as the redirected list of inquiry that teaching staff have
    public SharedContext() throws IOException {
        this.inquiryList = new ArrayList<>();
        this.redirectedInquiryList = new ArrayList<>();
        this.pageList = new HashMap<>();
        this.faq = new FAQ();
        this.faqTopicsUpdateSubscribers = new HashMap<>();

    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User newCurrentUser) {
        currentUser = newCurrentUser;
    }

    public List<Inquiry> getInquiryList() {
        return inquiryList;
    }

    public void  setInquiryList(List<Inquiry> newInquiryList) { inquiryList = newInquiryList;}


    public List<Inquiry> getRedirectedInquiryList() {
        return redirectedInquiryList;
    }

    public void setRedirectedInquiryList(List<Inquiry> newRedirectedInquiry){redirectedInquiryList = newRedirectedInquiry;}


    public Collection<Page> getPageList() {
        return pageList.values();
    }

    public void addPage(Page page) {
        pageList.put(page.getTitle(), page);
    }

    public FAQ getFAQ() {return faq;}

    public FAQ setFAQ() { faq = new FAQ();
        return (faq);
    }

    public boolean registerForFAQUpdates(String email, String topic) {
        if (faqTopicsUpdateSubscribers.containsKey(email)) {
            faqTopicsUpdateSubscribers.get(email).add(topic);
        } else {
            Collection<String> topics = new ArrayList<>();
            topics.add(topic);
            faqTopicsUpdateSubscribers.put(email, topics);
        }
        return true;


        //this is assuming that the two strings required for this method
        // are an email and a topic title, since the class diagram incredibly
        // unhelpfully has not provided a single parameter name. awesome !
    }

    public boolean unregisterForFAQUpdates(String email, String topic) {
        if (faqTopicsUpdateSubscribers.containsKey(email)) {
            faqTopicsUpdateSubscribers.get(email).remove(topic);
            return true;
        }
        //high probability that the parameters for this method should be
        // identical to that of the above method
        return false;
    }

    public Collection<String> usersSubscribedToFAQTopic(String topic) {
        List<String> users = new ArrayList<>();
        if (faqTopicsUpdateSubscribers == null) {

            return users;
        }
        for (Map.Entry<String, Collection<String>> entry : faqTopicsUpdateSubscribers.entrySet()) {
            if (entry.getValue().contains(topic)) {
                users.add(entry.getKey());
            }
        }
        return users;
    }
}




