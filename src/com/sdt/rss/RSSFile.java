package com.sdt.rss;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.bigknow.minero.model.ModelBean;
import com.bigknow.minero.util.StringUtil;

public class RSSFile {
	public static void main(String[] args) {
		CommandLineParser parser = new BasicParser();
		Options options= new Options();
		options.addOption("h","help",false,"RSSFile Usage:");
		options.addOption("f","file",true,"RSS XML File");
		options.addOption("d","directory",true,"Directory To Save RSS XML File");
		CommandLine commandline =null;
		try {
			commandline = parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exit("命令异常");
		}
		if(commandline ==null) exit("未识别到命令...");
		String path="";
		if(!commandline.hasOption("f")&&!commandline.hasOption("d")) {
			printUsage();
		}
		if(commandline.hasOption("f")) {
			path = commandline.getOptionValue("f");
			downloadWithFile(path);
		}
		if(commandline.hasOption("d")) {
			path = commandline.getOptionValue("d");
			if(StringUtil.isEmpty(path)) {
				exit("目录参数为空");
			}
			File file = new File(path);
			if(!file.exists()||!file.isDirectory()) {
				exit(String.format("...[%s]不是文件夹或者不存在...",path));
			}
			downloadWidthDirectory(file);
		}
		
	}
	public static void printUsage() {
		exit("Usage:\n"
				+ "  -f file RSS文件\n"
				+ "  -d directory 已下载好的RSS文件所存放的目录\n"
				+ "  欢迎使用RSS内容下载器,目前支持[新华网/百度/163/QQ]新闻源");
	}
	public static void exit(String msg) {
		System.out.println(msg);
		System.exit(0);
	}
	public static void downloadWidthDirectory(File file) {
		if(!file.exists()) {
			System.out.println(String.format("...[%s]文件不存在...", file.getAbsolutePath()));
			return;
		}
		if(file.isDirectory()) {
			for(File f:file.listFiles()) {
				downloadWidthDirectory(f);
			}
		}else if(file.isFile()) {
			downloadWithFile(file.getAbsolutePath());
		}
		
	}
	
	public static void downloadWithFile(String path) {
		if(StringUtil.isEmpty(path)) {
			System.out.println("...文件路径为空...");
			return;
		}
		File file = new File(path);
		if(!file.exists()) {
			System.out.println(String.format("...[%s]文件不存在...",path));
			return;
		}
		String xmlContent = FileHelper.readFile(path);
		downloadBody(file.getParent(), file.getName(), xmlContent);
	}
	
	public static void downloadBody(String directory,String saveName,String xmlContent) {
		if(StringUtil.isEmpty(xmlContent)){
			System.out.println("没有获得任何内容");
			return;
		}
		
		ModelBean model =new ModelBean();
		model.fromXML(xmlContent);
		
		ModelBean rss = model.getModel("rss");
		if(rss==null) {
			System.out.println("非RSS源文件");
			return;
		}
		ModelBean channel = rss.getModel("channel");
		if(channel==null) {
			System.out.println("非标准RSS源文件");
			return;
		}
		System.out.println(String.format("[当前频道是---%s]", channel.getString("title")));
		
		List<ModelBean> items = channel.getList("item");
		
		if(items==null || items.size() ==0) {
			System.out.println("无新闻内容,请更换RSS源文件");
			return;
		}
		
		for(ModelBean item:items) {
			String link = item.getString("link");
			System.out.print(String.format("[正在处理 :%s]...", link));
			String content = JsoupHelper.getQQ163XHBDBody(link);
			if(!StringUtil.isEmpty(content)) {
				item.setString("body", content);
			} else System.out.print(String.format("[没有内容--%s]", directory));
			System.out.println(String.format("..[处理结束]"));
		}
		
		System.out.println(String.format("[文件保存目录:%s， 文件名:%s]", directory,saveName));
		boolean save=FileHelper.save(directory, saveName, rss.toXml("rss"));
		System.out.println(String.format("[....保存%s....]", save?"成功":"失败"));
	}
}
