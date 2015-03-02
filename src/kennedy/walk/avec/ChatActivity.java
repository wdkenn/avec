package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity{

	static ParseObject chat;
	static ChatMessageAdapter adapter;
	static ListView convo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_chat);
		// check and see if chat between users already exists
		// if yes, load previous messages
		// if no, create new chat
			// send objectID to other user
		Bundle bundle = getIntent().getExtras();
		final String username = bundle.getString("username");
		TextView title = (TextView) findViewById(R.id.chat_other_username);
		title.setText(username);
		convo  = (ListView) findViewById(R.id.chat);
		final String currentUser = (String) ParseUser.getCurrentUser().get("Name");
		adapter = new ChatMessageAdapter(getApplicationContext());
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");
		query.whereEqualTo("Users", ParseUser.getCurrentUser().get("Name")+ "::" + username);
		
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> conversation, ParseException e) {
				if (!conversation.isEmpty()) {
					chat = conversation.get(0);
					adapter.addPreExistingConversation((ArrayList<String>) chat.get("Conversation"));
					convo.setDivider(null);
					convo.setAdapter(adapter);
				} else {
					// second check
					ParseQuery<ParseObject> secondQuery = ParseQuery.getQuery("Chat");
					secondQuery.whereEqualTo("Users", username + "::" + ParseUser.getCurrentUser().get("Name"));
					secondQuery.findInBackground(new FindCallback<ParseObject>() {

						@Override
						public void done(List<ParseObject> c,
								ParseException e) {
							
							if (!c.isEmpty()) {
								chat = c.get(0);
								adapter.addPreExistingConversation((ArrayList<String>) chat.get("Conversation"));
							} else {
								chat = new ParseObject("Chat");
								chat.add("Users", ParseUser.getCurrentUser().get("Name")+ "::" + username);
								ParseUser.getCurrentUser().add("ChatUsernames", ParseUser.getCurrentUser().get("Name")+ "::" + username);
								try {
									ParseUser.getCurrentUser().save();
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
								chat.saveInBackground();
							}
							ListView convo = (ListView) findViewById(R.id.chat);
							convo.setDivider(null);
							convo.setAdapter(adapter);
							
						}
					});
				}
				
				
			}
		});
		
		
		
		
		
		
		
		// send button sends if text is in edit text
			// sends push to user of new message
			// stores message in shared chat object
			// refreshes listview for both users
		
		final EditText messageTyped = (EditText) findViewById(R.id.message);
		
		Button sendBtn = (Button) findViewById(R.id.sendMessage);
		sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String message = messageTyped.getText().toString();
				if (!message.equals("")) {
					messageTyped.setText("");
					chat.add("Conversation",  ParseUser.getCurrentUser().get("Name") + "::" + message);
					chat.saveInBackground();
					
					adapter.addLocalMessage(ParseUser.getCurrentUser().get("Name") + "::" + message);
					adapter.notifyDataSetChanged();
					convo.smoothScrollToPosition(adapter.getCount());
					
					JSONObject data = new JSONObject();
					
					try {
						data.put("name", ParseUser.getCurrentUser().get("Name"));
						data.put("message", ParseUser.getCurrentUser().get("Name") + "::" + message);
						data.put("action", "kennedy.walk.avec.NOTIFY_CHAT");

					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
					ParsePush push = new ParsePush();
					
					push.setChannel(username);
					push.setData(data);
					push.sendInBackground();
				}
			
			}
		});
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Avec.activityResumed();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Avec.activityPaused();
	}
	
	@Override
	protected void onStop() {
		Avec.activityPaused();
		super.onStop();
	}
	
	static public void chatUpdate(Context context, String m) {
		adapter.addLocalMessage(m);
		adapter.notifyDataSetChanged();
		convo.smoothScrollToPosition(adapter.getCount());
		
	}
	

}
