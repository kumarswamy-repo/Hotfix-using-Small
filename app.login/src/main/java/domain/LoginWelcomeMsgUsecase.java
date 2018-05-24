package domain;

/**
 * Created by kumara on 23/5/18.
 */

public class LoginWelcomeMsgUsecase {

    LoginWelcomeMsgRepository loginWelcomeMsgRepository;

    public LoginWelcomeMsgUsecase(LoginWelcomeMsgRepository loginWelcomeMsgRepository) {
        this.loginWelcomeMsgRepository = loginWelcomeMsgRepository;
    }

    public String getWelcomeMessage(){
        return loginWelcomeMsgRepository.getWelcomeMsg();
    }
}
