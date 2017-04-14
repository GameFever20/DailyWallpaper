package app.studio.crafty.wallpaper.daily.dailywallpaper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.IOException;

import utils.AppController;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by bunny on 13/04/17.
 */

public class DailyWallPaperReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("Reciever", "onReceive: Wall paperchange");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(context.getString(R.string.app_name));
        mBuilder.setContentText("Your Daily wallpaper Changed via reciever");

        int mNotificationId = 123;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        SharedPreferences pref = context.getSharedPreferences("DailyWallPaperPref", 0); // 0 - for private mode

        String resolution = pref.getString("resolution", "1080x1920");

// If you are using normal ImageView
        String url = "https://source.unsplash.com/featured/" + resolution;

        imageLoader.get(url, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Service ", "Image Load Error: " + error.getMessage());
                //Toast.makeText(MainActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {

                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(context);
                    try {
                        myWallpaperManager.setBitmap(response.getBitmap());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        });

        checkRateUs(context);

    }


    public void checkRateUs(Context context) {
        SharedPreferences pref = context.getSharedPreferences("DailyWallPaperPref", 0); // 0 - for private mode
        int checkNum = pref.getInt("RateUs", 0); // getting Integer

        if (checkNum == 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=app.studio.crafty.wallpaper.daily.dailywallpaper"));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            2,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle("Rate us");
            mBuilder.setContentText("We appreciate your feedback");
            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId = 123;
// Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());


        } else if (checkNum == 15) {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=app.studio.crafty.wallpaper.daily.dailywallpaper"));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            2,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle("Rate us");
            mBuilder.setContentText("We appreciate your feedback");
            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId = 123;
// Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());


        } else if (checkNum > 15) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");

            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Get Awsome Wallpaper and automaticall change wallpaper daily");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=app.studio.crafty.wallpaper.daily.dailywallpaper");


            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            2,
                            sharingIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle("Share the love");
            mBuilder.setContentText("Share app with your friend and family");
            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId = 123;
// Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());


        } else {
            checkNum++;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("RateUs", checkNum); // Storing integer

            editor.apply(); // commit changes


        }


    }

}
