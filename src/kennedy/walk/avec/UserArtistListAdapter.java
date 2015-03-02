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

public class UserArtistListAdapter extends BaseAdapter {

	List<String> artistItems;
	Context context;
	
	public UserArtistListAdapter(Context applicationContext, ArrayList<String> artists) {
		artistItems = artists;
		context = applicationContext;
	}
	
	@Override
	public int getCount() {
		return artistItems.size();
	}

	@Override
	public Object getItem(int position) {
		return artistItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView artistName;
		Context context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_artist_without_delete_btn, null);
			
			holder = new ViewHolder();
			holder.artistName = (TextView) convertView.findViewById(R.id.artistName);
			holder.context = parent.getContext();
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String item = artistItems.get(position);
		String name = String.valueOf(position + 1)+". " + item;
		holder.artistName.setText(name);
		
		
		return convertView;
	}

	
}
