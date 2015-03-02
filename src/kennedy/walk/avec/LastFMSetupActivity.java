package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.parse.ParseException;
import com.parse.ParseUser;

import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LastFMSetupActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_lastfm_signup);
		
		
		final EditText lastFMUsername = (EditText) findViewById(R.id.lastFMET);
		Button confirmUsername = (Button) findViewById(R.id.lastfmBtn);
		confirmUsername.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String lastfmUser = lastFMUsername.getText().toString();
				DownloadLastFMArtists downloader = new DownloadLastFMArtists();
				downloader.execute(lastfmUser);
			}
		});
		super.onCreate(savedInstanceState);
	}
	
	private class DownloadLastFMArtists extends AsyncTask<String, Void, Boolean> {

		/*
		 * Progress bar spinner code adapted from:
		 * http://stackoverflow.com/questions/11752961/how-to-show-a-progress-spinner-in-android-when-doinbackground-is-being-execut
		 */
		String key = "cfa596e1e701f2b0a25a0006505198bf";
		ArrayList<String> topArtistList = new ArrayList<String>();
		private ProgressDialog dialog = new ProgressDialog(LastFMSetupActivity.this);

		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please wait while we finish creating your profile...");
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			String username = params[0];
			Caller.getInstance().setCache(null);
			Caller.getInstance().setUserAgent(username);
			Collection<Artist> topArtists = User.getTopArtists(username, key);
			Iterator<Artist> iterate = topArtists.iterator();
			while (iterate.hasNext())
			{
				topArtistList.add(iterate.next().getName());
			}
			
			if (topArtistList.size() == 0) {
				return false;
			} else {
				String topThree = "";
				if (topArtistList.size() >= 3) {
					for (int i = 0; i < 3; i++) {
						if (i==0) {
							topThree = topArtistList.get(i);
						} else {
							topThree = topThree + ", " + topArtistList.get(i);
						}
					}
				} else {
					for (int i = 0; i < topArtistList.size(); i++) {
						if (i==0) {
							topThree = topArtistList.get(i);
						} else {
							topThree = topThree + ", " + topArtistList.get(i);
						}
					}
				}
				ParseUser current = ParseUser.getCurrentUser();
				if(current != null) {
					current.put("TopArtist", topArtistList);
					current.put("LastFMUsername", username);
					current.put("TopThree", topThree);
					current.put("Friends", new ArrayList<String>());
					try {
						current.save();
						return true;
					} catch (ParseException e) {
						
						e.printStackTrace();
						return false;
					}
				} else {
					return false;
				}
			}
			
			
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (result) {
				Intent intent = new Intent(LastFMSetupActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), 
						"Error: Please check that your username exists and that you are connected to the internet", 
						Toast.LENGTH_LONG).show();
			}
			
			super.onPostExecute(result);
		}
		
	}
}
