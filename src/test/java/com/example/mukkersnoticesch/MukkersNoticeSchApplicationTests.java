package com.example.mukkersnoticesch;

import com.example.mukkersnoticesch.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class MukkersNoticeSchApplicationTests {

	@Test
	void contextLoads() {
		// 1. 사전 페이지에 접속하여 쿠키 설정
		String url = "https://nowon.sen.ms.kr/10546/subMenu.do";
		String setCookieHeader = HttpUtils.sendHttpGetToCookies(url);

		System.out.println(setCookieHeader);

		// 2. 리스트 페이지에 요청
		String listPageUrl = "https://nowon.sen.ms.kr/dggb/module/board/selectBoardListAjax.do";
		String postData = "bbsId=BBSMSTR_000000002891&bbsTyCode=notice&customRecordCountPerPage=10&pageIndex=1";
		String listPageContent = HttpUtils.sendHttpPost(listPageUrl, postData, setCookieHeader);
		System.out.println("List Page Content:\n" + listPageContent);

    }

	private static void setCookies() {


	}

}
