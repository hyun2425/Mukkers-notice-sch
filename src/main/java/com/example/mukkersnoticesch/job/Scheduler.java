package com.example.mukkersnoticesch.job;

import com.example.mukkersnoticesch.dto.NoticeDto;
import com.example.mukkersnoticesch.util.EmailSender;
import com.example.mukkersnoticesch.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Scheduler {

//    @Scheduled(fixedDelay = 1000 * 60 * 60 * 3)
    @Scheduled(cron = "0 0 8-21/3 * * ?")
    public void myScheduledTask() {
        StringBuilder content = new StringBuilder();
        log.info("sch start");

        content.append(targetScraping("노원중", "https://nowon.sen.ms.kr/10546/subMenu.do", "https://nowon.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000002891"));
        content.append(targetScraping("중계중", "https://junggye.sen.ms.kr/9800/subMenu.do", "https://junggye.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000002612"));
        content.append(targetScraping("상계중", "https://sanggye.sen.ms.kr/16536/subMenu.do", "https://sanggye.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000005204"));
        content.append(targetScraping("창동중", "https://changdong.sen.ms.kr/17199/subMenu.do", "https://changdong.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000005441"));
        content.append(targetScraping("상천초", "https://sangcheon.sen.es.kr/73576/subMenu.do", "https://sangcheon.sen.es.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000010373"));
        content.append(targetScraping("신상중", "https://sinsang.sen.ms.kr/76919/subMenu.do", "https://sinsang.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000011678"));
        content.append(targetScraping("상계제일중", "https://sanggyejeil.sen.ms.kr/75585/subMenu.do", "https://sanggyejeil.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000011156"));
        content.append(targetScraping("상원중", "https://sangwon.sen.ms.kr/12156/subMenu.do", "https://sangwon.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000003512"));

        if (content.length() != 0) {
            log.info(content.toString());
            EmailSender.send("gustkdwn2@naver.com","먹커스 NEW 공지사항", content.toString());
            EmailSender.send("ljhforyou@ckdpharm.com","먹커스 NEW 공지사항", content.toString());
            EmailSender.send("kjh91644412@gmail.com","먹커스 NEW 공지사항", content.toString());
        }

        log.info("sch end");
    }

    private String targetScraping(String targetName, String url1, String url2, String bbsId) {
        String setCookieHeader = HttpUtils.sendHttpGetToCookies(url1);
        String postData = "bbsId=" + bbsId + "&bbsTyCode=notice&customRecordCountPerPage=10&pageIndex=1";

        String listPageContent = HttpUtils.sendHttpPost(url2, postData, setCookieHeader);

        Document document = Jsoup.parse(listPageContent);
        Elements trElements = document.select("tr");
        List<NoticeDto> trList = new ArrayList<>();

        for (Element trElement : trElements) {
            Elements tdElements = trElement.select("td");

            NoticeDto noticeDto = new NoticeDto();
            for (int i = 0; i < tdElements.size(); i++) {
                if (i == 1) noticeDto.setTitle(tdElements.get(i).text());
                if (i == 3) noticeDto.setDate(tdElements.get(i).text());
            }
            trList.add(noticeDto);
        }

        return noticeSting(targetName, trList, url1);
    }

    private String noticeSting(String targetName, List<NoticeDto> trList, String link) {
        StringBuilder result = new StringBuilder();
        for (NoticeDto noticeDto : trList) {
            if (noticeDto.getTitle() == null) continue;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate givenDate = LocalDate.parse(noticeDto.getDate(), formatter);

            LocalDate specificDate = LocalDate.now();
//            LocalDate specificDate = LocalDate.parse("2024-01-02", formatter);

            if (givenDate.isEqual(specificDate)) {
                String content = String.format("[%s]\n제목 : %s \n날짜 : %s \n링크 : %s\n\n", targetName, noticeDto.getTitle(), noticeDto.getDate(), link);
                result.append(content);
            }
        }

        return result.toString();
    }

}
