package com.github.cyl.weixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WeixinCrawlerTest {

	private static final String AJAX_DOMAIN = "http://weixin.sogou.com/gzhjs";
	private static final Pattern PARAM_STR = Pattern.compile("\\?(.+?)\\'");
	private static final Pattern JSON_CONTENT = Pattern.compile("\\{(.+?)}");

	public static void main(String[] args) throws Exception {
		Document document = Jsoup.connect("http://weixin.sogou.com/weixin").data("type", "1").data("ie", "utf-8")
				.data("query", "大数据").get();
		String attr = document.getElementsByTag("div").attr("onclick");
		System.out.println(attr);
		Matcher matcher = PARAM_STR.matcher(attr);
		Map<String, String> map = new HashMap<String, String>();
		if (matcher.find()) {
			attr = matcher.group(1);
		}
		String[] pairs = attr.split("&");
		for (String pair : pairs) {
			String[] split = pair.split("=");
			map.put(split[0], split[1]);
		}
		Response response = Jsoup.connect(AJAX_DOMAIN).data(map).data("cb", "sogou.weixin.gzhcb").data("page", "1")
				.data("t", String.valueOf(System.currentTimeMillis())).ignoreContentType(true).execute();
		String body = response.body();
		Matcher matcher2 = JSON_CONTENT.matcher(body);
		String json = "";
		if (matcher2.find()) {
			json = matcher2.group();
		}
		ObjectMapper mapper = new ObjectMapper();
		Map dataMap = mapper.readValue(json, Map.class);
		List<String> list = (List<String>) dataMap.get("items");
		for (String string : list) {
			System.out.println(string);
		}
	}

}
