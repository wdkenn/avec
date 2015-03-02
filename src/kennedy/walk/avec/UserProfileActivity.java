package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends Activity {

	String username;
	ArrayList<String> userTopArtistList;
	ArrayList<String> friendList;
	ListView artistList;
	Boolean isMyFriend;
	Button addFriendBtn;
	Drawable d;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		addFriendBtn = (Button) findViewById(R.id.addFriendBtn);
		d = addFriendBtn.getBackground();

		friendList = new ArrayList<String>();
		artistList = (ListView) findViewById(R.id.userArtistListViewProfile);
		userTopArtistList = new ArrayList<String>();
		Bundle bundle = getIntent().getExtras();
		username = bundle.getString("username");
		ParseUser current = ParseUser.getCurrentUser();
		friendList = (ArrayList<String>) current.get("Friends");
		if (friendList.contains(username)) {
			isMyFriend = true;
			addFriendBtn.setBackgroundColor(getResources().getColor(R.color.green));
			addFriendBtn.setText("FRIEND");
			addFriendBtn.setTextSize(16);
		} else {
			isMyFriend = false;
			addFriendBtn.setText("Add Friend");
			addFriendBtn.setTextSize(14);
		}
		TextView usernameTV = (TextView) findViewById(R.id.usernameProfile);
		usernameTV.setText(username);
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("Name", username);
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> user, ParseException e) {
				if (e==null) {
					if (user.size() > 0) {
						ParseUser userProfile = user.get(0);
						userTopArtistList = (ArrayList<String>) userProfile.get("TopArtist");
						UserArtistListAdapter adapter = new UserArtistListAdapter(getApplicationContext(), userTopArtistList);
						artistList.setAdapter(adapter);
					} else {
						Toast.makeText(getApplicationContext(), "Error loading -- User not found", Toast.LENGTH_LONG).show();

					}
					
				} else {
					Toast.makeText(getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		addFriendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ParseUser current = ParseUser.getCurrentUser();
				if (isMyFriend) {
					showCancelDialog();
				} else {
					isMyFriend = true;
					addFriendBtn.setBackgroundColor(getResources().getColor(R.color.green));
					addFriendBtn.setText("FRIEND");
					addFriendBtn.setTextSize(16);
					JSONObject data = new JSONObject();
					
					try {
						data.put("name", ParseUser.getCurrentUser().get("Name"));
						data.put("action", "kennedy.walk.avec.NOTIFY_FRIEND");

					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
					ParsePush push = new ParsePush();
					
					push.setChannel(username);
					push.setData(data);
					push.sendInBackground();
					
					current.add("Friends", username);
					try {
						current.save();
					} catch (ParseException e) {
						Toast.makeText(getApplicationContext(), "Error: Friend not added", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}
				
				
				
			}
		});
		
		Button chatBtn = (Button) findViewById(R.id.chatBtn);
		chatBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
				intent.putExtra("username", username);
				startActivity(intent);
				
			}
		});
	}
	
	private void showCancelDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Are you sure you want to remove " + username + " from your friends?" )
				.setCancelable(false)
				.setPositiveButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		alertDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isMyFriend = false;
				addFriendBtn.setText("Add Friend");
				addFriendBtn.setTextSize(14);
				addFriendBtn.setBackground(d);
				
				friendList.remove(username);
				ParseUser current = ParseUser.getCurrentUser();
				current.put("Friends", friendList);
				try {
					current.save();
				} catch (ParseException e) {
					Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_LONG);
					e.printStackTrace();
				}
				
			}
		});
		
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}
}
