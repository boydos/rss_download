package com.sdt.rss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.bigknow.minero.util.StringUtil;

public class FileHelper extends BaseCloseHelper {
	public static void save(String dirs,String content) {
		save(dirs, null,content);
	}
	public static boolean save(String dirs,String filename,String content) {
		File file = new File(dirs);
		BufferedWriter writer=null;
		boolean save =false;
        try {
	        if(!file.exists()) {
	        		file.mkdirs();
	        }
	        if(StringUtil.isEmpty(filename))  {
	        		filename=System.currentTimeMillis()+".temp";
	        }
	        	file = new File(dirs+"/"+filename);
	        
	        if(file.isFile()) {
	        	if(file.exists()) {
	        		file.delete();
	        	}
	        	file.createNewFile();
	        }
	      
	        writer=new BufferedWriter(new FileWriter(file));  
	        writer.write(content);  
	        writer.flush();
	        save=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
        finally  {
           closeResources(writer);
        }
        return save;
	}
	
	public static String readFile(String path) {
		File file = new File(path);
		if(!file.exists()) return null;
		BufferedReader reader=null;
		String str= "";
		try {
			 reader = new BufferedReader(new FileReader(file));
			 String line =reader.readLine();
			 while(line !=null) {
				 str+=line;
				 line =reader.readLine();
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeResources(reader);
		}
		return str;
	}
}
