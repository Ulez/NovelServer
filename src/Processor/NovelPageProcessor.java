package Processor;

import org.apache.http.util.TextUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lcy on 2016/11/21.
 */
public class NovelPageProcessor implements PageProcessor {
    private int i = 0;
    static String urls[] = new String[]{
            "http://www.23wx.com/map/2.html",
            "http://www.23wx.com/map/1.html",
            "http://www.23wx.com/map/4.html",
            "http://www.23wx.com/map/3.html",
            "http://www.23wx.com/map/6.html",
            "http://www.23wx.com/map/7.html"
    };
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    Pattern p_novel_type = Pattern.compile("http://www.23wx.com/map/\\d*.html");

    //http://www.23wx.com/html/58/58148/23902489.html

    Pattern p_chapter_content = Pattern.compile("http://www.23wx.com/html/\\d*/\\d*/\\d*.html");  //"http://www.23wx.com/html/21/21741/21040339.html"
    Pattern p_chapters = Pattern.compile("http://www.23wx.com/html/\\d*/\\d*/"); //http://www.23wx.com/html/21/21741/
    @Override
    public void process(Page page) {
        Matcher m_novel_type = p_novel_type.matcher(page.getUrl().toString());
        Matcher m_chapter_content = p_chapter_content.matcher(page.getUrl().toString());
        Matcher m_chapters = p_chapters.matcher(page.getUrl().toString());
        if (m_chapter_content.find()){
            getChapterContent(page);
        }else if (m_chapters.find()){
            getChaptersName(page);
        }else if (m_novel_type.find()) {
            getBooks(page);
        }
    }
    private void getChaptersName(Page page) {
        String origin = page.getHtml().regex("<td class=\"L\"><a href=\"http://www.23wx.com/html/\\d*/\\d*/\\d*.html\">.*</a></td>").toString();
        Pattern pattern = Pattern.compile("<td class=.*</a></td>");
        if (pattern!=null&& !TextUtils.isEmpty(origin)){
            Matcher matcher = pattern.matcher(origin);
            while (matcher.find()) {//截取章节信息；
                i++;
                String chapterDetail = matcher.group();
                //<td class="L"><a href="http://www.23wx.com/html/21/21741/21068062.html">第八部 第九百二十二章 傲世之大结局（全书完！）</a></td>
//            System.out.println(matcher.group());
                Pattern p = Pattern.compile("http://www.23wx.com/html/\\d*/\\d*/\\d*.html");
                Matcher m = p.matcher(chapterDetail);
                String chapter_url = "";
                if (m.find())
                    chapter_url = m.group();
//            Pattern p2 = Pattern.compile("title=\"([^\"]*)");//获取title="xxx"的xxx内容；
                Pattern p2 = Pattern.compile("<td class=\"L\"><a href=\"http://www.23wx.com/html/\\d*/\\d*/\\d*.html\">(.*)</a></td>");//获取title="xxx"的xxx内容；
                //<td class="L"><a href="http://www.23wx.com/html/21/21741/21111907.html">完本感言！（跟大家说说话儿（二））</a></td>
                Matcher m2 = p2.matcher(chapterDetail);
                String chapter_name = "";
                if (m2.find())
                    chapter_name = m2.group(1);
                System.out.println("id=" + i + ",章节名=" + chapter_name + ",chapter_url=" + chapter_url);
                page.addTargetRequest(chapter_url);
            }
        }
    }
    private void getChapterContent(Page page) {
        String content = page.getHtml().regex("<dd id=.*>.*</dd>.*<div class=\"adhtml\">").toString();
        content=content.replaceAll("<dd id=\"contents\">|&nbsp;&nbsp;&nbsp;&nbsp;|<br />&nbsp;&nbsp;&nbsp;&nbsp;|<br />|</dd>|<div class=\"adhtml\">"," ");
        System.out.println("本章节内容："+content);
        String url=page.getUrl().toString();
        System.out.println(url);
        try {
            if (FileUtil.writeTxtFile(content,url)){
                System.out.println("保存章节成功，url="+url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBooks(Page page) {
        page.putField("url", page.getUrl().toString());
        System.out.println("************************************小说类型url**********************************=" + page.getUrl().toString());
//        page.putField("name", page.getHtml().regex("<li><a href=\"http://www.23wx.com/book/.*[下载]</a>.*</a></li>").toString());
        String origin_books = page.getHtml().regex("<li><a href=\"http://www.23wx.com/book/.*[下载]</a>.*</a></li>").toString();
        Pattern pattern = Pattern.compile("<li>.*</li>");
        Matcher matcher = pattern.matcher(origin_books);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        for (String deltail : list) {
            i++;
//            System.out.println("第" + i + "本书：" + deltail);
            deliver(page,i, deltail);
        }
        if (page.getResultItems().get("name") == null) {
            //skip this page
            System.out.println("page.getResultItems().get(\"name\")"+page.getResultItems().get("name"));
//            page.setSkip(true);
        }
    }

    private void deliver(Page page,int number, String detail) {
        Pattern p = Pattern.compile("http://www.23wx.com/html/\\d*/\\d*/");
        Matcher m = p.matcher(detail);
        String book_url = "";
        if (m.find())
            book_url = m.group();
        Pattern p2 = Pattern.compile("title=\"([^\"]*)");//获取title="xxx"的xxx内容；
        Matcher m2 = p2.matcher(detail);
        String name = "";
        if (m2.find())
            name = m2.group(1);
        System.out.println("id=" + number + ",书名=" + name + ",url=" + book_url);
        page.addTargetRequest(book_url);
        try {
            new DAO.BookDao().insertBook(number,name,book_url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new NovelPageProcessor())
                .addUrl("http://www.23wx.com/map/9.html")
                .thread(5)
                .run();
    }
}

