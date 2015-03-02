package kennedy.walk.avec;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FriendActivity extends Activity{

	ListView friendsList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend);
		friendsList = (ListView) findViewById(R.id.friendsListView);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		FriendListAdapter adapter = new FriendListAdapter(getApplicationContext());
		friendsList.setAdapter(adapter);
		
		friendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				String username = (String) parent.getItemAtPosition(position);
				Intent intent = new Intent(FriendActivity.this, UserProfileActivity.class);
				intent.putExtra("username", username);
				startActivity(intent);
			}
		});
		super.onStart();
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
			Intent profileIntent = new Intent(this, ProfileActivity.class);
			startActivity(profileIntent);
			return true;
		
		case R.id.menuFriends:
			return true;
			
		case R.id.menuAbout:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

