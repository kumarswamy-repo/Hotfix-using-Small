package presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kumar.applogin.BuildConfig;
import com.kumar.applogin.R;

import net.wequick.small.Small;

import data.LoginWelcomeMsgRepositoryImpl;
import domain.LoginWelcomeMsgUsecase;

public class LoginActivity extends AppCompatActivity {

    TextView welcomeText = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        welcomeText = findViewById(R.id.welcome_msg);
        Button signupButton = findViewById(R.id.singup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Small.setUp(LoginActivity.this, new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Small.openUri("signup",LoginActivity.this);
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
        LoginWelcomeMsgRepositoryImpl welcomeMsgRepository = new LoginWelcomeMsgRepositoryImpl();
        LoginWelcomeMsgUsecase loginWelcomeMsgUsecase = new LoginWelcomeMsgUsecase(welcomeMsgRepository);
        welcomeText.setText(loginWelcomeMsgUsecase.getWelcomeMessage());
    }
}
