package app.studio.crafty.wallpaper.daily.dailywallpaper;

import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    }

}