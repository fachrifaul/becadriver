package id.web.go_cak.drivergocak.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.activity.ReceiveActivity;

public class GcmIntentService extends IntentService{
	Context context;
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String TAG = "GCM Demo";

	public GcmIntentService() {
		super("GcmIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		String address = extras.getString("message");
		Log.i("COba Dump", "address "+address+" hasilna : "+intent.toString()+" , Extras : "+extras.toString());
		String msg = intent.getStringExtra("message");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		
		 if (!extras.isEmpty()) {
			 
			 if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
	                sendNotification("Send error: " + extras.toString());
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_DELETED.equals(messageType)) {
	                sendNotification("Deleted messages on server: " +
	                        extras.toString());
	            // If it's a regular GCM message, do some work.
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	                // This loop represents the service doing some work.
	                for (int i=0; i<5; i++) {
	                    Log.i(TAG, "Working... " + (i+1)
	                            + "/5 @ " + SystemClock.elapsedRealtime());
	                    try {
	                        Thread.sleep(500);
	                    } catch (InterruptedException e) {
	                    }
	                }
	                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
	                // Post notification of received message.
	                //sendNotification("Received: " + extras.toString());
				 	//String Kirim = extras.toString().replace("Bundle","message :").replace("=",":");
	                //sendNotification(address);
				 	sendNotification(msg);
	                Log.i(TAG, "Dari GCM : "+msg+"Received: " + extras.toString());

				 try {
					 Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					 Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
					 r.play();
				 } catch (Exception e) {
					 e.printStackTrace();
				 }

	            }
	        }
		 GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        Intent myIntent = new Intent(this, ReceiveActivity.class);
		//Intent myIntent = new Intent(this, ConfirmationActivity.class);
        myIntent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.icon)
        .setContentTitle("GOCAK Notification")
        .setContentText("Tekan disini untuk melihat detail");

		//.bigText(msg))


		mBuilder.setVibrate(new long[] { 100, 100, 100, 100, 100 });
		// mId allows you to update the notification later on.
		mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		mBuilder.setContentIntent(contentIntent);
		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
	
}