package com.compsci702project.compsci702project;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.FileObserver;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Created by Kyungmo on 4/8/2015.
 */
public class FileObserverIntentService extends IntentService {


    RecursiveFileObserver observer = new RecursiveFileObserver(android.os.Environment.getExternalStorageDirectory().toString(), 4095) { // set up a file observer to watch this directory on sd card

        //this method must be implemented in order to use FileObserver
        @Override
        public void onEvent(int event, String file) {

            if(event == FileObserver.ACCESS || event == FileObserver.DELETE || event == FileObserver.MODIFY || event == FileObserver.OPEN || event == FileObserver.CREATE){
                AccessHistory accessHistory = new AccessHistory();

                Date now = new Date();
                accessHistory.setAccessedAt(now);
                //set access type
                //set accessed file name
                //set accessed file type

                //Name of the app that accessed the file is yet unknown
                //TODO: save the access information as AccessHistory object and store it in db
            }


            if(event== FileObserver.ACCESS){
                Log.d(":::ACCESSED:::", file);
            }
            if(event== FileObserver.DELETE){
                Log.d(":::DELETED:::", file);
            }
            if(event== FileObserver.MODIFY){
                Log.d(":::MODIFIED:::", file);
            }
            if(event== FileObserver.OPEN){
                Log.d(":::CREATED:::", file);
            }

            if(event == FileObserver.CREATE){ // check if its a "create" and not equal to .probe because thats created every time camera is launched
                Log.d(":::CREATED:::", file);
            }

            //implement other events as well (modified, opened, etc)
            //http://developer.android.com/reference/android/os/FileObserver.html

        }
    };



    public FileObserverIntentService() {
        super("FileObserverIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("", "onHandleIntent()");
        observer.startWatching();
/*        synchronized (this)
        {
            startActivity(new Intent(this, com.compsci702project.compsci702project.AlertDialog.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }*/
    }
}
