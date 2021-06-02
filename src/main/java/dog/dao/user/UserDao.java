package dog.dao.user;

import dog.pojo.Role;
import dog.pojo.User;
import dog.util.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    public User getLoginUser(Connection connection, String userCode) throws SQLException;
    // 修改用户密码
    public int updatePwd(Connection connection, int id, String userPassword) throws SQLException;
    // 根据用户名或者用户角色查询用户总数
    public int getUserCount(Connection connection, String userName, int userRole) throws SQLException;
    // 获取用户列表
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException;
    // 添加用户
    public int add(Connection connection, User user) throws SQLException;

}
