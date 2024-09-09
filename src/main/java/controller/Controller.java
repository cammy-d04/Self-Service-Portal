package controller;

import external.AuthenticationService;
import external.EmailService;
import model.SharedContext;
import view.View;

import java.util.Collection;

public abstract class Controller{


    protected SharedContext sharedContext;

    protected View view;
    protected AuthenticationService authenticationService;
    protected EmailService emailService;



    protected Controller(SharedContext sharedContext, View view,
                         AuthenticationService authenticationService,
                         EmailService emailService){
        this.authenticationService = authenticationService;
        this.sharedContext = sharedContext;
        this.emailService = emailService;
        this.view = view;

    }

    protected <T> int selectFromMenu(Collection<T> optionsList){

        this.view.displayDivider();

        int selectedOption = 0;
        int optionNum = 1;

        String outputString = "Please select an option: " + "\n";


        //print each option
        for (T option : optionsList) {
            this.view.displayInfo((optionNum++) + ". " + option + "\n");
        }

            do {
                String userInput = this.view.getInput(outputString);
                try {
                    selectedOption = Integer.parseInt(userInput);
                    if (selectedOption < 0 || selectedOption > optionsList.size()) {
                        view.displayError("Invalid input. Please select a number between 1 and " + optionsList.size());
                    }


            } catch (NumberFormatException e) {
                view.displayError("Invalid input. Please enter an integer");
                selectedOption = -1;
            }

        }
        while (selectedOption < 0 || selectedOption > optionsList.size());
            return selectedOption;

    }
}

