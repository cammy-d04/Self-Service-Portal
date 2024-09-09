package controller;

import external.AuthenticationService;
import external.EmailService;
import model.Inquiry;
import model.SharedContext;
import view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TeachingStaffController extends StaffController {



    public TeachingStaffController(SharedContext sharedContext, View view,
                              AuthenticationService authenticationService,
                              EmailService emailService) {
        super(sharedContext,view,authenticationService,emailService);
    }
    public void manageReceivedInquiries(){

    }

    public void manageReceivedInquiry(){
        List<Inquiry> redirectedInquiries = sharedContext.getRedirectedInquiryList();
        String staffEmail = view.getInput("please type in your staff Email");
        List<Inquiry> selectedRedirectedInquiryByEmail = redirectedInquiries.stream().filter(inquiry ->
                staffEmail.equalsIgnoreCase(inquiry.getAssignedTo())).collect(Collectors.toList());
        // select the inquires corresponding to the teaching staff email out
        List<Inquiry> newRedirectedInquiry = sharedContext.getRedirectedInquiryList();
        if(selectedRedirectedInquiryByEmail.isEmpty()){
            view.displayInfo("There are no inquiries assigned to you.");
        }
        else{
            Collection<String> titles = getInquiryTitles(selectedRedirectedInquiryByEmail);
            titles.forEach(title -> view.displayInfo(title));

            String selectedTitle = view.getInput("Please select the title of the inquiry to manage: ");
            Inquiry selectedRedirectedInquiry  = (Inquiry)redirectedInquiries.stream().filter(inquiry -> inquiry.getSubject()
                    .equalsIgnoreCase(selectedTitle)).findFirst().orElse(null);
            // select the inquiry out and answer it
            view.displayInquiry(selectedRedirectedInquiry);
            respondToInquiry(selectedRedirectedInquiry);
            newRedirectedInquiry.remove(selectedRedirectedInquiry);
            sharedContext.setRedirectedInquiryList(newRedirectedInquiry);
            // after finish answer, the inquiry in removed from redirected inquiry list
            view.displayInfo("The inquiry has been answered and removed from the redirected inquiry list");

        }


    }

}
