package kennedy.walk.avec;

import java.util.ArrayList;

import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_profile);
		TextView usernameTitle = (TextView) findViewById(R.id.username_profile);
		ParseUser current = ParseUser.getCurrentUser();
		String username = (String) current.get("Name");
		usernameTitle.setText("My Profile: " + username);
		
		ArrayList<String> artistItems = new ArrayList<String>();
		artistItems = (ArrayList<String>) current.get("TopArtist");
		
		ListView profileListView = (ListView) findViewById(R.id.myArtistListView);
		UserArtistListAdapter adapter = new UserArtistListAdapter(getApplicationContext(), artistItems);
		profileListView.setAdapter(adapter);
		
		Button editBtn = (Button) findViewById(R.id.editBtn);
		editBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
				startActivity(intent);
				finish();
			}
		});
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.menuHelp:
			return true;
			
		case R.id.menuProfile:
			return true;
		
		case R.id.menuFriends:
			Intent intent = new Intent(this, FriendActivity.class);
			startActivity(intent);
			return true;
			
		case R.id.menuAbout:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
