package kennedy.walk.avec;

import com.parse.Parse;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DispatchActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_dispatch);
		super.onCreate(savedInstanceState);
		Log.d("MAIN_ACTIVITY_TAG", "onCreate");
		Parse.initialize(getApplicationContext(), 
				"CEEVvOCi5hJmt5dC1FADXWaQeZd3KZud31Ls0M53", 
				"fFVSRHTNAfyFVHth27AtWOr0TfL25EhoUOnH981G");
		
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
		  // do stuff with the user
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		} else {
		  // show the signup or login screen
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		
		finish();
	}
}
