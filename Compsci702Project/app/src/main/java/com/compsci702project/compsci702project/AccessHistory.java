package com.compsci702project.compsci702project;

import java.util.Date;

/**
 * Created by Kyungmo on 4/26/2015.
 */
public class AccessHistory {


    private Date AccessedAt;
    public void setAccessedAt(Date accessedAt){
        this.AccessedAt = accessedAt;
    }
    public Date getAccessedAt(){
        return this.AccessedAt;
    }


    private String AppName;
    public void setAppName(String appName){
        this.AppName = appName;
    }
    public String getAppName(){
        return this.AppName;
    }


    private String AccessedFileName;
    public void setAccessedFileName(String accessedFileName){
        this.AccessedFileName = accessedFileName;
    }
    public String getAccessedFileName(){
        return this.AccessedFileName;
    }


    private String AccessedFilePath;

    public void setAccessedFilePath(String accessedFilePath){
        this.AccessedFilePath = accessedFilePath;
    }
    public String GetAccessedFilePath(){
        return this.AccessedFilePath;
    }



    private String AccessedFileExtension;
    public void setAccessedFileExtension(String accessedFileExtension) {
        AccessedFileExtension = accessedFileExtension;
    }
    public String getAccessedFileExtension(){
        return this.AccessedFileExtension;
    }




    private String AccessType;
    public void setAccessType(String accessType){
        this.AccessType = accessType;
    }



}
