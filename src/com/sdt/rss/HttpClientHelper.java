package com.sdt.rss;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.bigknow.minero.util.StringUtil;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.sun.xml.internal.ws.util.StringUtils;

public class HttpClientHelper extends BaseCloseHelper {
	public static String post(String url, NameValuePair ...paras) {
		return post(url,null,paras);
	}
	public static String post(String url,String charset, NameValuePair ...paras) {
		if(StringUtil.isEmpty(url)) return "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if(paras!=null) {
			for(NameValuePair nvp:paras) {
				formparams.add(nvp);
			}
		}
		UrlEncodedFormEntity uefEntity;
		CloseableHttpResponse response=null;
		try {
			if(charset==null) {
				uefEntity=new UrlEncodedFormEntity(formparams);
			} else {
				uefEntity=new UrlEncodedFormEntity(formparams,charset);
			}
			httppost.setEntity(uefEntity);
			response = httpclient.execute(httppost);
			HttpEntity result= response.getEntity();
			if(result!=null) {
				if(charset==null) {
					return EntityUtils.toString(result);
				} else {
					return EntityUtils.toString(result,charset);
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			closeResources(response,httpclient);
		}
		return "";
	}
	
	public static String get(String url) {
		return get(url,null);
	}
	public static String get(String url,String charset) {
		if(StringUtil.isEmpty(url)) return null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget= new HttpGet(url);
		CloseableHttpResponse response=null;
		try {
			response = httpclient.execute(httpget);
			HttpEntity result= response.getEntity();
			if(StringUtil.isEmpty(charset)) {
				charset = getContentCharSet(result);
			}
			System.out.println("charset="+charset);
			return formatString(result,charset);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeResources(response,httpclient);
		}
		return null;
	}

	public static String formatString(HttpEntity entity) {
		return formatString(entity,null);
	}
	public static String formatString(HttpEntity entity,String charset) {
			try {
				if(StringUtil.isEmpty(charset)) {
					return entity!=null? EntityUtils.toString(entity):""; 
				}
				return entity!=null? EntityUtils.toString(entity,charset):"";
			} catch (org.apache.http.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
	}
	
	 public static String getContentCharSet(final HttpEntity entity)   
		        throws ParseException {   
		   
		        if (entity == null) {   
		            throw new IllegalArgumentException("HTTP entity may not be null");   
		        }   
		        String charset = null;   
		        if (entity.getContentType() != null) {    
		            HeaderElement values[] = entity.getContentType().getElements();   
		            if (values.length > 0) {   
		                NameValuePair param = values[0].getParameterByName("charset" );   
		                if (param != null) {   
		                    charset = param.getValue();   
		                }   
		            }   
		        }   
		         
		        if(StringUtil.isEmpty(charset)){  
		            charset = "UTF-8";  
		        }  
		        return charset;   
   }



}
