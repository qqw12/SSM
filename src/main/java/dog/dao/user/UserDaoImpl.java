package dog.dao.user;

import com.mysql.jdbc.StringUtils;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import dog.dao.BaseDao;
import dog.pojo.Role;
import dog.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao{
    public User getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        if(connection != null){
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            resultSet = BaseDao.execute(connection, sql, params, resultSet, preparedStatement);
            if (resultSet.next()){
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreationDate(resultSet.getDate("creationDate"));
                user.setModifyBy(resultSet.getInt("modifyBy"));
                user.setModifyDate(resultSet.getDate("modifyDate"));
            }
            BaseDao.closeResource(connection, preparedStatement, resultSet);
        }

        return user;
    }

    public int updatePwd(Connection connection, int id, String userPassword) throws SQLException {
        PreparedStatement preparedStatement = null;
        int execute = 0;

        if(connection != null){
            String sql = "update smbms_user set userPassword=? where id=?";
            Object[] params = {userPassword, id};
            execute = BaseDao.execute(connection, sql, params, preparedStatement);
            BaseDao.closeResource(connection,preparedStatement,null);
        }
        return execute;
    }

    public int getUserCount(Connection connection, String userName, int userRole) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null){
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u, smbms_role r where u.userRole = r.id");
            // 使用 list 存放查询参数
            ArrayList<Object> list = new ArrayList<Object>();
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                list.add("%" + userName + "%");
            }
            if (userRole > 0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            // 将 list 转换为数组
            Object[] params = list.toArray();
            // 输出完整 sql 语句 便于调试
            System.out.println("getUserCount -> " + sql);
            rs = BaseDao.execute(connection, sql.toString(), params, rs, ps);
            if (rs.next()){
                // 获取总数量
                count = rs.getInt("count");
            }
            BaseDao.closeResource(null, ps, rs);
        }
        return count;
    }

    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        if (connection != null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u, smbms_role r where u.userRole = r.id");
            List<Object> list = new ArrayList<Object>();
            if (StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                list.add("%"+ userName +"%");
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);
            Object[] params = list.toArray();
            System.out.println("getUserList -> sql "+ sql);
            rs = BaseDao.execute(connection, sql.toString(), params, rs, ps);
            while (rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setUserRole(rs.getInt("userRole"));
                user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(user);
            }
            BaseDao.closeResource(connection, ps, rs);
        }
        return userList;
    }

    @Override
    public int add(Connection connection, User user) throws SQLException {
        PreparedStatement ps = null;
        int updateRows = 0;
        if (connection != null){
            String sql = "insert into smbms_user(userCode, userName, userPassword, " +
                    "gender, birthday, phone, address, userRole, createdBy, " +
                    "creationDate) VALUES (?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(),user.getUserName(),user.getUserPassword(),
                    user.getGender(),user.getBirthday(),user.getPhone(),user.getAddress(),
                    user.getUserRole(),user.getCreatedBy(),user.getCreationDate()};
            updateRows = BaseDao.execute(null, sql, params,  ps);
            BaseDao.closeResource(null, ps, null);
        }

        return updateRows;
    }
}
