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

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
        //   <li><a href="http://www.23wx.com/book/62267" target="_blank">[下载]</a><a href="http://www.23wx.com/html/62/62267/" title="驭禽斋" target="_blank">驭禽斋</a></li>
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
            deliver(i, deltail);
        }
        if (page.getResultItems().get("name") == null) {
            //skip this page
            page.setSkip(true);
        }
        // 部分三：从页面发现后续的url地址来抓取
//        page.addTargetRequests(page.getHtml().links().regex("http://www.23wx.com/map/\\d.html").all());
    }

    private void deliver(int number, String deltail) {
        Pattern p = Pattern.compile("http://www.23wx.com/html/\\d*/\\d*/");
        Matcher m = p.matcher(deltail);
        String url = "";
        if (m.find())
            url = m.group();
        Pattern p2 = Pattern.compile("title=\"([^\"]*)");//获取title="xxx"的xxx内容；
        Matcher m2 = p2.matcher(deltail);
        String name = "";
        if (m2.find())
            name = m2.group(1);
        System.out.println("id="+number+",书名=" + name + ",url=" + url);
//        try {
//            new BookDao().insertBook(number,name,url);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new NovelPageProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl(urls[1])
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }
}

