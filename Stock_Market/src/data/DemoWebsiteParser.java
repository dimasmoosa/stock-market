package data;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DemoWebsiteParser {
	
	public static void main(String[] args) throws IOException{
		
		Document doc;
		doc = Jsoup.connect("http://google.com").get();
		//to get the list of all links from a website
		Elements links = doc.getElementsByTag("a");
		for(Element link: links) {
			String l = link.attr("href"); 
			if(l.length()>0) {
				if(l.length()<4) {
					l = doc.baseUri()+l.substring(1);
				}
				else if(!l.substring(0,4).equals("http"))
					l = doc.baseUri()+l.substring(1);					
			}
			System.out.println(l);
		}
	}

}
