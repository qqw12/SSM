package dog.service.user;

import com.sun.org.apache.xpath.internal.operations.Bool;
import dog.dao.BaseDao;
import dog.dao.user.UserDao;
import dog.dao.user.UserDaoImpl;
import dog.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService{
    // 业务层会调用 dao 层
    private UserDao userDao;
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    public User login(String userCode, String userPassword) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        User user = null;
        connection = BaseDao.getConnection();
        // 通过业务层调用对应的数据库操作
        user = userDao.getLoginUser(connection, userCode);
        BaseDao.closeResource(connection, null, null);
        return user;
    }

    public boolean updatePwd(int id, String userPassword) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        connection = BaseDao.getConnection();
        boolean flag = false;
        System.out.println("UserServiceImpl============="+userPassword);
        if(userDao.updatePwd(connection,id,userPassword) > 0){
            flag = true;
        }
        BaseDao.closeResource(connection,null,null);
        return flag;
    }

    public int getUserCount(String userName, int userRole) throws SQLException, ClassNotFoundException {
        int count = 0;
        Connection connection = BaseDao.getConnection();
        count = userDao.getUserCount(connection, userName, userRole);
        BaseDao.closeResource(connection, null, null);

        return count;
    }

    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        List<User> userList = null;
        System.out.println("queryUserName -> "+queryUserName);
        System.out.println("queryUserRole -> "+queryUserRole);
        System.out.println("currentPageNo -> "+currentPageNo);
        System.out.println("pageSize -> "+pageSize);
        connection = BaseDao.getConnection();
        userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        BaseDao.closeResource(connection, null, null);

        return userList;
    }

    @Override
    public boolean add(User user) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        boolean flge = false;
        try {
            connection = BaseDao.getConnection();
            // 关闭自动提交事务
            connection.setAutoCommit(false);
            int updateRows = userDao.add(connection,user);
            connection.commit();
            if (updateRows > 0){
                flge = true;
                System.out.println(" commit success!");
            }else {
                System.out.println(" commit failed!");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("rollback ============");
            connection.rollback();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flge;
    }

    @Test
    public void test() throws SQLException, ClassNotFoundException {
        UserServiceImpl service = new UserServiceImpl();
        int count = service.getUserCount(null, 1);
        System.out.println(count);
    }
}
