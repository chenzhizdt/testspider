package com.triman.spider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestPageProcessor implements PageProcessor {
	
	public static final String URL58 = "http://qy.58.com";

    private Site site = Site.me().setDomain("qy.58.com");

    public void process(Page page) {
    	String url = page.getUrl().toString();
    	if(url.equals(URL58)) {
    		List<String> links = page.getHtml().xpath("//div[@class='hotCityList']/a").links().all();
    		List<String> newLinks = new ArrayList<String>();
    		Iterator<String> it = links.iterator();
    		while(it.hasNext()) {
    			String link = it.next();
    			String newLink = link.replace(" ", "%20");
    			newLinks.add(newLink);
    		}
    		page.addTargetRequests(newLinks);
    	} else {
    		List<String> titles = page.getHtml().xpath("//div[@class='compList']/ul/li/span/a/text()").all();
    		if(!titles.isEmpty()) {
    			page.putField("titles", titles);
    			int pageNumber = this.pageNumber(url);
    			String requestString = null;
    			if(pageNumber == 1) {
    				requestString = url + "pn2/";
    			} else {
    				pageNumber++;
    				requestString = this.baseUrl(url) + pageNumber + "/";
    			}
    			page.addTargetRequest(requestString);
//    			System.out.println("Add a new request: " + requestString);
    		}
    	}
    }
    
    private int pageNumber(String url) {
    	Pattern p=Pattern.compile("pn(\\d{1,3})/$");
		Matcher m=p.matcher(url);
		if(m.find()){
			return Integer.parseInt(m.group(1));
		} else {
			return 1;
		}
    }
    
    private String baseUrl(String url) {
    	Pattern p=Pattern.compile("(.*pn)(\\d{1,3})/$");
		Matcher m=p.matcher(url);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	String path = null;
    	String url = null;
    	if (args.length == 0) {
    		path = "f://companys.txt";
    		url = URL58;
    	} else if (args.length == 1) {
    		url = args[0];
    		path = "f://companys.txt";
    		System.out.println(args[0]);
		} else {
    		url = args[0];
			path = args[1];
		}
        Spider.create(new TestPageProcessor()).addUrl(url)
             .addPipeline(new FileOutputPipeline(path)).run();
    }
}
