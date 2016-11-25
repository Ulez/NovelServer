package Processor;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eado on 2016/11/23.
 */
public class FileUtil {
    /**
     * 创建文件
     *
     * @param fileName
     * @return
     */
    public static boolean createFile(File fileName) throws Exception {
        boolean flag = false;
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 读TXT文件内容
     *
     * @param fileName
     * @return
     */
    public static String readTxtFile(File fileName) throws Exception {
        String result = null;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            try {
                String read = null;
                while ((read = bufferedReader.readLine()) != null) {
                    result = result + read + "\r\n";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }
        }
        System.out.println("读取出来的文件内容是：" + "\r\n" + result);
        return result;
    }

    public static boolean writeTxtFile(String content, String url) throws Exception {

        Pattern pattern = Pattern.compile("http://www.23wx.com/html/\\d*/(\\d*)/(\\d*).html");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String book_id = matcher.group(1);
            String chapter_id = matcher.group(2);
            System.out.println("book_id=" + book_id + ",chapter_id=" + chapter_id);
            File book = new File("E:\\Novels\\" + book_id);
            if (!book.exists())
                book.mkdir();
            File chapter = new File("E:\\Novels\\" + book_id + "\\" + chapter_id + ".txt");
            if (chapter.exists()){
                System.out.println("已经存在文件.");
                return true;
            }
            RandomAccessFile mm = null;
            boolean flag = false;
            FileOutputStream o = null;
            try {
                o = new FileOutputStream(chapter);
                o.write(content.getBytes("GBK"));
                o.close();
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mm != null) {
                    mm.close();
                }
            }
            return flag;
        } else {
            return false;
        }
    }


    public static void contentToTxt(String filePath, String content) {
        String str = new String(); //原有txt内容
        String s1 = new String();//内容更新
        try {
            File f = new File(filePath);
            if (f.exists()) {
                System.out.print("文件存在");
            } else {
                System.out.print("文件不存在");
                f.createNewFile();// 不存在则创建
            }
            BufferedReader input = new BufferedReader(new FileReader(f));

            while ((str = input.readLine()) != null) {
                s1 += str + "\n";
            }
            System.out.println(s1);
            input.close();
            s1 += content;

            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            output.write(s1);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}