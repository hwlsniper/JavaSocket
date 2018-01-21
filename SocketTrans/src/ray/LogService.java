package ray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LogService {
    public void save(String sourceid, File uploadFile){
    	String filename=uploadFile.getAbsolutePath();
    	try {  
            String data = sourceid;  
  
            File file = new File(filename+".log");  
            if (!file.exists())  
                file.createNewFile();  
            FileOutputStream out = new FileOutputStream(file, false);  
            StringBuffer sb = new StringBuffer();  
            // 写入的内容最后加上一个空格用于拆分成行  
            sb.append(data);  
            out.write(sb.toString().getBytes("utf-8"));  

            out.close();  
            
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }

    public void delete(File uploadFile){
    	String filename=uploadFile.getAbsolutePath();
    	File file = new File(filename+".log");
    	file.delete();
    }

    public String getBindId(File uploadFile){
    	String filename=uploadFile.getAbsolutePath();
    	System.out.println(filename);
    	try {
    		File file=new File(filename+".log");
    		if (!file.exists()) {  
                return null;
            } 
        	FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);
            String line="";
            while ((line=br.readLine())!=null) {
                return line.toString();
            }
            br.close();
            fr.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	return null;
    }
}
