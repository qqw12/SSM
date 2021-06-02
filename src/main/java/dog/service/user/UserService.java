package dog.service.user;

import dog.pojo.User;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    // 登录验证
    public User login(String userCode, String userPassword) throws SQLException, ClassNotFoundException;
    // 根据用户id修改密码
    public boolean updatePwd(int id, String userPassword) throws SQLException, ClassNotFoundException;
    // 查询记录数
    public int getUserCount(String userName, int userRole) throws SQLException, ClassNotFoundException;
    // 根据条件查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) throws SQLException, ClassNotFoundException;
    // 添加用户
    public boolean add(User user) throws SQLException, ClassNotFoundException;
}
