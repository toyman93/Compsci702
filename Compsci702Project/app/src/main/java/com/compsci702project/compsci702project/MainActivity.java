package com.compsci702project.compsci702project;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.graphics.drawable.Drawable;
import android.os.FileObserver;
//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
//import com.vogella.java.library.gson;



public class MainActivity extends Activity {

    Context cont;
    private ContextWrapper _context = new ContextWrapper(cont);
    private static boolean isFileObserverMonitoring = false;


    // Initialize activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view as res.layout.activity_main.xml
        setContentView(R.layout.activity_main);


        //SCAN AND SAVE AS JSON ====================================================================================
        // Create button, attach it to button with id buttontest
        final Button button = (Button) findViewById(R.id.buttonScanCurrentOpenFiles);
        // Button listener
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent broadcastR = new Intent(MainActivity.this, BroadcastReceiverIntentService.class);
                //startService(broadcastR);
                String output = ExecutelsofCommand("json");
                Log.d("", output);
            }
        });




        //SCAN AND SAVE AS HUMAN READABLE TEXT ====================================================================================
        final Button buttonSaveAsReadableText = (Button) findViewById(R.id.buttonScanAndSaveAsText);
        // Button listener
        buttonSaveAsReadableText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String output = ExecutelsofCommand("text");
                Log.d("", output);
            }
        });




        //VIEW ACCESS HISTORY ====================================================================================
        final Button button_view = (Button) findViewById(R.id.buttonViewAccessHistory);
        // Button listener
        button_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("", "view");
                TextView accessHistoryTextView = (TextView)findViewById(R.id.accessHistoryTextView);
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File (root.getAbsolutePath() + "/se702");
                if(!dir.exists()){
                    dir.mkdirs();
                }

                File file = new File(dir, "accessHistoryLog_text.txt");
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                        StringBuilder text = new StringBuilder();

                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String line;
                            while ((line = br.readLine()) != null) {
                                text.append(line);
                                text.append('\n');
                            }

                        accessHistoryTextView.setText(text);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        });



        //SCAN AND SAVE AS HUMAN READABLE TEXT ====================================================================================
        final Button buttonStartWatch = (Button) findViewById(R.id.buttonStartWatch);
        // Button listener
        buttonStartWatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent fileObserverIntentService = new Intent(MainActivity.this, FileObserverIntentService.class);

                if(isFileObserverMonitoring == false){
                    startService(fileObserverIntentService);
                    isFileObserverMonitoring = true;
                    buttonStartWatch.setText("Tap to stop");
                }
                else{
                    stopService(fileObserverIntentService);
                    isFileObserverMonitoring = false;
                    buttonStartWatch.setText("Start monitoring (File observer)");
                }
            }
        });


    }


    public String ExecutelsofCommand(String saveMode){
        String out = new String();
        try {
            //Process p = Runtime.getRuntime().exec("/system/bin/sh");
            //Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "lsof"});
            //Process p = Runtime.getRuntime().exec(new String[]{"su", "-s"});
            //Process p = Runtime.getRuntime().exec("sh");
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream stdin = new DataOutputStream(p.getOutputStream());
            //stdin.writeBytes("ls -l  /proc/*/fd echo $?\n"); // \n executes the command
            //stdin.writeBytes("su\n"); // \n executes the command
            //stdin.writeBytes("su --shell\n"); // \n executes the
            stdin.writeBytes("lsof /sdcard\n"); // \n executes the
            stdin.flush();
            //stdin.writeBytes("/proc/*/fd\n");
            InputStream stdout = p.getInputStream();

            byte[] buffer = new byte[5000];
            int read;
            //read method will wait forever if there is nothing in the stream
            // so we need to read it in another way than while((read=stdout.read(buffer))>0)
            while(true){
                read = stdout.read(buffer);
                out += new String(buffer, 0, read);
                if(read<5000){
                    //we have read everything
                    break;
                }
            }

            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/se702");
            if(!dir.exists()){
                dir.mkdirs();
            }

            File file_json = new File(dir, "accessHistoryLog_json.txt");
            if(!file_json.exists()){
                file_json.createNewFile();
            }

            File file_text = new File(dir, "accessHistoryLog_text.txt");
            if(!file_text.exists()){
                file_text.createNewFile();
            }


            FileOutputStream f_json = new FileOutputStream(file_json, true);
            OutputStreamWriter myOutWriter_json = new OutputStreamWriter(f_json);

            FileOutputStream f_text = new FileOutputStream(file_text, true);
            OutputStreamWriter myOutWriter_text = new OutputStreamWriter(f_text);

            Scanner scanner = new Scanner(out);
            int lineNo = 1;
            while (scanner.hasNextLine()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                String line = scanner.nextLine();
                if(lineNo ==1){
                    //skipping first line because they're just headers
                    line = scanner.nextLine();
                    lineNo++;
                }
                // process the line
                String[] splited = line.split("\\s+");

                int arrayLength = splited.length;
                String filePath = splited[8];
                for(int i = 1 ; i+8 <= arrayLength-1 ; i++){
                    filePath = filePath + splited[8+i];
                }

                String fileExtension = filePath.substring(filePath.lastIndexOf('.') + 1);
                if(fileExtension.length() > 4){
                    fileExtension = "none";
                }


                //JSON ================================================================================================
                if(saveMode == "json"){
                    try {
                        JSONObject jsonObject= new JSONObject();
                        try {
                            jsonObject.put("date", currentDateandTime);
                            jsonObject.put("process", getAppName(Integer.parseInt(splited[1])));
                            jsonObject.put("process", getPackageManager().getNameForUid(Integer.parseInt(splited[2])));
                            jsonObject.put("command", splited[0]);
                            jsonObject.put("pid", splited[1]);
                            jsonObject.put("uid", splited[2]);
                            jsonObject.put("filesize", splited[6]);
                            jsonObject.put("node", splited[7]);
                            jsonObject.put("filePath", filePath);
                            jsonObject.put("fileExtension", fileExtension);


                            jsonObject.put("isHumanAccess", true);

                            myOutWriter_json.append(jsonObject.toString());
                            myOutWriter_json.append("\n");
                            myOutWriter_json.append("#");
                            myOutWriter_json.append("\n");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("", "******* File not found. Did you" +  " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                //HUMAN READABLE TEXT ================================================================================================
                if(saveMode == "text"){
                    myOutWriter_text.append("Date: " + currentDateandTime + "\n" +
                            "Process: " + getAppName(Integer.parseInt(splited[1])) + "\n" +
                            "App: " + getPackageManager().getNameForUid(Integer.parseInt(splited[2])) + "\n" +
                            "Command: " + splited[0] + "\n" +
                            "PID: " + splited[1] + "\n" +
                            "UID: " + splited[2] + "\n" +
                            "File path: " + filePath + "\n" +
                            "File type: " + fileExtension + "\n" +
                            "File size: " + splited[6] + "\n" +
                            "Node: " + splited[7] + "\n" +
                            "Was it human access?: " + "no" + "\n\n"
                    );

                }

                lineNo++;
            }
            scanner.close();
            myOutWriter_json.close();
            myOutWriter_text.close();
            f_json.close();
            f_text.close();


            Log.d("lsof:", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;

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



    private String getAppName(int pID)
    {
        String processName = "";
        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while(i.hasNext())
        {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
            try
            {
                if(info.pid == pID)
                {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    //Log.d("Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
                    //processName = c.toString();
                    processName = info.processName;
                }
            }
            catch(Exception e)
            {
                //Log.d("Process", "Error>> :"+ e.toString());
            }
        }

        return processName;
    }

    public void getCurrentApp(View v) {
        Log.d("Message2", "Button2 Clicked");

        //Run every X Seconds to check current opened activity
        h.postDelayed(new Runnable(){
            public void run(){
                ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

                List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
                String currentTask = tasks.get(0).processName + "";
                Log.d("CurrentTask : ", currentTask);

                h.postDelayed(this, delay);
            }
        }, delay);
    }
}
