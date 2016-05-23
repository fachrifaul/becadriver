/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.web.go_cak.drivergocak.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.activity.TransaksiActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Data: " + remoteMessage.getData());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Map data = remoteMessage.getData();
        String message = data.get("message").toString();
        Log.wtf(TAG, "onMessageReceived: "+message );
        sendNotification(message);
    }


    private void sendNotification(String msg) {
        Log.v(TAG, "message: " + msg);

        NotificationManager  mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent myIntent = new Intent(this, TransaksiActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("GOCAK Notification")
                        .setContentText("Tekan disini untuk melihat detail");


        mBuilder.setVibrate(new long[] { 100, 100, 100, 100, 100 });
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(0, notification);
    }
}
