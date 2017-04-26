package app.studio.crafty.wallpaper.daily.dailywallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.tapadoo.alerter.Alerter;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;
import org.michaelevans.colorart.library.ColorArt;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import utils.AppController;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main activity";
    ImageView imageView;
    Bitmap imageBitmap;

    SpaceNavigationView spaceNavigationView;
    boolean isAutoSettingCurrentItem = false;
    boolean isAutoSettingCurrentItem1 = false;

    SwipeRefreshLayout swipeRefreshLayout;

    //want to change initial
    String urlString = "https://source.unsplash.com/featured/1080x1920";
    String resolution = "";

    int showAdCount = 0;

    public boolean isSaved = false;

    //BottomSheet Variable
    View bottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(getApplicationContext());
                try {
                    myWallpaperManager.setBitmap(imageBitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        });
        fab.hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        fetchDisplayInfo();
        imageView = (ImageView) findViewById(R.id.imageView2);
        refreshWallPaper();


        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        try {
            spaceNavigationView.initWithSaveInstanceState(savedInstanceState);

            spaceNavigationView.addSpaceItem(new SpaceItem("Save", R.drawable.ic_menu_gallery));
            spaceNavigationView.addSpaceItem(new SpaceItem("Set", R.drawable.ic_menu_camera));
        } catch (Exception e) {
            e.printStackTrace();
        }

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                //Toast.makeText(MainActivity.this, "onCentreButtonClick", Toast.LENGTH_SHORT).show();
                refreshWallPaper();
                checkAdShown();


            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                //Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();

                if (itemIndex == 1) {
                    if (!isAutoSettingCurrentItem1) {
                        showAlert("WallPaper Set ", "");
                        setAsWallPaper();
                    }
                    isAutoSettingCurrentItem1 = false;


                } else if (itemIndex == 0) {
                    if (!isAutoSettingCurrentItem) {
                        if (!isSaved) {
                            //Toast.makeText(MainActivity.this, "Image saved in " + saveToInternalStorage(imageBitmap, "Wall_"), Toast.LENGTH_LONG).show();
                            //showAlert("WallPaper Saved ", saveToInternalStorage(imageBitmap, "Wall_"));
                            saveToInternalStorage(imageBitmap, "Wall_");
                            //spaceNavigationView.setCentreButtonSelected();
                            //scheduleWallChangeJob(3600);
                            isSaved = true;
                        }
                    }
                    isAutoSettingCurrentItem = false;
                }

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                //Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                if (itemIndex == 0) {
                    if (!isAutoSettingCurrentItem) {
                        if (!isSaved) {
                            //Toast.makeText(MainActivity.this, "Image saved in reselected" + saveToInternalStorage(imageBitmap, "Wall_"), Toast.LENGTH_LONG).show();
                            //spaceNavigationView.setCentreButtonSelected();
                            //showAlert("WallPaper Saved ", saveToInternalStorage(imageBitmap, "Wall_"));
                            saveToInternalStorage(imageBitmap, "Wall_");
                            isSaved = true;
                        }
                    }
                    isAutoSettingCurrentItem = false;

                } else if (itemIndex == 1) {
                    if (!isAutoSettingCurrentItem1) {

                        setAsWallPaper();
                    }
                    isAutoSettingCurrentItem1 = false;


                }
            }
        });


        spaceNavigationView.setCentreButtonSelectable(true);
        setSpaceNavHeight();

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                spaceNavigationView.setVisibility(View.INVISIBLE);

                return true;
                //return false to achivec if preview only when user is holding image view
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spaceNavigationView.getVisibility() == View.INVISIBLE
                        || spaceNavigationView.getVisibility() == View.GONE) {
                    spaceNavigationView.setVisibility(View.VISIBLE);
                }

                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWallPaper();

            }
        });


        showSplashScreen();


        initializeBottomSheet();

        initializeInterstitialAd();


    }

    private void initializeNativeAd() {
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adView);

        AdRequest request = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_ID")
                .build();
        adView.loadAd(request);
    }

    private void initializeInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

            }
        });

        requestNewInterstitial();


    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void initializeBottomSheet() {
        bottomSheet = findViewById(R.id.bottom_sheet);
        try {
            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void openBottomSheet() {

        try {
            ColorArt colorArt = new ColorArt(imageBitmap);
            bottomSheet.setBackgroundColor(colorArt.getBackgroundColor());
            bottomSheet.setAlpha(0.90f);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            TextView textview = (TextView) findViewById(R.id.bottom_sheet_textView);
            textview.setTextColor(colorArt.getDetailColor());

            MultiStateToggleButton multiButton = (MultiStateToggleButton) findViewById(R.id.mstb_multi_id);
            multiButton.setColors(colorArt.getDetailColor(), colorArt.getPrimaryColor());


            multiButton.setValue(getTimePrefrenceValue());

            multiButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
                @Override
                public void onValueChanged(int value) {
                    setTimePrefrenceValue(value);
                    postjob(value);
                }
            });

            initializeNativeAd();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void postjob(int value) {
        switch (value) {
            case 0:
                scheduleWallChangeJob(3600 * 24);
                break;
            case 1:
                scheduleWallChangeJob(3600 * 12);
                break;
            case 2:
                scheduleWallChangeJob(3600 * 6);
                break;
            case 3:
                scheduleWallChangeJob(3600 * 3);
                //Toast.makeText(this, "Job time 1800", Toast.LENGTH_SHORT).show();
                break;
            default:
                scheduleWallChangeJob(3600 * 24);
                break;

        }
    }

    private int getTimePrefrenceValue() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DailyWallPaperPref", 0); // 0 - for private mode
        return (pref.getInt("Time", 0)); // getting Integer


    }

    private void setTimePrefrenceValue(int timePrefrenceIndex) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DailyWallPaperPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("Time", timePrefrenceIndex); // Storing integer

        editor.apply(); // commit changes

    }

    private void fetchDisplayInfo() {

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

        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("DailyWallPaperPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("resolution", resolution); // Storing integer

            editor.apply(); // commit changes

        } catch (Exception e) {
            e.printStackTrace();
        }
        urlString = "https://source.unsplash.com/featured/" + resolution;


    }

    private void setSpaceViewColor() {

        try {
            if (imageBitmap != null) {
                ColorArt colorArt = new ColorArt(imageBitmap);
                spaceNavigationView.invalidate();

                //spaceNavigationView.changeSpaceBackgroundColor(ContextCompat.getColor(this, colorArt.getBackgroundColor()));
                spaceNavigationView.changeSpaceBackgroundColor(colorArt.getBackgroundColor());
                spaceNavigationView.setInActiveSpaceItemColor(colorArt.getPrimaryColor());
                spaceNavigationView.setActiveSpaceItemColor(colorArt.getDetailColor());
                spaceNavigationView.setCentreButtonColor(colorArt.getSecondaryColor());
                spaceNavigationView.setActiveCentreButtonBackgroundColor(colorArt.getSecondaryColor());

                isAutoSettingCurrentItem = true;
                spaceNavigationView.changeCurrentItem(0);
                isAutoSettingCurrentItem1 = true;
                spaceNavigationView.changeCurrentItem(1);


                spaceNavigationView.setCentreButtonSelected();


                //Toast.makeText(this, "Color changed to "+colorArt.getBackgroundColor(), Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSpaceNavHeight() {
        int margin = getNavBarHeight(this);

        LinearLayout linear = (LinearLayout) findViewById(R.id.space_viewGroup_layout);


        View view = (View) findViewById(R.id.space_bottomView);

        view.getLayoutParams().height = margin;


    }

    private void hideStatusBar() {


        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

        );

    }

    private void refreshWallPaper() {
        loadImage();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id) {
            case R.id.nav_featured:
                onFeaturedClick();
                break;
            case R.id.nav_random:
                onRandomClick();
                break;
            case R.id.nav_nature:
                onCategoryClick("nature");
                break;
            case R.id.nav_food:
                onCategoryClick("food");
                break;
            case R.id.nav_people:
                onCategoryClick("people");
                break;
            case R.id.nav_buildings:
                onCategoryClick("buildings");
                break;
            case R.id.nav_objects:
                onCategoryClick("objects");
                break;
            case R.id.nav_technology:
                onCategoryClick("technology");
                break;
            case R.id.nav_wall_setting:
                onWallSetting();
                break;
            case R.id.nav_send_wall:
                onWallSendClick();
                break;
            case R.id.nav_rate_us:
                onRateUsClick();
                break;
            case R.id.nav_share:
                onShareClick();
                break;
            case R.id.nav_feedback:
                onFeedbackClick();
                break;
            case R.id.nav_about_us:
                onAboutUsClick();
                break;


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onAboutUsClick() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://acraftystudio.wixsite.com/android")));

        } catch (Exception exception) {

        }
    }

    private void onFeedbackClick() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"acraftystudio@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion for Daily Wallpaper App");
        intent.putExtra(Intent.EXTRA_TEXT, "Your suggestion here \n");

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email App"));
    }

    private void onShareClick() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Get Awsome Wallpaper and automaticall change wallpaper daily");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=app.studio.crafty.wallpapersplash.daily.dailywallpaper");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    private void onWallSendClick() {
        Intent share = new Intent(Intent.ACTION_SEND);
        if (isStoragePermissionGranted()) {
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));
            f.mkdirs();
            f = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + File.separator + "Shared.jpg");

            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(f.getPath()));
            share.putExtra(Intent.EXTRA_TEXT, "Get Full HD WallPaper and auto - change wallpaper daily  \n https://play.google.com/store/apps/details?id=app.studio.crafty.wallpapersplash.daily.dailywallpaper");
            startActivity(Intent.createChooser(share, "Share Image"));
        }
    }

    private void onRateUsClick() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=app.studio.crafty.wallpapersplash.daily.dailywallpaper")));
        } catch (Exception e) {

        }
    }

    private void onWallSetting() {

        openBottomSheet();
    }

    private void onCategoryClick(String category) {
        urlString = "https://source.unsplash.com/category/" + category + "/" + resolution;
        refreshWallPaper();
    }

    private void onRandomClick() {
        urlString = "https://source.unsplash.com/random/" + resolution;
        refreshWallPaper();
    }

    private void onFeaturedClick() {
        urlString = "https://source.unsplash.com/featured/" + resolution;
        refreshWallPaper();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    public void loadImage() {


        Toast.makeText(this, "Loading WallPaper...", Toast.LENGTH_SHORT).show();

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();


// If you are using normal ImageView
        String url = "https://source.unsplash.com/category/nature/1080x1920";

        imageLoader.get(urlString, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    imageView.setImageBitmap(response.getBitmap());
                    imageBitmap = response.getBitmap();
                    setSpaceViewColor();
                    Toast.makeText(MainActivity.this, "Image loaded", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    isSaved = false;


                }
            }
        });
    }

    private void showAlert(String s, String s1) {
        Alerter.create(MainActivity.this)
                .setTitle(s)
                .setText(s1)
                .setDuration(3000)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .show();

        //                .setBackgroundColor(new ColorArt(imageBitmap).getBackgroundColor())


    }

    private void checkAdShown() {

        try {
            if (showAdCount > 4) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    showAdCount = 0;
                } else {

                }
                //Ads showing code here

            } else {
                showAdCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAsWallPaper() {

        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(imageBitmap);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Toast.makeText(this, "WallPaper set", Toast.LENGTH_SHORT).show();
        postjob(getTimePrefrenceValue());

    }


    private String saveToInternalStorage(Bitmap bitmapImage, String filename) {

        if(isStoragePermissionGranted()) {


            //get path to external storage (SD card)
            String iconsStoragePath = Environment.getExternalStorageDirectory() + "/"+getString(R.string.app_name) + "/MyWallPaper/";
            File sdIconStorageDir = new File(iconsStoragePath);

            //create storage directories, if they don't exist
            sdIconStorageDir.mkdirs();
            try {
                String filePath = sdIconStorageDir.toString() + File.separator + filename + System.currentTimeMillis() + ".png";
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

                //choose another format if PNG doesn't suit you
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, bos);

                bos.flush();
                bos.close();

            } catch (FileNotFoundException e) {
                Log.w("TAG", "Error saving image file: " + e.getMessage());
                return sdIconStorageDir.getPath();
            } catch (IOException e) {
                Log.w("TAG", "Error saving image file: " + e.getMessage());
                return sdIconStorageDir.getPath();
            }
            isSaved = true;
            showAlert("WallPaper Saved ",sdIconStorageDir.getAbsolutePath() );

            return sdIconStorageDir.getAbsolutePath();

        }else{
            return "";
        }



    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            saveToInternalStorage(imageBitmap, "Wall_");
        }
    }

    public int getNavBarHeight(Context c) {
        int result = 0;
        try {
            boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

            if (!hasMenuKey && !hasBackKey) {
                //The device has a navigation bar
                Resources resources = getResources();

                int orientation = getResources().getConfiguration().orientation;
                int resourceId;
                if (isTablet(c)) {
                    resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
                } else {
                    resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
                }

                if (resourceId > 0) {
                    return getResources().getDimensionPixelSize(resourceId);
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void scheduleWallChangeJob(int time) {

        try {
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
                    .setReplaceCurrent(true)
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showSplashScreen() {

        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            imageView.setImageDrawable(wallpaperDrawable);

            imageBitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();
            setSpaceViewColor();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void openDrawerButtonClick(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }
}
