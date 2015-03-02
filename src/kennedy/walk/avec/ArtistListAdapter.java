package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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

public class ArtistListAdapter extends BaseAdapter {

	List<String> artistItems;
	Context context;
	Activity parentActivity;
	
	public ArtistListAdapter(Context applicationContext, Activity p) {
		artistItems = new ArrayList<String>();
		context = applicationContext;
		parentActivity = p;
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
	

	public void addArtist(String artistName) {
		if (areThereDuplicates(artistName)) {
			Toast.makeText(context, 
					"You have already put " + artistName  + " into your list!", 
					Toast.LENGTH_LONG).show();
		} else {
			artistItems.add(artistName);
		}
	}

	static class ViewHolder {
		TextView artistName;
		Context context;
	}
	
	public Boolean areThereDuplicates(String artistName) {
		if (artistItems.contains(artistName)) {
			return true;
		} else {
			return false; 
		}
		

	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_artist, null);
			
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
		
		Button deleteBtn = (Button) convertView.findViewById(R.id.deleteArtistBtn);
		deleteBtn.setTag(position);
		deleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder deleteAlertBuilder = new AlertDialog.Builder(parentActivity);
				deleteAlertBuilder
				.setMessage("Are you sure you want to delete " + artistItems.get(position) + "?")
				.setCancelable(false)
				.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				})
				.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						artistItems.remove(position);
						notifyDataSetChanged();
						
					}
				});
			
				AlertDialog deleteArtistAlert = deleteAlertBuilder.create();
				deleteArtistAlert.show();
				
				
			}
		});
		
		return convertView;
	}

}
