package dog.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import dog.pojo.Role;
import dog.pojo.User;
import dog.service.role.RoleServiceImpl;
import dog.service.user.UserService;
import dog.service.user.UserServiceImpl;
import dog.util.Constants;
import dog.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 实现 servlet 的复用
public class UserModifyPwd extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null){
            this.updatePwd(req, resp);
        }else if (method.equals("add") && method != null){
            try {
                this.add(req, resp);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method.equals("pwdmodify") && method!=null){
            this.pwdModify(req, resp);
        }else if (method.equals("query") && method != null){
            try {
                this.UserManagement(req, resp);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    // 修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newPassword = req.getParameter("newpassword");
        boolean flag = false;
        if (o != null && !StringUtils.isNullOrEmpty(newPassword)){
            UserService userService = new UserServiceImpl();
            try {
                flag = userService.updatePwd(((User)o).getId(),newPassword);
                // 密码修改成功，移除 session 回到登录页面
                if(flag){

                    req.setAttribute("message","密码修改成功，请返回登录页面重新登陆！");
                    req.getSession().removeAttribute(Constants.USER_SESSION);
                }else {
                    req.setAttribute("message","密码修改失败！");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            req.setAttribute("message","新密码有问题！");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
    }
    // 验证旧密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 从 session 获取用户数据
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldPassword = req.getParameter("oldpassword");
        // 使用 map 映射结果集
        Map<String, String> resultMap = new HashMap<String, String>();
        if (o == null){
            // session 失效
            resultMap.put("result", "sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldPassword)){
            // 旧密码为 null
            resultMap.put("result", "error");
        }else {
            // 获取 session 中用户的密码，和提交参数处的密码进行比较
            String password = ((User)o).getUserPassword();
            if (password.equals(oldPassword)){
                resultMap.put("result", "true");
            }else {
                resultMap.put("result", "false");
            }
        }
        // 进行 json 数据和前端 ajax 请求的交互
        System.out.println(resultMap);
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }
    // 用户管理（重点，难点）
    public void UserManagement(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, ClassNotFoundException, ServletException {
        // 获取用户相应信息
        String userName = req.getParameter("queryname");
        String userRole = req.getParameter("queryUserRole");
        String index = req.getParameter("pageIndex");
        int roleId = 0;
        // 获取用户列表
        UserServiceImpl service = new UserServiceImpl();
        // 设置页面大小 可以写到配置文件中方便后期修改
        int pageSize = 5;
        // 当前页面默认设置为1
        int currentPageNo = 1;
        if (userName == null){
            userName = "";
        }
        if (userRole != null && !userRole.equals("")){
            // 前端页面选择的角色 id
            roleId = Integer.parseInt(userRole);
        }
        // 获取索引页
        if (index != null){
            try {
                currentPageNo = Integer.parseInt(index);
            }catch (Exception e){
                resp.sendRedirect("error.jsp");
            }
        }
        // 获取用户总数
        int totalCount = service.getUserCount(userName, roleId);
        // 分页支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);
        // 控制首页和尾页
        int pageCount = pageSupport.getTotalPageCount();
        // 如果页面小于1， 设置为1
        if (currentPageNo < 1){
            currentPageNo = 1;
        }else if (currentPageNo > pageCount){ // 大于最后一页
            currentPageNo = pageCount;
        }
        // 获取用户列表展示
        List<User> userList = service.getUserList(userName, roleId, currentPageNo, pageSize);
        req.setAttribute("userList", userList);
        // 获取所有角色
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", pageCount);
        req.setAttribute("queryUserName", userName);
        req.setAttribute("queryUserRole", userRole);
        // 返回前端
        req.getRequestDispatcher("userlist.jsp").forward(req, resp);
    }
    public boolean add(HttpServletRequest req, HttpServletResponse resp) throws ParseException, SQLException, ClassNotFoundException, IOException, ServletException {
        boolean flag = false;
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone") ;
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole") ;
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(Integer.parseInt(gender));
        user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.parseInt(userRole));
        UserServiceImpl service = new UserServiceImpl();
        if (service.add(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
            flag = true;
        }else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);

        return flag;
    }
}
