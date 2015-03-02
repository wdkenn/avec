package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

public class ProfileArtistListAdapter extends BaseAdapter{

	List<String> artistItems;
	Context context;
	Activity parentActivity;
	
	public ProfileArtistListAdapter(Context c, Activity a) {
		context = c;
		artistItems = new ArrayList<String>();
		parentActivity = a;
		ParseUser current = ParseUser.getCurrentUser();
		artistItems = (ArrayList<String>) current.get("TopArtist");
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
	public View getView(final int position, View convertView, ViewGroup parent) {
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
		
//		Button deleteBtn = (Button) convertView.findViewById(R.id.deleteArtistBtn);
//		deleteBtn.setTag(position);
//		deleteBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				AlertDialog.Builder deleteArtistBuilder = new AlertDialog.Builder(parentActivity);
//				deleteArtistBuilder
//				.setMessage("Are you sure you want to delete " + artistItems.get(position) + "?")
//				.setCancelable(false)
//				.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//						
//					}
//				})
//				.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						artistItems.remove(position);
//						ParseUser current = ParseUser.getCurrentUser();
//						current.put("TopArtist", artistItems);
//						try {
//							current.save();
//						} catch (ParseException e) {
//							Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
//							e.printStackTrace();
//						}
//						
//						notifyDataSetChanged();
//						
//					}
//				});
//				
//				AlertDialog deleteArtistAlert = deleteArtistBuilder.create();
//				deleteArtistAlert.show();
//			}
//		});
		return convertView;
	}

}
