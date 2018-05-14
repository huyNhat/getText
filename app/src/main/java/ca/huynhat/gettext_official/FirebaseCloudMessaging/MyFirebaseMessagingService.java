package ca.huynhat.gettext_official.FirebaseCloudMessaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ca.huynhat.gettext_official.Activities.ChatMessagesActivity;
import ca.huynhat.gettext_official.Activities.HomeActivity;
import ca.huynhat.gettext_official.Activities.ItemDetailsActivity;
import ca.huynhat.gettext_official.R;

/**
 * Created by huynhat on 2018-03-23.
 * Ref: https://codelabs.developers.google.com/codelabs/firebase-android/#9
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title=null, message_body=null,click_action=null;
        String message_id=null, chat_title=null, receiver_id=null, post_id=null;


        if(remoteMessage.getNotification()!=null){
            click_action=remoteMessage.getNotification().getClickAction();
        }
        switch (click_action){
            case "ChatMessageActivity":
                title = remoteMessage.getNotification().getTitle();
                message_body = remoteMessage.getNotification().getBody();
                message_id = remoteMessage.getData().get("message_id");
                chat_title = remoteMessage.getData().get("chat_title");
                receiver_id = remoteMessage.getData().get("receiver_chat");
                sendUserToChatActivity(title, message_body, click_action,message_id,chat_title,receiver_id);
                break;

            case "ItemDetailsActivity":
                title = remoteMessage.getNotification().getTitle();
                message_body = remoteMessage.getNotification().getBody();
                post_id = remoteMessage.getData().get("post_id");
                sendUserToItemDetailsActivity(title,message_body,click_action,post_id);
                break;

            default:
                break;

        }

        /*
        if(remoteMessage.getNotification()!=null){
                title = remoteMessage.getNotification().getTitle();
                message_body = remoteMessage.getNotification().getBody();
                click_action = remoteMessage.getNotification().getClickAction();
                Log.d(TAG, "Message received: "+ title+"\n"+message_body);

        }
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            message_id = remoteMessage.getData().get("message_id");
            chat_title = remoteMessage.getData().get("chat_title");
            receiver_id = remoteMessage.getData().get("receiver_chat");

        }
        sendNotification(title, message_body, click_action,message_id,chat_title,receiver_id);
        */

    }

    @Override
    public void onDeletedMessages() {
        //super.onDeletedMessages();
    }

    private void sendUserToItemDetailsActivity(String title, String messageBody, String click_action,
                                               String post_id){

        Intent intent;
        if(click_action.equals("ItemDetailsActivity")){
            intent = new Intent(this, ItemDetailsActivity.class);
            intent.putExtra("post_id",post_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }else {
            intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        createNotification(title,messageBody,pendingIntent);



    }

    /**
     * Ref: https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyFirebaseMessagingService.java
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendUserToChatActivity(String title,String messageBody, String click_action,
                                  String message_id, String chat_title, String receiver_id) {

        Intent intent;
        if(click_action.equals("ChatMessageActivity")){
            intent = new Intent(this, ChatMessagesActivity.class);
            intent.putExtra("message_id",message_id);
            intent.putExtra("chat_name",chat_title);
            intent.putExtra("receiver_id", receiver_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }else {
            intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        createNotification(title,messageBody,pendingIntent);
    }

    private void createNotification(String title, String messageBody, PendingIntent pendingIntent ){
        String channelId = "my_notification";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo_get_text)
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
