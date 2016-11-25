package DAO;

import java.sql.*;

/**
 * Created by eado on 2016/11/22.
 */
public class BookDao {
    private Connection connection;
    private String tableBook = "book";
    private String tableChapter = "chapter";

    public BookDao() {

    }

    public int insertBook(int book_id, String book_name, String book_url) throws Exception {
        if (connection == null)
            connection = MysqlTool.getmInstance().getConnection();
        String query = " insert into " + tableBook + " (book_id, book_name, book_url)" + " values (?, ?, ?)";
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setInt(1, book_id);
        preparedStmt.setString(2, book_name);
        preparedStmt.setString(3, book_url);
        int rt = preparedStmt.executeUpdate();
        connection.close();
        preparedStmt.close();
        connection.close();
        return rt;
    }



    public int updateBook(String author, String cover_url, int is_end, String word_count, String book_id) throws Exception {
        if (connection == null)
            connection = MysqlTool.getmInstance().getConnection();
        //update radio set number=?,title=?,option1=?,option2=?,option3=?,answer=?,score=? where number=?
        String update = "update " + tableBook + " set author=?,is_end=?,word_count=? WHERE book_id=?";
        PreparedStatement preparedStmt = connection.prepareStatement(update);
        preparedStmt.setString(1, author);
        preparedStmt.setInt(2, is_end);
        preparedStmt.setString(3, word_count);
        preparedStmt.setString(4, book_id);
        int rt = preparedStmt.executeUpdate();
        connection.close();
        preparedStmt.close();
        connection.close();
        return rt;
    }


    public int insertChapter(int chapter_id,String chapter_name, String chapter_url,int book_id,int has_down) throws Exception {
        if (connection == null)
            connection = MysqlTool.getmInstance().getConnection();
        String query = " insert into " + tableChapter + " (chapter_id, chapter_name, chapter_url,book_id,has_down)" + " values (?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setInt(1, chapter_id);
        preparedStmt.setString(2, chapter_name);
        preparedStmt.setString(3, chapter_url);
        preparedStmt.setInt(4, book_id);
        preparedStmt.setInt(5, has_down);
        int rt = preparedStmt.executeUpdate();
        connection.close();
        preparedStmt.close();
        connection.close();
        return rt;
    }
}
