package dog.servlet.user;

import dog.pojo.User;
import dog.service.user.UserServiceImpl;
import dog.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    // servlet 控制层 ， 调用业务层代码
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        // 和数据库中的密码进行对比
        UserServiceImpl userService = new UserServiceImpl();
        try {
            User user = userService.login(userCode, userPassword);
            if (user != null && userPassword.equals(user.getUserPassword())){
                // 用户存在，将用户放入 session 中
                req.getSession().setAttribute(Constants.USER_SESSION, user);
                // 跳转到内部主页
                resp.sendRedirect("jsp/frame.jsp");
            }else {
                // 查无此人，回到登录页面，显示错误信息
                req.setAttribute("error","用户名或者密码不正确！");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
