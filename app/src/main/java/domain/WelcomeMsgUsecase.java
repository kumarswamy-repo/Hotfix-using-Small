package domain;

/**
 * Created by kumara on 23/5/18.
 */

public class WelcomeMsgUsecase {

    WelcomeMsgRepository welcomeMsgRepository;

    public WelcomeMsgUsecase(WelcomeMsgRepository welcomeMsgRepository) {
        this.welcomeMsgRepository = welcomeMsgRepository;
    }

    public String getWelcomeMessage(){
        return welcomeMsgRepository.getWelcomeMsg();
    }
}
