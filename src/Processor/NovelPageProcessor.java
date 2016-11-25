package Processor;

import DAO.BookDao;
import org.apache.http.util.TextUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

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
    Pattern p_book_info = Pattern.compile("http://www.23wx.com/book/\\d*");     //http://www.23wx.com/book/61432

    @Override
    public void process(Page page) {
        String current_url = page.getUrl().toString();
        Matcher m_novel_type = p_novel_type.matcher(current_url);
        Matcher m_chapter_content = p_chapter_content.matcher(current_url);
        Matcher m_chapters = p_chapters.matcher(current_url);
        Matcher m_book_info = p_book_info.matcher(current_url);
        if (m_chapter_content.find()) {
            getChapterContent(page);
        } else if (m_chapters.find()) {
            getChaptersName(page);
        } else if (m_novel_type.find()) {
            getBooks(page);
        } else if (m_book_info.find()) {
            getBookInfo(page);
        }
    }

    String regex_book_info = "<tbody>(.*)</tbody>\\s*</table>\\s*<p></p>";//书本详细信息正则表达式；

    private void getBookInfo(Page page) {
        Pattern pattern = Pattern.compile("http://www.23wx.com/book/(\\d*)");
        Matcher matcher = pattern.matcher(page.getUrl().toString());
        String book_id = "";
        if (matcher.find())
            book_id = matcher.group(1);
        String content = page.getHtml().regex(regex_book_info, 1).toString();
//        System.out.println("书本详细信息:" + content + "book_id=" + book_id);
        Pattern p = Pattern.compile("<th>(.*)</th>\\s*<td>&nbsp;(.*)</td>");
        Matcher m = p.matcher(content);
        String word_count = "";
        int is_end = 0;
        String author = "";
        while (m.find()) {
            if ("全文长度".equals(m.group(1)))
                word_count = m.group(2);
            else if ("文章作者".equals(m.group(1)))
                author = m.group(2);
            else if ("文章状态".equals(m.group(1)))
                is_end = "已完成".equals(m.group(2)) ? 1 : 0;
            System.out.println(m.group(1) + ":" + m.group(2));
        }
        try {
            new DAO.BookDao().updateBook(author, "", is_end, word_count, book_id);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getChaptersName(Page page) {
        String book_info_url = page.getHtml().regex("http://www.23wx.com/book/\\d*").toString();
        System.out.println("book_info_url=" + book_info_url);
        page.addTargetRequest(book_info_url);

        String origin = page.getHtml().regex("<td class=\"L\"><a href=\"http://www.23wx.com/html/\\d*/\\d*/\\d*.html\">.*</a></td>").toString();
        Pattern pattern = Pattern.compile("<td class=.*</a></td>");
        if (pattern != null && !TextUtils.isEmpty(origin)) {
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
        //<dd><h1>正文 第六章 问话</h1></dd>
        String chapter_url = page.getUrl().toString();
        //http://www.23wx.com/html/3/3609/1235222.html
        Pattern p=Pattern.compile("http://www.23wx.com/html/\\d*/(\\d*)/(\\d*).html");
        Matcher m=p.matcher(chapter_url);
        int book_id=0;
        int chapter_id=0;
        if (m.find()){
            book_id=Integer.valueOf(m.group(1));
            chapter_id=Integer.valueOf(m.group(2));
        }
        Html html = page.getHtml();
        String chapter_name = html.regex("<title>.*-(.*)-顶点小说</title>",1).toString();
        String content = html.regex("<dd id=.*>.*</dd>.*<div class=\"adhtml\">").toString();
        content = content.replaceAll("<dd id=\"contents\">|&nbsp;&nbsp;&nbsp;&nbsp;|<br />&nbsp;&nbsp;&nbsp;&nbsp;|<br />|</dd>|<div class=\"adhtml\">", " ");
        try {
            if (FileUtil.writeTxtFile(content, chapter_url)) {
                new BookDao().insertChapter(chapter_id,chapter_name,chapter_url,book_id,1);
                System.out.println("保存章节成功，url=" + chapter_url+chapter_name);
            }else {
                new BookDao().insertChapter(chapter_id,chapter_name,chapter_url,book_id,0);
                System.out.println("保存章节失败，url=" + chapter_url+chapter_name);
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
        while (matcher.find()) {
            i++;
            deliver(page, i, matcher.group());
        }
        if (page.getResultItems().get("name") == null) {
            //skip this page
            System.out.println("page.getResultItems().get(\"name\")" + page.getResultItems().get("name"));
//            page.setSkip(true);
        }
    }

    private void deliver(Page page, int number, String detail) {
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
            new DAO.BookDao().insertBook(getStr(book_url, "http://www.23wx.com/html/\\d*/(\\d*)/"), name, book_url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getStr(String book_url, String regex) {
        Pattern p2 = Pattern.compile(regex);//获取title="xxx"的xxx内容；
        Matcher m2 = p2.matcher(book_url);
        if (m2.find())
            return Integer.valueOf(m2.group(1));
        else return 0;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new NovelPageProcessor())
                .addUrl("http://www.23wx.com/html/61/61763/")
                .thread(5)
                .run();
    }
}

