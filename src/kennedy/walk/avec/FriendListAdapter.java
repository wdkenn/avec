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

public class FriendListAdapter extends BaseAdapter{

	List<String> friendItems;
	Context context;
	
	public FriendListAdapter(Context c) {
		context = c;
		friendItems = new ArrayList<String>();
		ParseUser current = ParseUser.getCurrentUser();
		if (current != null) {
			friendItems = (ArrayList<String>) current.get("Friends");
		}
	}
	
	@Override
	public int getCount() {
		return friendItems.size();
	}

	@Override
	public Object getItem(int position) {
		return friendItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	static class ViewHolder {
		TextView friendName;
		Context context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_artist_without_delete_btn, null);
			
			holder = new ViewHolder();
			holder.friendName = (TextView) convertView.findViewById(R.id.artistName);
			holder.context = parent.getContext();
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String item = friendItems.get(position);
		holder.friendName.setText(item);
		
		
		return convertView;
		
	}

}
