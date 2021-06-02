package dog.dao.role;

import dog.dao.BaseDao;
import dog.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{
    // 获取角色列表
    public List<Role> getRoleList(Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Role> roleArrayList = new ArrayList<Role>();
        if (connection != null){
            String sql = "select * from smbms_role";
            Object[] params = {};
            rs = BaseDao.execute(connection, sql, params, rs, ps);
            while (rs.next()){
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                roleArrayList.add(role);
            }
            BaseDao.closeResource(connection, ps, rs);
        }
        return roleArrayList;
    }
}
