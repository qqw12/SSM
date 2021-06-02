package dog.service.role;

import dog.dao.BaseDao;
import dog.dao.role.RoleDao;
import dog.dao.role.RoleDaoImpl;
import dog.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoleServiceImpl implements RoleService{
    // 引入Dao
    private RoleDao roleDao;
    public RoleServiceImpl(){
        roleDao = new RoleDaoImpl();
    }


    @Override
    public List<Role> getRoleList() throws SQLException, ClassNotFoundException {
        Connection connection = BaseDao.getConnection();
        List<Role> roleList = null;
        roleList = roleDao.getRoleList(connection);

        BaseDao.closeResource(connection, null, null);

        return roleList;
    }

    @Test
    public void test() throws SQLException, ClassNotFoundException {
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> list = roleService.getRoleList();
        for (Role role : list) {
            System.out.println(role.getRoleName());
        }
    }
}

