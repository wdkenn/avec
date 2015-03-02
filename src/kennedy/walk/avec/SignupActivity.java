package kennedy.walk.avec;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		Parse.initialize(getApplicationContext(), 
				"CEEVvOCi5hJmt5dC1FADXWaQeZd3KZud31Ls0M53", 
				"fFVSRHTNAfyFVHth27AtWOr0TfL25EhoUOnH981G");
		
		Button loginReturnBtn = (Button) findViewById(R.id.backToLoginBtn);
		loginReturnBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent logInIntent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(logInIntent);
				finish();
			}
		});
		
		final EditText email = (EditText) findViewById(R.id.email);
		final EditText password = (EditText) findViewById(R.id.password);
		final EditText username = (EditText) findViewById(R.id.username);
		
		
		Button createBtn = (Button) findViewById(R.id.createAccountBtn);
		createBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String emailString = email.getText().toString();
				String passwordString = password.getText().toString();
				final String usernameString = username.getText().toString();
				
				// check all fields are filled and display error if not
				if (emailString.isEmpty() || passwordString.isEmpty() || usernameString.isEmpty()) {
					
					  Toast.makeText(getApplicationContext(), "Please fill in ALL fields!", Toast.LENGTH_LONG).show();
					
				} else {
					final ParseUser user = new ParseUser();
					user.setUsername(emailString);
					user.setPassword(passwordString);
					user.setEmail(emailString);
					// sign new user into background
					user.signUpInBackground(new SignUpCallback() {
						
						@Override
						public void done(ParseException e) {
							if (e == null) {
								
								user.put("Name", usernameString);
								user.saveInBackground();
								showSetupDialog();
								
							} else {
								// get displayable error message for user
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
		
		
		// Download artist list
	}
	
	private void showSetupDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Hello! Thanks for joining the Avec community! " +
									"To get started, you need to make a profile with your " +
									"favorite artists! You can do this in one of two ways: " +
									"1. Use your Last.FM username to get your top 50 artists! " +
									"2. Enter manually your favorite artists. Can't think of 50 artists? " +
									"No worries! Input as many as you like. Avec just needs 1!")
				.setCancelable(false)
				.setPositiveButton("LastFM", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(SignupActivity.this, LastFMSetupActivity.class);
						startActivity(intent);
						finish();
					}
				});
		alertDialogBuilder.setNegativeButton("Manually enter names", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(SignupActivity.this, ProfileSetupActivity.class);
				startActivity(intent);
				finish();
				
			}
		});
		
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}
}
