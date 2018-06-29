package data;

import domain.LoginWelcomeMsgRepository;

/**
 * Created by kumara on 23/5/18.
 */

public class LoginWelcomeMsgRepositoryImpl implements LoginWelcomeMsgRepository {
    private String message = "Login to Page!!!!!";
    @Override
    public String getWelcomeMsg() {
        return message;
    }
}
