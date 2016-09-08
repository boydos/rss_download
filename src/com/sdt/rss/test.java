package com.sdt.rss;

public class test {
	public static void main(String[] args) {
		String url ="http://news.baidu.com/n?cmd=1&amp;class=finannews&amp;tn=rss";
		
		System.out.println(url.replace("/", "_").replace("?", "_").replace("&", "_"));
	}
}
