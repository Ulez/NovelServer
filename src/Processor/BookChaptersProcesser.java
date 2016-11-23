package Processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lcy on 2016/11/22.
 */
public class BookChaptersProcesser implements PageProcessor {

    int i = 0;
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        String oringin = page.getHtml().regex("<td class=\"L\"><a href=\"http://www.23wx.com/html/\\d*/\\d*/\\d*.html\">.*</a></td>").toString();
        Pattern pattern = Pattern.compile("<td class=.*</a></td>");
        Matcher matcher = pattern.matcher(oringin);
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
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new BookChaptersProcesser())
                .addUrl("http://www.23wx.com/html/21/21741/")
                .thread(5)
                .run();
    }
}
