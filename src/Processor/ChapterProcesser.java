package Processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by lcy on 2016/11/22.
 */
public class ChapterProcesser implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        String content = page.getHtml().regex("<dd id=.*>.*</dd>.*<div class=\"adhtml\">").toString();
        content=content.replaceAll("<dd id=\"contents\">|&nbsp;&nbsp;&nbsp;&nbsp;|<br />&nbsp;&nbsp;&nbsp;&nbsp;|<br />|</dd>|<div class=\"adhtml\">"," ");
        System.out.println(content);

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

    @Override
    public Site getSite() {
        return site;
    }
    public static void main(String[] args) {
        Spider.create(new ChapterProcesser())
                .addUrl("http://www.23wx.com/html/21/21741/21040339.html")
                .thread(1)
                .run();
    }
}
