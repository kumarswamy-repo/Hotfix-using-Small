package data;

import domain.SignupWelcomeMsgRepository;

/**
 * Created by kumara on 23/5/18.
 */

public class SignupWelcomeMsgRepositoryImpl implements SignupWelcomeMsgRepository {
    private String message = "Signup to Page!!!!!";
    @Override
    public String getWelcomeMsg() {
        return message;
    }
}
