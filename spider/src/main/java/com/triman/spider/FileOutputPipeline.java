package com.triman.spider;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class FileOutputPipeline implements Pipeline{
	
	private String path;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FileOutputPipeline(String path) {
		this.path = path;
	}

	public synchronized void process(ResultItems resultItems, Task task) {
		try {
			 FileWriter fw = new FileWriter(path,true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 List<String> titles = resultItems.get("titles");
			 if(titles != null) {
				 Iterator<String> it = titles.iterator();
				 while(it.hasNext()) {
					 String title = it.next();
//					 System.out.println(title);
					 bw.append(title + "\n");
				 }
			 }
			 bw.flush();
			 bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
