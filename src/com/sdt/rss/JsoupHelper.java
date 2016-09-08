package com.sdt.rss;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupHelper extends BaseCloseHelper {
	public static String getQQ163XHBDBody(String url) {
		Element element=null;
		Document document = getDocument(url);
		if(document==null) return "";
		element=document.getElementById("ctrlfscont");
		if(element==null) {
			element = document.getElementById("Cnt-Main-Article-QQ");
		}
		if(element==null) {
			element = document.getElementById("videoArea");
		}
		if(element==null) {
			element = document.getElementById("endText");
		}
		return element==null?"":element.toString();
	}

	private static Document getDocument(String url) {
		Document document=null;
		try {
			document= Jsoup.connect(url).timeout(9000).get();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.print(String.format("[无法连接到页面]"));
		}
		return document;
	}
	
}
