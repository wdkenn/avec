package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.List;

import kennedy.walk.avec.ArtistListAdapter.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserListAdapter extends BaseAdapter{

	List<SimiliarUserItem> userList;
	Context context;
	
	public UserListAdapter(Context applicationContext, ArrayList<SimiliarUserItem> list) {
		userList = list;
		context = applicationContext;
	}
	
	@Override
	public int getCount() {
		return userList.size();
	}

	@Override
	public Object getItem(int position) {
		return userList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	static class ViewHolder {
		TextView userName;
		TextView compatability;
		TextView topThree;
		Context context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_nearby_user, null);
			
			holder = new ViewHolder();
			holder.userName = (TextView) convertView.findViewById(R.id.otherUserName);
			holder.compatability = (TextView) convertView.findViewById(R.id.compatability);
			holder.topThree = (TextView) convertView.findViewById(R.id.topThree);
			holder.context = parent.getContext();
			
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		SimiliarUserItem item = userList.get(position);
		String name = "";
		if (item.username != null && !item.username.equals("")) {
			name = item.username;
		}
		
		holder.userName.setText(name);
		
		
		String similar = Integer.toString(item.similiarity);
		holder.compatability.setText(similar);
		
		String topThreeString = "";
		if (item.topThree != null && !item.topThree.equals("")) {
			topThreeString = item.topThree;
		}
		
		holder.topThree.setText(topThreeString);
		return convertView;
	}

}
