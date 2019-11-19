package com.google.apis;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.jsp.jstl.sql.Result;

import com.google.api.client.googleapis.*;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Context;
import com.google.api.services.customsearch.model.Search;
import com.google.apis.SearchGoogle.Helper;
import com.google.common.cache.Cache;
import com.google.common.util.concurrent.Service.Listener;


public class SearchGoogle {

	 public class Helper {
		String s = "";
		StringBuilder sb = new StringBuilder();
		
		public String toString() {
			return s + sb.toString();
		}
		
	}
	 
	private static final int HTTP_REQUEST_TIMEOUT = 3 * 600000;
	
	public static List<com.google.api.services.customsearch.model.Result> search(String keyword){
		Customsearch customsearch= null;

		try {
			customsearch = new Customsearch(new NetHttpTransport(),new JacksonFactory(), new HttpRequestInitializer() {
				public void initialize(HttpRequest httpRequest) {
					try {
						// set connect and read timeouts
						httpRequest.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
						httpRequest.setReadTimeout(HTTP_REQUEST_TIMEOUT);
						httpRequest.getNumberOfRetries();

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<com.google.api.services.customsearch.model.Result> resultList=null;
		try {
			Customsearch.Cse.List list=customsearch.cse().list(keyword);
			list.setKey("AIzaSyD4x3-B_ZaKspr_0gLTfhLCXIyYkUD-J50");
			list.setCx("014097863273480938663:pmmxsyuflaa");
			Search results=list.execute();
			resultList=results.getItems();
		}
		catch (  Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	public class MyCache {
		
		public MyCache() {
			
		}

		public void writeCache(Helper hm) throws IOException {
			
			String fileName = "cache";
			int r = (int) (Math.random() * 10000 ) + 1;
			fileName += r + ".txt.cache";
			
			File cache = new File("/home/zx/cache/" + fileName);
			FileWriter fw = new FileWriter(cache);
			fw.write(hm.s + "\n");
			fw.write(hm.sb.toString());
			fw.close();
		}
		
		Helper[] meinCache;
		
		public Helper[] readCache() throws IOException {
			int anzahlFiles = new File("/home/zx/cache/").listFiles().length;
			meinCache = new Helper[anzahlFiles];
			int pos = 0;
			for ( File f :  new File("/home/zx/cache/").listFiles() ) {
				Helper h = new Helper();
				Scanner scan = new Scanner(f);
				scan.useDelimiter("\n");
				
				// helper fill search
				h.s = scan.nextLine() + "\n";
				String s = null;
				// helper fill result
				while (scan.hasNextLine() ) 
					h.sb.append(scan.nextLine()+ "\n");
				
				meinCache[pos++] = h;
			}
			
			return meinCache;
		}

	}

	public static void main(String[] args) throws Exception{

		
		MyCache mc = new SearchGoogle().new MyCache();
		Helper helpme = new SearchGoogle().new Helper();
		List<com.google.api.services.customsearch.model.Result> results = new ArrayList<>();
		
		try {

			helpme.s = getInputQuery();
			results = search(helpme.s);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(com.google.api.services.customsearch.model.Result result : results){
			System.out.println("\n" + result.getDisplayLink());
			System.out.println("\n" +result.getHtmlTitle());
			System.out.println("\n" +result.getFileFormat());
			System.out.println("\n" +result.getLink());
			System.out.println("\n" +result.getImage());
			System.out.println("\n" +result.getSnippet());
			System.out.println("\n" +result.getPagemap());
			// all attributes:
			System.out.println(result.toString());


			//	        fill result in helper
			helpme.sb.append(result.getLink() + "\n");
			

		}
		mc.writeCache(helpme);
		try {
			mc.readCache();
			
			for ( Helper h : mc.meinCache )
				System.out.println(h.toString());
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String neueSuche = getInputQuery();
		boolean habeGefunden = false;
		
		for ( Helper h : mc.meinCache ) 
			if ( h.s.startsWith(neueSuche)) {
				System.out.println("habe suche in cache: " + neueSuche);
				habeGefunden = true;
			}
		
		if ( habeGefunden == false )
			System.out.println("Suche " + neueSuche + " muss online nicht in Cache");
		
	}
	private static String getInputQuery() throws IOException {

	    String inputQuery = "";

	    System.out.print("Please enter a search term: ");
	    BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
	    inputQuery = bReader.readLine();
	    return inputQuery;
	}

}
		





