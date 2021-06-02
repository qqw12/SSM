package dog.service.role;

import dog.pojo.Role;

import java.sql.SQLException;
import java.util.List;

public interface RoleService {
    // 获取角色列表
    public List<Role> getRoleList() throws SQLException, ClassNotFoundException;
}
