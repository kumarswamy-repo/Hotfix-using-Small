package data;

import domain.WelcomeMsgRepository;

/**
 * Created by kumara on 23/5/18.
 */

public class WelcomeMsgRepositoryImpl implements WelcomeMsgRepository {
    private String message = "Launching Page";
    @Override
    public String getWelcomeMsg() {
        return message;
    }
}
