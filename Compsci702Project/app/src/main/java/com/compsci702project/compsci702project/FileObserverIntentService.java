package com.compsci702project.compsci702project;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.FileObserver;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
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

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                //set access type
                String accessType = "";


                if(event== FileObserver.ACCESS){
                    accessType = "Accesss";
                }
                if(event== FileObserver.DELETE){
                    accessType = "Delete";
                }
                if(event== FileObserver.MODIFY){
                    accessType = "Modify";
                }
                if(event== FileObserver.OPEN){
                    accessType = "Open";
                }

                if(event == FileObserver.CREATE){ // check if its a "create" and not equal to .probe because thats created every time camera is launched
                    accessType = "Create";
                }

                //set accessed file name
                String accessedFileName = file;

                //set accessed file type
                String accessedFileType = file.substring(file.lastIndexOf('.') + 1);

                String accessedDate = currentDateandTime;

                //save to file
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File (root.getAbsolutePath() + "/se702");
                if(!dir.exists()){
                    dir.mkdirs();
                }

                File file_fileObserver = new File(dir, "fileAccessMonitor.txt");
                if(!file_fileObserver.exists()){
                    try {
                        file_fileObserver.createNewFile();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                FileOutputStream f = null;
                try {
                    f = new FileOutputStream(file_fileObserver, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                OutputStreamWriter myOutWriter = new OutputStreamWriter(f);

                try {
                    myOutWriter.append("Date: " + currentDateandTime + "\n" +
                                    "Access type: " + accessType + "\n" +
                                    "File path: " + accessedFileName + "\n" +
                                    "File type: " + accessedFileType + "\n\n"
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    myOutWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    };



    public FileObserverIntentService() {
        super("FileObserverIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("", "onHandleIntent()");
        observer.startWatching();
    }
}
