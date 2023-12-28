package com.example.mukkersnoticesch;

import com.example.mukkersnoticesch.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class MukkersNoticeSchApplicationTests {

	@Test
	void contextLoads() {
		// 노원중
		// 1. 사전 페이지에 접속하여 쿠키 설정
		String url = "https://nowon.sen.ms.kr/10546/subMenu.do";
		String setCookieHeader = HttpUtils.sendHttpGetToCookies(url);

		// 2. 리스트 페이지에 요청
		String listPageUrl = "https://nowon.sen.ms.kr/dggb/module/board/selectBoardListAjax.do";
		String postData = "bbsId=BBSMSTR_000000002891&bbsTyCode=notice&customRecordCountPerPage=10&pageIndex=1";
		String listPageContent = HttpUtils.sendHttpPost(listPageUrl, postData, setCookieHeader);

		Document document = Jsoup.parse(listPageContent);
		Elements trElements = document.select("tr");
		List<Map<String, String>> trList = new ArrayList<>();

		for (Element trElement : trElements) {
			Map<String, String> rowData = new HashMap<>();
			Elements tdElements = trElement.select("td");

			for (int i = 0; i < tdElements.size(); i++) {
				rowData.put("column" + i, tdElements.get(i).text());
			}
			trList.add(rowData);
		}

		for (Map<String, String> rowData : trList) {
			System.out.println(rowData);
		}

    }

}
