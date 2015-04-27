package com.compsci702project.compsci702project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.graphics.drawable.Drawable;
import android.os.FileObserver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    Context cont;
    private ContextWrapper _context = new ContextWrapper(cont);

    // Initialize activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view as res.layout.activity_main.xml
        setContentView(R.layout.activity_main);

        // Create button, attach it to button with id buttontest
        final Button button = (Button) findViewById(R.id.buttontest);

        // Button listener
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent fileObserverIntentService = new Intent(MainActivity.this, FileObserverIntentService.class);

                startService(fileObserverIntentService);
                Log.d("", "clicked");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    //Method which shows an example of using package manager to get all installed packages on this device
    //Potentially useful, but more importantly, look at this if you are not sure how to use package manager.
    //Code is pretty self explanatory
    public void listAllActivities() throws PackageManager.NameNotFoundException
    {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for(PackageInfo pack : packages)
        {
            ActivityInfo[] activityInfo = getPackageManager().getPackageInfo(pack.packageName, PackageManager.GET_ACTIVITIES).activities;
            Log.i("1", pack.packageName + " has total " + ((activityInfo==null)?0:activityInfo.length) + " activities");
            if(activityInfo!=null)
            {
                for(int i=0; i<activityInfo.length; i++)
                {
                    Log.i("2", pack.packageName + " ::: " + activityInfo[i].name);
                }
            }
        }
    }

    //--Charindu --
    //This method gets the list of all apps installed in the device
    //and logs attributes of the apps
    //Additionally logs permissions of those apps in a separate log -Doesn't work for now.
    public void getApplicationList() throws PackageManager.NameNotFoundException {
        String tag1 = "AppList";//Tag string for the non system appList log
        //String tag2 = "sysAppList";//Tag string for the system appList log
        //String tag3 = "appPermissions"; //Tag string for the appPermissions log
        String appName = " ";
        String pName = " ";
        String versionName = " ";
        String appPermissions = " ";
        int versionCode = 0;
        Drawable icon;
        //gets the list of applications installed on device
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            /*if (p.versionName == null)
            {
                //logs the non system app list
                Log.i(tag2,p.applicationInfo.loadLabel(getPackageManager()).toString()
                        + "\t" + p.packageName + "\t");
                continue ;
            }*/
            //assign the app info into corresponding variables
            appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
            pName = p.packageName;
            versionName = p.versionName;
            versionCode = p.versionCode;
            icon = p.applicationInfo.loadIcon(getPackageManager());

            //appPermissions = p.permissions.toString();
            //logs the non system app list
            Log.v(tag1, appName + "\t" + pName + "\t" + versionName + "\t" + versionCode);

            //logs the permission list
            //Log.v(tag3,appPermissions);


        }
    }

    public void getAppPermissions() throws PackageManager.NameNotFoundException
    {
        //String tag2 = "sysAppList";//Tag string for the system appList log
        //String tag3 = "appPermissions"; //Tag string for the appPermissions log

        String appName = " ";
        String appPermissions = " ";

        // Get the object of packageManager
        PackageManager pm = getPackageManager();

        // Obtain list of installed applications
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        //list
        String tag = "AppPermissions";//Tag string for the non system appList log
        for (ApplicationInfo applicationInfo : packages) {
            // Print the name of the application
            Log.d("--------Application"," : " + applicationInfo.packageName +"----");

            // Get permission of current application
            try {
                // Get the overall information of the package
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if(requestedPermissions != null) {
                    // Print the permissions of the package
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        Log.d(tag, requestedPermissions[i]);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }







}
