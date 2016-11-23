package DAO;

import java.sql.*;

/**
 * Created by eado on 2016/11/22.
 */
public class BookDao {
    private Connection connection;
    private String tableName = "book";

    public BookDao(){

    }

    public int insertBook(int id, String book_name, String book_url) throws Exception {
        if (connection == null)
            connection = MysqlTool.getmInstance().getConnection();
        String query = " insert into " + tableName + " (book_id, book_name, book_url)"
                + " values (?, ?, ?)";
        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setInt(1, id);
        preparedStmt.setString(2, book_name);
        preparedStmt.setString(3, book_url);
        int rt=preparedStmt.executeUpdate();
        connection.close();
        /** Closing JDBC connection */
//        rs.close();
        preparedStmt.close();
        connection.close();
        return rt;
    }
}
