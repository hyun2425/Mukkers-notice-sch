package com.example.mukkersnoticesch;

import com.example.mukkersnoticesch.dto.NoticeDto;
import com.example.mukkersnoticesch.util.EmailSender;
import com.example.mukkersnoticesch.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class MukkersNoticeSchApplicationTests {

    @Test
    void test01() {
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

        // column1:제목  column3:날짜
        for (Map<String, String> rowData : trList) {
            if (rowData.isEmpty()) continue;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate givenDate = LocalDate.parse(rowData.get("column3"), formatter);

            LocalDate specificDate = LocalDate.parse("2023-12-28", formatter);

            if (givenDate.isAfter(specificDate)) {
                String content = "제목 : " + rowData.get("column1") + "\n날짜 : " + rowData.get("column3");
                EmailSender.send("hsj@infotech.co.kr", "[노원중] 새로운 공지사항", content);
                EmailSender.send("ljhforyou@ckdpharm.com", "[노원중] 새로운 공지사항", content);
            }
        }
    }

}
