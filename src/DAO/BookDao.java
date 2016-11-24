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

    public int insertBook(String book_name, String book_url) throws Exception {
        if (connection == null)
            connection = MysqlTool.getmInstance().getConnection();
        String query = " insert into " + tableBook + " (book_id, book_name, book_url)" + " values (?, ?, ?)";
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setString(3, book_name);
        preparedStmt.setString(4, book_url);
        int rt = preparedStmt.executeUpdate();
        connection.close();
        preparedStmt.close();
        connection.close();
        return rt;
    }

    public int insertChapter(String chapter_name, String chapter_url) throws Exception {
        if (connection == null)
            connection = MysqlTool.getmInstance().getConnection();
        String query = " insert into " + tableChapter + " (chapter_id, chapter_name, chapter_url)" + " values (?, ?, ?)";
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setString(3, chapter_name);
        preparedStmt.setString(4, chapter_url);
        int rt = preparedStmt.executeUpdate();
        connection.close();
        preparedStmt.close();
        connection.close();
        return rt;
    }
}
