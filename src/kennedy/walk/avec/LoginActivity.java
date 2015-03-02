package kennedy.walk.avec;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        final EditText email = (EditText) findViewById(R.id.emailLogin);
        final EditText password = (EditText) findViewById(R.id.passwordLogin);
        
        Button logIn = (Button) findViewById(R.id.loginButton);
        logIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String emailString = email.getText().toString();
				String passwordString = password.getText().toString();
				if (emailString.isEmpty() || passwordString.isEmpty())
				{
					Toast.makeText(getApplicationContext(), "Please fill in ALL fields", Toast.LENGTH_LONG).show();
				} else {
					// BEGIN HERE
					ParseUser.logInInBackground(emailString, passwordString, new LogInCallback() {

						@Override
						public void done(ParseUser user, ParseException e) {
							if (user != null) {
								Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_LONG).show();
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								startActivity(intent);
								finish();
							} else {
								String errorMessage = e.toString().substring(e.toString().indexOf(" ") + 1);
								//capitalize first letter
								errorMessage = errorMessage.substring(0, 1).toUpperCase() + errorMessage.substring(1);
								// display message
								Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
							}
							
						}
						
					});
				}
			}
		});
        
        Button signUp = (Button) findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), SignupActivity.class);
				startActivity(intent);
				finish();
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    
}
