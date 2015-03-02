package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatMessageAdapter extends BaseAdapter{

	List<String> messages;
	Context context;
	int count;
	
	public ChatMessageAdapter(Context c) {
		messages = new ArrayList<String>();
		context = c;
		count = 0;
	}
	
	public void addLocalMessage(String m) {
		messages.add(m);
	}
	
	public void addPreExistingConversation(ArrayList<String> conversation) {
		messages = conversation;
	}
	
	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	static class ViewHolder {
		TextView message;
		Context context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		String messageAndUsername = messages.get(position);
		String username = messageAndUsername.substring(0, messageAndUsername.indexOf("::"));
		String message = messageAndUsername.substring(messageAndUsername.indexOf("::")+2);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			String current = (String) ParseUser.getCurrentUser().get("Name");
			
			if (username.equalsIgnoreCase(current)) {
				convertView = inflater.inflate(R.layout.item_message_right, null);
			} else {
				convertView = inflater.inflate(R.layout.item_message_left, null);
			}
			
			holder = new ViewHolder();
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			holder.context = parent.getContext();
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		holder.message.setText(message);
		return convertView;
	}

}
