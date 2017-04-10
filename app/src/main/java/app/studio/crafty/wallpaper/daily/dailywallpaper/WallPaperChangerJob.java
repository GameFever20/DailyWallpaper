package app.studio.crafty.wallpaper.daily.dailywallpaper;

import android.app.WallpaperManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

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


// If you are using normal ImageView
        String url = "https://source.unsplash.com/category/nature/1080x1920";

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

                }
            }
        });

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}