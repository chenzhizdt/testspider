package com.triman.spider;

import java.util.ArrayList;
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
    
    private int countUnnormal = 0;

	public void process(Page page) {
    	String url = page.getUrl().toString();
    	if(url.equals(URL58)) {
    		List<String> links = page.getHtml().xpath("//div[@class='hotCityList']/a").links().all();
    		page.addTargetRequests(links);
    	} else if(isCompanyPage(url)) {
    		List<String> titles = page.getHtml().xpath("//div[@class='compT']/h1/a/text()").all();
    		page.putField("titles", titles);
    	} else {
    		List<String> titles = page.getHtml().xpath("//div[@class='compList']/ul/li/span/a/text()").all();
    		List<String> links = page.getHtml().xpath("//div[@class='compList']/ul/li/span/a/@href").all();
    		List<String> normalTitles = new ArrayList<String>();
    		List<String> requestLinks = new ArrayList<String>();
    		if(!titles.isEmpty()){
    			for(int i = 0; i < titles.size(); i++){
    				String title = titles.get(i);
    				if(!title.endsWith("...")){
    					normalTitles.add(title);
    				} else {
//    					System.out.println("Unnormal Title: " + title);
//    					System.out.println("Unnormal Link: " + links.get(i));
    					requestLinks.add(links.get(i));
    					synchronized (this) {
							this.countUnnormal++;
							System.out.println("Unnormal Count: " + this.countUnnormal);
						}
    				}
    			}
    			page.putField("titles", normalTitles);
    			if(!requestLinks.isEmpty()){
    				page.addTargetRequests(requestLinks);
    			}
    			
    			int pageNumber = this.pageNumber(url);
    			String requestString = null;
    			if(pageNumber == 1) {
    				requestString = url + "pn2/";
    			} else {
    				pageNumber++;
    				requestString = this.baseUrl(url) + pageNumber + "/";
    			}
    			page.addTargetRequest(requestString);
    		}
    	}
    }
    
    private boolean isCompanyPage(String url){
    	Pattern p = Pattern.compile("http://qy.58.com/\\d+/$");
    	Matcher m = p.matcher(url);
    	if(m.find()){
    		return true;
    	} else {
    		return false;
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
    		path = "d://companys.txt";
    		url = URL58;
    	} else if (args.length == 1) {
    		url = args[0];
    		path = "d://companys.txt";
    		System.out.println(args[0]);
		} else {
    		url = args[0];
			path = args[1];
		}
        Spider.create(new TestPageProcessor()).addUrl(url)
             .addPipeline(new FileOutputPipeline(path)).thread(20).run();
    }
}
