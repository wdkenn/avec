package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.Bundle;

public class ChatListActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_chat_list);
		super.onCreate(savedInstanceState);
		ArrayList<ChatPreview> chats =  new ArrayList<ChatPreview>();
		
		chats = (ArrayList<ChatPreview>) ParseUser.getCurrentUser().get("ChatUsernames");
		
	}
}
