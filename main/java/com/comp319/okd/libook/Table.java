package com.comp319.okd.libook;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by O. Kaan Demiröz on 21.4.2015.
 */
@ParseClassName("Tables")
public class Table  extends ParseObject{

    public int getFloor(){
        return getInt("Floor");
    }

    public void setFloor(int floor){
        put("Floor",floor);
    }

    public int getX(){
        return getInt("X");
    }

    public void setX(int x){
        put("X",x);
    }

    public int getY(){
        return getInt("Y");
    }

    public void setY(int y){
        put("Y",y);
    }

    public void setPhoto(File file){
        ParseFile parseFile = getParseFileFromFile(file);
        put("Photo",parseFile);
    }

    public File getPhoto(){
        ParseFile parseFile = getParseFile("Photo");
        return getFileFromParseFile(parseFile,0);
    }

    public void setAudio(File file){
        ParseFile parseFile = getParseFileFromFile(file);
        put("Audio",parseFile);
    }

    public File getAudio(){
        ParseFile parseFile = getParseFile("Audio");
        return getFileFromParseFile(parseFile,1);
    }

    public void setExpiresAt(Date date){
        put("expiresAt",date);
    }

    private ParseFile getParseFileFromFile(File file){
        String filename = file.getName();
        byte[] buffer = new byte[(int) file.length()];
        ByteArrayOutputStream byteArrayOutputStream = null;
        try{
            FileInputStream fileInputStream = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            while((read = fileInputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer,0,read);
            }
            byteArrayOutputStream.close();
            fileInputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        return new ParseFile(filename,byteArrayOutputStream.toByteArray());
    }

    private File getFileFromParseFile(ParseFile parseFile, int type){
        if(parseFile == null) return null;
        File file = null;
        String suffix = parseFile.getName().substring(parseFile.getName().indexOf('.'));
        try{
            byte[] data = parseFile.getData();
            file = File.createTempFile("temp_file_"+type+"-", suffix);
            file.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
            fileOutputStream.close();
        }catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
