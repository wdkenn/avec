package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("UseSparseArrays")
public class MainActivity extends Activity implements LocationListener {

	private LocationManager locMan;
	UserListAdapter adapter;
	
	ArrayList<String> usernamesNearby;
	ListView userListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Parse.initialize(getApplicationContext(), 
				"CEEVvOCi5hJmt5dC1FADXWaQeZd3KZud31Ls0M53", 
				"fFVSRHTNAfyFVHth27AtWOr0TfL25EhoUOnH981G");
		PushService.setDefaultPushCallback(getApplicationContext(), MainActivity.class);
		PushService.subscribe(getApplicationContext(), (String) ParseUser.getCurrentUser().get("Name"), UserProfileActivity.class);
		
		userListView = (ListView) findViewById(R.id.usersNearby);
		usernamesNearby = new ArrayList<String>();
		
		userListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				SimiliarUserItem user = (SimiliarUserItem) parent.getItemAtPosition(position);
				String username = user.username;
				Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
				intent.putExtra("username", username);
				startActivity(intent);
			}
		});
		
		Button refresh = (Button) findViewById(R.id.updateList);
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				
				// Check if GPS is enabled
				if (!locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					showGPSDialog();
				} else {
					startLocationMonitoring();
				}
				
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		PushService.unsubscribe(getApplicationContext(), (String) ParseUser.getCurrentUser().get("Name"));
		super.onDestroy();
	}

	
	@Override
	protected void onStart() {
		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Check if GPS is enabled
		if (!locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showGPSDialog();
		} else {
			startLocationMonitoring();
		}
		
		super.onStart();
	}
	

	@Override
	protected void onStop() {
		stopLocationMonitoring();
		super.onStop();
	}
	
	/*
	 * Modified code from: 
	 * http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
	 */
	private void showGPSDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Oops! Looks like you have Location Services disabled! " +
				"In order for Avec to work properly, you need to enable Location Services. " +
				"Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Intent callGPSSettingIntent = new Intent(
		                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                startActivity(callGPSSettingIntent);
					}
				});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		});
		
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
		
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
			Intent intent = new Intent(this, FriendActivity.class);
			startActivity(intent);
			return true;
			
		case R.id.menuAbout:
			return true;
			
		case R.id.menuChat:
			Intent chatIntent = new Intent(this, ChatActivity.class);
			startActivity(chatIntent);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	
	
	private void startLocationMonitoring() {
		locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1800000, 0, this);
	}

	private void stopLocationMonitoring() {
		locMan.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		ParseGeoPoint currentLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
		ParseUser currentUser = ParseUser.getCurrentUser();
		
		if (currentUser != null) {
			currentUser.put("location", currentLocation);
			
			try {
				currentUser.save();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			GetNearbyUsers downloader = new GetNearbyUsers();
			downloader.execute();
		}
		
	}

	private class GetNearbyUsers extends AsyncTask<Void, Void, Void> {

		ParseUser currentUser = ParseUser.getCurrentUser();
		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please wait while Avec loads users near you...");
			dialog.show();
			super.onPreExecute();
		}
		

		private ArrayList<SimiliarUserItem> userList = new ArrayList<SimiliarUserItem>();

		@Override
		protected Void doInBackground(Void... params) {
			ParseGeoPoint currentUserLocation = (ParseGeoPoint) currentUser.get("location");
			ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
			query.whereNear("location", currentUserLocation);
			query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
			query.setLimit(10);
			query.findInBackground(new FindCallback<ParseUser>() {
				
				@Override
				public void done(List<ParseUser> nearbyUserList, ParseException e) {
					if (e != null) {
						Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					} else {
						userList = sortUsers(nearbyUserList);
					}
					adapter = new UserListAdapter(getApplicationContext(), userList);
					userListView.setAdapter(adapter);
					if (dialog.isShowing()) {
						dialog.dismiss();
					}

				}
			});
			return null;
		}
		
		protected ArrayList<SimiliarUserItem> sortUsers(List<ParseUser> originalList) {

			
			ArrayList<String> myTopArtists = (ArrayList<String>) ParseUser.getCurrentUser().get("TopArtist");
			if (myTopArtists == null) {
				Toast.makeText(getApplicationContext(), "Please add artists to your list", Toast.LENGTH_LONG).show();
				return null;
			} else {
				HashMap<Integer, ArrayList<ParseUser>> orderedHashMap = new HashMap<Integer, ArrayList<ParseUser>>();
				int similiarity = 0;
				for (int i = 0; i < originalList.size(); i++) {
					ArrayList<String> userList = (ArrayList<String>) originalList.get(i).get("TopArtist");
					for (int j = 0; j < myTopArtists.size(); j++) {
						String compare = myTopArtists.get(j);
						if (userList.contains(compare)) {
							similiarity = similiarity + 1;
						}
					} 
					
					if (orderedHashMap.containsKey(similiarity)) {
						orderedHashMap.get(similiarity).add(originalList.get(i));
					} else {
						ArrayList<ParseUser> newList = new ArrayList<ParseUser>();
						newList.add(originalList.get(i));
						orderedHashMap.put(similiarity, newList);
					}
					
					similiarity = 0;
				}
				
				Iterator<Entry<Integer, ArrayList<ParseUser>>> it = orderedHashMap.entrySet().iterator();
				List<Integer> keyValues = new ArrayList<Integer>();
				while (it.hasNext()) {
					Entry<Integer, ArrayList<ParseUser>> pairs = it.next();
					keyValues.add(pairs.getKey());
				}
				
				Collections.sort(keyValues, new IntComparable());
				ArrayList<SimiliarUserItem> orderedUserItemList = new ArrayList<SimiliarUserItem>();
				for (int i = 0; i < keyValues.size(); i++) {
					int key = keyValues.get(i);
					ArrayList<ParseUser> users = orderedHashMap.get(key);
					for (int j = 0; j < users.size(); j++) {
						String email = users.get(j).getUsername();
						String username = (String) users.get(j).get("Name");
						String topThree = (String) users.get(j).get("TopThree");
								
					
						SimiliarUserItem newItem = new SimiliarUserItem(key, username, email, topThree);
						orderedUserItemList.add(newItem);
					}
				}

				return orderedUserItemList;
			}
			
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			
			
			
			
			
			super.onPostExecute(result);
		}
	}



	
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}
