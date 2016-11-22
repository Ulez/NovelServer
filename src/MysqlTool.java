import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by eado on 2016/11/22.
 */
public class MysqlTool {
    private static MysqlTool mInstance;
    private Connection connection;

    private MysqlTool() {

    }

    public static MysqlTool getmInstance() {
        if (mInstance == null) {
            synchronized (MysqlTool.class) {
                if (mInstance == null)
                    mInstance = new MysqlTool();
            }
        }
        return mInstance;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        String serverName = "localhost";
        String database = "noveldb";
        String url = "jdbc:mysql://" + serverName + "/" + database + "?autoReconnect=true&useSSL=false";

        // 数据配置用户和密码
        String user = "root";
        String password = "1";

        return DriverManager.getConnection(url, user, password);
    }
}
