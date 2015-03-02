package kennedy.walk.avec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseUser;


import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ProfileSetupActivity extends Activity{

	ArrayList<String> artistPossibilities;
	private ArrayAdapter<String> adapter;
	AutoCompleteTextView artistEntry;
	Boolean itemClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_profilesetup);

		AlertDialog.Builder welcomeBuilder = new AlertDialog.Builder(this);
		welcomeBuilder
		.setMessage("Hello! Thank you for joining the Avec community! To create your profile, " +
				"simply type the names of your favorite artists in the search bar at the " +
				"top of the screen and click 'Add artist to list.' You can enter up to 50 artists! " +
				"Once you're done, click submit list to get started! Don't worry if your musical interests" +
				" change, you can always change your favorite artist list!")
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

		AlertDialog welcome = welcomeBuilder.create();
		welcome.show();



		final ListView userArtistListView = (ListView) findViewById(R.id.userTopArtistList);
		artistPossibilities = new ArrayList<String>();
		artistEntry = (AutoCompleteTextView) findViewById(R.id.artist);
		artistEntry.setThreshold(2);
		artistEntry.addTextChangedListener(new TextWatcher() {
			String artistTypedSoFar;
			@Override
			public void onTextChanged(CharSequence artist, int start, int before, int count) {

				if (!itemClicked) {
					if (!artist.toString().isEmpty() ) {
						artistPossibilities.clear();
						artistTypedSoFar = artist.toString();
						GetArtistSuggestions downloader = new GetArtistSuggestions();
						downloader.execute(artistTypedSoFar);
					} else {
						artistEntry.dismissDropDown();
					}
				}

				





			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {


			}
		});

		artistEntry.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				itemClicked = true;
				
			}
		});


		final ArtistListAdapter adapter = new ArtistListAdapter(getApplicationContext(), ProfileSetupActivity.this);
		userArtistListView.setAdapter(adapter);

		Button addArtistBtn = (Button) findViewById(R.id.addArtistBtn);
		addArtistBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				if (adapter.artistItems.size() >= 50) {
					Toast.makeText(getApplicationContext(),
							"You have reached the maximum number of artists for your list!", 
							Toast.LENGTH_LONG).show();
				} else {
					String artistAdded = artistEntry.getText().toString();
					if (!artistAdded.isEmpty()) {

						adapter.addArtist(artistAdded);
						adapter.notifyDataSetChanged();
						artistEntry.setText("");


					} else {
						Toast.makeText(getApplicationContext(), 
								"Please type a name into the search box!", 
								Toast.LENGTH_LONG).show();
					}
				}



			}
		});

		Button submitListBtn = (Button) findViewById(R.id.submitList);
		submitListBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (adapter.artistItems.isEmpty()) {
					Toast.makeText(getApplicationContext(), 
							"Please put AT LEAST one artist in your list before submitting!", 
							Toast.LENGTH_LONG).show();
				} else {
					DownloadTopArtistList downloader = new DownloadTopArtistList();
					downloader.execute(adapter.artistItems);
				}

			}
		});
		super.onCreate(savedInstanceState);
	}

	private class DownloadTopArtistList extends AsyncTask<List<String>, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(ProfileSetupActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please wait while we finish creating your profile...");
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(List<String>... params) {


			String topThree = "";
			if (params[0].size() >= 3) {
				for (int i = 0; i < 3; i++) {
					if (i==0) {
						topThree = params[0].get(i);
					} else {
						topThree = topThree + ", " + params[0].get(i);
					}
				}
			} else {
				for (int i = 0; i < params[0].size(); i++) {
					if (i==0) {
						topThree = params[0].get(i);
					} else {
						topThree = topThree + ", " + params[0].get(i);
					}
				}
			}
			ParseUser current = ParseUser.getCurrentUser();
			if (current != null) {
				current.put("TopArtist", params[0]);
				current.put("TopThree", topThree);
				current.put("Friends", new ArrayList<String>());
				try {
					current.save();
				} catch (ParseException e) {
					e.printStackTrace();
					return false;
				}

				return true;
			} else {
				Toast.makeText(getApplicationContext(), 
						"ERROR: Please restart app and try again", 
						Toast.LENGTH_LONG).show();
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (result) {
				Intent intent = new Intent(ProfileSetupActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), 
						"Error: Please check that you are connected to the internet", 
						Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}

	}

	private class GetArtistSuggestions extends AsyncTask<String, Void, Void>{

		final String key = "cfa596e1e701f2b0a25a0006505198bf";

		@Override
		protected Void doInBackground(String... params) {

			String artistName = params[0];
			Caller.getInstance().setCache(null);
			Collection<Artist> artistSuggestions = Artist.search(artistName, key);
			Iterator<Artist> iterate = artistSuggestions.iterator();
			while (iterate.hasNext())
			{
				String newArtist = iterate.next().getName();
				artistPossibilities.add(newArtist);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (itemClicked) {
				artistEntry.dismissDropDown();
				itemClicked = false;
			} else {
				if (artistPossibilities.size() > 0) {
					adapter = new ArrayAdapter<String>(ProfileSetupActivity.this, android.R.layout.simple_dropdown_item_1line, artistPossibilities);
					artistEntry.setAdapter(adapter);
					if (itemClicked) {
						artistEntry.dismissDropDown();
					} else {
						artistEntry.showDropDown();
					}
				}
			}
			


			super.onPostExecute(result);
		}

	}
}
