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

    //    @Scheduled(cron = "0 0/3 * * * ?")
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 3)
    public void myScheduledTask() {
        log.info("sch start");

        targetScraping("노원중", "https://nowon.sen.es.kr/10546/subMenu.do", "https://nowon.sen.es.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000002891");
        targetScraping("중계중", "https://junggye.sen.ms.kr/9800/subMenu.do", "https://junggye.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000002612");
        targetScraping("상계중", "https://sanggye.sen.ms.kr/16536/subMenu.do", "https://sanggye.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000005204");
        targetScraping("창동중", "https://changdong.sen.ms.kr/17199/subMenu.do", "https://changdong.sen.ms.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000005441");
        targetScraping("상천초", "https://sangcheon.sen.es.kr/73576/subMenu.do", "https://sangcheon.sen.es.kr/dggb/module/board/selectBoardListAjax.do", "BBSMSTR_000000010373");

        log.info("sch end");
    }

    private static void targetScraping(String targetName, String url1, String url2, String bbsId) {
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

        noticeCheckSend(targetName, trList, url1);
    }

    private static void noticeCheckSend(String targetName, List<NoticeDto> trList, String link) {
        for (NoticeDto noticeDto : trList) {
            if (noticeDto.getTitle() == null) continue;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate givenDate = LocalDate.parse(noticeDto.getDate(), formatter);

            LocalDate specificDate = LocalDate.parse("2023-12-29", formatter);

            if (givenDate.isAfter(specificDate)) {
                String content = String.format("제목 : %s \n날짜 : %s \n링크 : %s", noticeDto.getTitle(), noticeDto.getDate(), link);
                EmailSender.send("hsj@infotech.co.kr", targetName + " NEW 공지사항", content);
                EmailSender.send("ljhforyou@ckdpharm.com", targetName + " NEW 공지사항", content);
                log.info(targetName + "\n" + content);
            }
        }
    }

}
