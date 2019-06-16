package ir.viratech.commons.code_gen.test;

import ir.viratech.commons.code_gen.model.resource.ResourceManager;
import ir.viratech.commons.code_gen.model.util.HSUtil;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class TestGetAllResources {
	public static void main(String[] args) throws URISyntaxException, MalformedURLException {
		for (URL url : ResourceManager.getAllResources(ir.viratech.commons.model.order.OrderEntry.class, "..///")) {
			System.out.println(url.toString());
			System.out.println(HSUtil.getUrlFileName(url));
		}
		
		for (String str : new String[] {"file:ir/viratech/test.class","file:ir/viratech/","file:test.class","file:test","file:ir/viratech/test","jar:file:ir/viratech/test2.jar!/ir/viratech/text.class"})
		{ 
			System.out.println("URL: " + str);
			System.out.println("Filename: " + HSUtil.getUrlFileName(new URL(str)));
			System.out.println("");
		}
	}
}
