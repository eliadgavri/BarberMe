package service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.barberme.R;
import com.google.firebase.firestore.FirebaseFirestore;

import ui.SplashScreenActivity;
import userData.User;

public class UploadNewUserService extends Service {

    User user;
    private static final int ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


       String uID=intent.getStringExtra("uID");
       String firstName=intent.getStringExtra("firstName");
       String lastName=intent.getStringExtra("lastName");
       String password=intent.getStringExtra("password");
       String email=intent.getStringExtra("email");
       String profilePicture=intent.getStringExtra("profilePicture");
       String gender=intent.getStringExtra("gender");
       String birthday=intent.getStringExtra("birthday");
       String address=intent.getStringExtra("address");

       user=new User(uID,firstName,lastName,password,email,profilePicture,gender,birthday,address);

        startForeground(ID, createNotification());
        uploadNewUser(user);

        return Service.START_NOT_STICKY;
    }

    private void uploadNewUser(User user) {

        FirebaseFirestore.getInstance().collection("users")
                .add(user)
                .addOnSuccessListener(docRef -> {
                    stopSelf();
                })
                .addOnFailureListener(ex -> {
                    ex.printStackTrace();
                    showMessageAndFinish("failed to upload");
                });

    }

    private Notification createNotification() {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = getPackageName() + "UploadNotifID";
        if(Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Important Notification", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Important Notification");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);

        // Create notification action intent..
        Intent notificationIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("BarberMe")
                .setContentText(user.getFirstName()+ " " +user.getLastName()+ " " +"Welcome to your BarberHouse.")
                .setContentIntent(pi);

        return notificationBuilder.build();
    }

    private void showMessageAndFinish(String msg) {
        try {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            stopSelf();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
