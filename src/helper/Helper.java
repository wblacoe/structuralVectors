package helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Helper {

    //I/O
    public synchronized static BufferedReader getFileReader(File file) throws IOException{
        return
            file.getName().endsWith(".gz") ?
            new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)))) :
            new BufferedReader(new FileReader(file));
    }
    public synchronized static BufferedReader getFileReader(String fileName) throws IOException{
        return getFileReader(new File(fileName));
    }
    
    public synchronized static BufferedWriter getFileWriter(File file) throws IOException{
        ensureContainingFolderExists(file);
        return
            file.getName().endsWith(".gz") ?
            new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)), "UTF-8")) :
            new BufferedWriter(new FileWriter(file));
    }
    public synchronized static BufferedWriter getFileWriter(String fileName) throws IOException{
        return getFileWriter(new File(fileName));
    }

    public synchronized static void ensureContainingFolderExists(File file){
        if(file.getParentFile() != null && !file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        } 
    }    
    
    public synchronized static HashSet<String> getLinesFrom(File f) throws IOException{
        HashSet<String> r = new HashSet<>();
        
        BufferedReader in = getFileReader(f);
        String line;
        while((line = in.readLine()) != null){
            r.add(line);
        }
        
        in.close();
        
        return r;
    }
    
}
