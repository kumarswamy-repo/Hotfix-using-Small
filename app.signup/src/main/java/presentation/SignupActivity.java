package presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kumar.appsignup.BuildConfig;
import com.kumar.appsignup.R;

import net.wequick.small.Small;

import data.SignupWelcomeMsgRepositoryImpl;
import domain.SignupWelcomeMsgUsecase;

public class SignupActivity extends AppCompatActivity {

    TextView welcomeText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        welcomeText = findViewById(R.id.welcome_msg);
        Button signupButton = findViewById(R.id.id_login);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Small.setUp(SignupActivity.this, new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Small.openUri("login",SignupActivity.this);
                        finish();
                    }
                });
            }
        });
        TextView version = findViewById(R.id.id_version);
        version.setText("version:"+ BuildConfig.VERSION_NAME);
        getMessage();
    }

    private void getMessage(){
        SignupWelcomeMsgRepositoryImpl welcomeMsgRepository = new SignupWelcomeMsgRepositoryImpl();
        SignupWelcomeMsgUsecase signupWelcomeMsgUsecase = new SignupWelcomeMsgUsecase(welcomeMsgRepository);
        welcomeText.setText(signupWelcomeMsgUsecase.getWelcomeMessage());
    }
}
