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


   /** Registers a user for updates on a specific FAQ topic.
    *
    * @param email The user's email address.
    * @param topic The FAQ topic the user wants to subscribe to.
    * @return returns true, indicating the user was successfully registered.
    */
    public boolean registerForFAQUpdates(String email, String topic) {
        // Check if the user is already subscribed to any topic
        if (faqTopicsUpdateSubscribers.containsKey(email)) {
            // Add the new topic to their existing subscriptions
            faqTopicsUpdateSubscribers.get(email).add(topic);
        } else {
            // If not subscribed to anything, create a new subscription list and add the topic
            Collection<String> topics = new ArrayList<>();
            topics.add(topic);
            faqTopicsUpdateSubscribers.put(email, topics);
        }
        return true;
    }

    /**
     * Unregisters a user from updates on a specific FAQ topic.
     *
     * @param email The user's email address.
     * @param topic The FAQ topic the user wants to unsubscribe from.
     * @return true if the user was successfully unregistered, false otherwise.
     */
    public boolean unregisterForFAQUpdates(String email, String topic) {
        // Check if the user is currently subscribed to any topics
        if (faqTopicsUpdateSubscribers.containsKey(email)) {
            // Remove the specified topic from their subscriptions
            faqTopicsUpdateSubscribers.get(email).remove(topic);
            return true;
        }
        return false;
    }

    /**
     * Retrieves a collection of users subscribed to a specific FAQ topic.
     *
     * @param topic The FAQ topic.
     * @return a collection of email addresses of users subscribed to the topic.
     */
    public Collection<String> usersSubscribedToFAQTopic(String topic) {
        List<String> users = new ArrayList<>();

        // Guard clause for null faqTopicsUpdateSubscribers map
        if (faqTopicsUpdateSubscribers == null) {
            return users;
        }

        // Iterate over all entries in the map to find users subscribed to the specified topic
        for (Map.Entry<String, Collection<String>> entry : faqTopicsUpdateSubscribers.entrySet()) {
            if (entry.getValue().contains(topic)) {
                users.add(entry.getKey());
            }
        }
        return users;
    }
}




