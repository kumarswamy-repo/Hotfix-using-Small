package domain;

/**
 * Created by kumara on 23/5/18.
 */

public class SignupWelcomeMsgUsecase {

    SignupWelcomeMsgRepository signupWelcomeMsgRepository;

    public SignupWelcomeMsgUsecase(SignupWelcomeMsgRepository signupWelcomeMsgRepository) {
        this.signupWelcomeMsgRepository = signupWelcomeMsgRepository;
    }

    public String getWelcomeMessage(){
        return signupWelcomeMsgRepository.getWelcomeMsg();
    }
}
