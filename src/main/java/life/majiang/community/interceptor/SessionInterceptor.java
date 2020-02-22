package life.majiang.community.interceptor;

import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Session拦截器
 * 对每个网页请求进行拦截
 * 验证是否登录
 */

@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    /**
     * https://blog.csdn.net/weixin_41767154/article/details/84648873
     * https://www.cnblogs.com/winner-0715/p/9749039.html
     *
     * 在Controller处理之前进行调用
     *
     * SpringMVC中的Interceptor拦截器是链式的
     * 可以同时存在多个Interceptor，然后SpringMVC会根据声明的前后顺序一个接一个的执行
     * 而且所有的Interceptor中的preHandle方法都会在Controller方法调用之前调用。
     *
     * （SpringMVC的这种Interceptor链式结构也是可以进行中断的
     * 这种中断方式是令preHandle的返回值为false
     * 当preHandle的返回值为false的时候整个请求就结束了。）
     *
     * 主要做身份认证、身份授权
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0)
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();

                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);

                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {
                        request.getSession().setAttribute("user", users.get(0));
                    }
                    break;
                }
            }
        return true;
    }

    /**
     * 只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行
     *
     * 在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行
     * 也就是说在这个方法中你可以对ModelAndView进行操作。
     *
     * 可以在此将公共模型数据传到视图(如导航栏)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * preHandle方法返回false，或者之前pre或post发生了异常，最终都会执行afterCompletion方法(triggerAfterCompletion)
     *
     * 在整个请求完成之后，也就是DispatcherServlet完成视图渲染后执行
     * （这个方法的主要作用是用于清理资源的，包括统一日志处理、异常处理）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
