package kennedy.walk.avec;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FriendRequestReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		String jsonData = extras.getString("com.parse.Data");
		JSONObject json = null;
		try {
			json = new JSONObject(jsonData);
		} catch (JSONException e) {
			Toast.makeText(context, "ERROR- " + e, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		String name = json.optString("name");
		
		Intent newIntent = new Intent(context, UserProfileActivity.class);
		newIntent.putExtra("username", name);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, newIntent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification.Builder n  = new Notification.Builder(context)
		        .setContentTitle("Avec")
		        .setContentText(name + " has sent you a friend request!")
		        .setTicker(name + " has sent you a friend request!")
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentIntent(pIntent)
		        .setAutoCancel(true)
		        .setSound(soundUri);
		    
		  
		NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		

		nManager.notify(0, n.build());
		
	}

}
