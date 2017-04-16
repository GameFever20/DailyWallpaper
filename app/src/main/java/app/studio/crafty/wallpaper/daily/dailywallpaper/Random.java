package app.studio.crafty.wallpaper.daily.dailywallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;

import utils.AppController;

public class Random extends Activity {

    private String urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_layout);



        Toast.makeText(this, "Loading Wallpaper", Toast.LENGTH_SHORT).show();

        fetchDisplayInfo();

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();


// If you are using normal ImageView

        imageLoader.get(urlString, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Random", "Image Load Error: " + error.getMessage());
                Toast.makeText(Random.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageview

                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());
                    try {
                        myWallpaperManager.setBitmap(response.getBitmap());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Toast.makeText(Random.this, "WallPaper Changed", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });

        //test it
        finish();


    }

    private void fetchDisplayInfo() {

        String resolution;
        if (Build.VERSION.SDK_INT > 16) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            resolution = String.valueOf(width) + "x" + String.valueOf(height);
            //Toast.makeText(this, "Resolution is "+resolution, Toast.LENGTH_SHORT).show();
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            resolution = String.valueOf(width) + "x" + String.valueOf(height);
            // Toast.makeText(this, "Resolution is "+resolution, Toast.LENGTH_SHORT).show();

        }


        urlString = "https://source.unsplash.com/random/" + resolution;



    }

}
