package controller;

import external.AuthenticationService;
import external.EmailService;
import model.SharedContext;
import model.*;
import view.View;

import java.util.Collection;
import java.util.stream.Collectors;
public class StaffController extends Controller{



    public StaffController(SharedContext sharedContext, View view,
                           AuthenticationService authenticationService,
                           EmailService emailService) {
        super(sharedContext,view,authenticationService,emailService);
    }
    protected Collection<String> getInquiryTitles(Collection<Inquiry> inquiries) {
        return inquiries.stream()
                .map(Inquiry::getSubject)
                .collect(Collectors.toList());
    }

    public void respondToInquiry(Inquiry inquiry){
        String response = view.getInput("Please enter your response to the inquiry:");
        if(response == null || response.trim().isEmpty()){
            response = super.view.getInput("A response can not be empty.");
        }
        emailService.sendEmail(inquiry.getAssignedTo(), inquiry.getInquirerEmail(),
                "Re:" + inquiry.getSubject(),
                response);
        view.displayInfo("Your response has been sent.");
    }

}


