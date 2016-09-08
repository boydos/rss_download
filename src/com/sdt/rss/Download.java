package com.sdt.rss;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpEntity;

import com.bigknow.minero.model.ModelBean;
import com.bigknow.minero.util.StringUtil;

public class Download {
	public static String DEFAULT_CHARSET="gb2312";
	public static void main(String[] args) {
		CommandLineParser parser = new BasicParser();
		Options options= new Options();
		options.addOption("h","help",false,"DownLoad Usage:");
		options.addOption("f","file",true,"XML Config File For Download");
		options.addOption("u","url",true,"RSS XML URL For Download");
		options.addOption("c","charset",true,"Download Charset");
		options.addOption("d","directory",true,"Directory To Save Download File");
		CommandLine commandline =null;
		try {
			commandline = parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exit("命令异常");
		}
		if(commandline ==null) exit("未识别到命令...");
		if(commandline.hasOption("c")) {
			
			DEFAULT_CHARSET =commandline.getOptionValue("c");
		}
		if(commandline.hasOption("h")) {
			printUsage();
		}else if(!commandline.hasOption("f")&&!commandline.hasOption("u")&&!commandline.hasOption("d")) {
			printUsage();
		}else if(commandline.hasOption("d")&&commandline.hasOption("f")) {
			String file = commandline.getOptionValue("f");
			String defaultPath = commandline.getOptionValue("d");
			downloadWithConfig(file, defaultPath);
			
		}else if(commandline.hasOption("d")&&commandline.hasOption("u")) {
			String directory =commandline.getOptionValue("d");
			String url =commandline.getOptionValue("u");
			download(url, directory);
			
		}else if(commandline.hasOption("f")) {
			String file = commandline.getOptionValue("f");
			downloadWithConfig(file);
			
		}else if(commandline.hasOption("u")) {
			exit("请输入文件保存路径[-d]");
		}
		
	}
	public static void printUsage() {
		exit("Usage:\n"
				+ "  -f file RSS文件批量下载配置文件,XML格式\n"
				+ "  -u url 所要下载的RSS文件路径,和-d命令一起配合使用\n"
				+ "  -d directory 下载文件保存路径,可以配合-f以及-u命令使用\n"
				+ "  -c charset 下载文件时设置内容请求编码，默认无\n"
				+ "  欢迎使用RSS　DOWNLOAD 目前支持163 和QQ新闻源");
	}
	public static void exit(String msg) {
		System.out.println(msg);
		System.exit(0);
	}
	public static void downloadWithConfig(String filePath) {
		downloadWithConfig(filePath,null);
	}
	public static void downloadWithConfig(String filePath,String defaultPath) {
		String xmlString = FileHelper.readFile(filePath);
		if(StringUtil.isEmpty(xmlString)) {
			System.out.println("配置文件为空...");
			return;
		}
		ModelBean config = new ModelBean();
		config.fromXML(xmlString);
		ModelBean download = config.getModel("download");
		if(download ==null) {
			System.out.println("配置文件格式不正确");
			return;
		}
		if(StringUtil.isEmpty(defaultPath)) {
			defaultPath = download.getString("dir");
		}
		if(StringUtil.isEmpty(defaultPath)) {
			System.out.println("下载目录为空...");
			return;
		}
		List<ModelBean> members=download.getList("member");
		if(members==null|| members.isEmpty()) {
			System.out.println("无下载内容");
			return;
		}
		
		String memberDir;
		int i=0;
		for(ModelBean member:members) {
			 i++;
			 memberDir = member.getString("dir");
			 if(StringUtil.isEmpty(memberDir)) {
				 System.out.println(String.format("...第%d个成员无子路径,将保存到父级目录[%s]...",i,defaultPath));
				 memberDir=defaultPath;
			 } else {
				 memberDir=defaultPath+"/"+memberDir;
			 }
			 List<String> urlList=new ArrayList<String>();
			 try {
				 List<String> tempList = member.getStringList("url");
				 if(tempList!=null && !tempList.isEmpty()) {
					 urlList.addAll(tempList);
				 }
			 }catch(Exception e) {
				 try {
				 String url = member.getString("url");
				 urlList.add(url);
				 }catch(Exception ex) {
					 
				 }
				 
			 }
			 if(urlList==null|| urlList.isEmpty()) {
				 System.out.println(String.format("...第%d个成员,无下载内容,保存目录为[%s]...",i,memberDir));
				 continue;
			}
			long time = System.currentTimeMillis();
			for(String url:urlList) {
				download(url, memberDir);
			}
			long distance = System.currentTimeMillis()- time;
			distance = distance/1000;
			String costTime ="";
			if(distance<60) {
				costTime =distance+"S";
			} else if(distance<60*60) {
				costTime =(int)(distance/60)+"M"+distance%60+"S";
			} else {
				long min =distance%3600;
				costTime =(int)(distance/3600)+"H"+(int)(min/60)+"M"+min%60+"S";
			}
			
			System.out.println(String.format("下载完成,耗时%s", costTime));
		}
	}

	public static void download(String url,String directory) {
		if(StringUtil.isEmpty(url)){
			System.out.println("URL 为空");
			return;
		}
		System.out.println(String.format("[开始处理URL:%s,保存路径:%s]", url,directory));
		if(StringUtil.isEmpty(directory)){
			System.out.println("保存路径为空...");
			return;
		}
		String xmlContent = HttpClientHelper.get(url,DEFAULT_CHARSET);
		String[] temps = url.split("/");
		String filename =url.toLowerCase();
		if(filename.toLowerCase().startsWith("http:")) {
			filename =filename.substring(5);
		}
		if(temps !=null&& temps.length>0) {
			filename =temps[temps.length-1];
		}
		filename=filename.replace("/", "_").replace("?", "_").replace("&", "_");
		if(!filename.toLowerCase().endsWith(".xml")) {
			filename+=".xml";
		}
		
		RSSFile.downloadBody(directory, filename, xmlContent);
		
	}
	
}
