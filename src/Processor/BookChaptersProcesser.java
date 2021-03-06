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
