package app.studio.crafty.wallpaper.daily.dailywallpaper;

import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.IOException;

import utils.AppController;

/**
 * Created by bunny on 11/04/17.
 */

public class WallPaperChangerJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        // Do some work here

        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        Log.e("Service ", "Started" );


        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("DailyWallPaperPref", 0); // 0 - for private mode

        String resolution = pref.getString("resolution", "1080x1920");

// If you are using normal ImageView
        String url = "https://source.unsplash.com/random/" + resolution;

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
                            = WallpaperManager.getInstance(getApplicationContext());
                    try {
                        myWallpaperManager.setBitmap(response.getBitmap());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    mBuilder.setContentTitle(getString(R.string.app_name));
                    mBuilder.setContentText("Your wallpaper Changed via job");

                    int mNotificationId = 123;
// Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());


                }
            }
        });


        int timePref = pref.getInt("Time", 0); // getting Integer

        int time = 3600;

        switch (timePref) {
            case 0:

                time= 3600 * 24;

                break;
            case 1:
                time= 3600 * 12;
                break;
            case 2:
                time= 3600 * 6;
                break;
            case 3:
                time= 1800;
                break;
            default:
                time= 3600 * 24;
                break;

        }
        Toast.makeText(this, "Time for job"+time, Toast.LENGTH_SHORT).show();

        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));


        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString("Category", "some_value");

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(WallPaperChangerJob.class)
                // uniquely identifies the job
                .setTag("my-wallchanger_job")
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(time, time + 3600))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        //Constraint.ON_UNMETERED_NETWORK,
                        // only run when the device is charging
                        //Constraint.DEVICE_CHARGING

                )
                .setExtras(myExtrasBundle)
                .build();

        dispatcher.mustSchedule(myJob);



        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {

       /* SharedPreferences pref = getApplicationContext().getSharedPreferences("DailyWallPaperPref", 0); // 0 - for private mode
        int timePref = pref.getInt("Time", 0); // getting Integer

int time = 3600;

        switch (timePref) {
            case 0:

                time= 3600 * 24;

                break;
            case 1:
                time= 3600 * 12;
                break;
            case 2:
                time= 3600 * 6;
                break;
            case 3:
                time= 3600 * 3;
                break;
            default:
                time= 3600 * 24;
                break;

        }

        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));


        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString("Category", "some_value");

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(WallPaperChangerJob.class)
                // uniquely identifies the job
                .setTag("my-wallchanger_job")
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(time, time + 3600))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        //Constraint.ON_UNMETERED_NETWORK,
                        // only run when the device is charging
                        //Constraint.DEVICE_CHARGING

                )
                .setExtras(myExtrasBundle)
                .build();

        dispatcher.mustSchedule(myJob);

*/
        return false; // Answers the question: "Should this job be retried?"
    }
}