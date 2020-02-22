package life.majiang.community.controller;

import life.majiang.community.provider.GithubProvider;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 向GitHub请求授权的第一步绑定在前端"登录"按钮上
 *
 * AuthorizeController处理callback页面
 * 包括获取code和state参数通过Post方式调用access_token接口获取access_token
 * 进而通过access_token调取user?access_token=xxx接口获得github用户user信息
 * 然后在数据库中插入该用户信息
 */
@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    //在application.properties中配置github.client.id等参数
    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    /**
     * GitHub用户接受该授权请求，则GitHub将带着code和state参数重定向到自己之前注册的那个callback地址
     * 这里看到带了`code`参数，`state`就是上一步请求中带的`state`参数，原样返回
     * 这里服务端需要接收这个code和state参数用于之后获取access_token
     */
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);

        //通过accessTokenDTO获取相应的access_token
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        //再由获取的access_token获取GitHub用户GithubUser对象
        GithubUser githubUser = githubProvider.getUser(accessToken);

        /**
         * 登陆成功，写Cookie
         * 新建一个论坛用户并为该用户创建一个唯一标识token写入到cookie授权给客户端(浏览器)
         * 之后浏览器再访问服务器时，服务器可以凭此token找到相应的session，从而实现登录持久化
         */
        if (githubUser != null && githubUser.getId() != null) {

            User user = new User();
            //通过UUID为当前用户创建一个唯一标识
            //如果当前用户是一个新用户，则新建token等信息，否则只更新token
            String token = UUID.randomUUID().toString();

            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAvatarUrl(githubUser.getAvatarUrl());

            /**
             * 是新用户就将其插入到数据库中然后写cookie
             * 否则只更新token并写入cookie
             */
            userService.createOrUpdate(user);
            /**
             * 并将token放入Cookie加入到response中
             * 这样浏览器带着该Cookie去请求该论坛网站服务器时
             * 服务器端就可以拿着该Cookie(即token)去数据库找对应的用户信息
             * 有的话服务端就可以直接从服务端获取该用户相关信息，无需再重新登录
             * 这样就实现了持久化登录
             *
             * 不过浏览器保存的Cookie有一定的时间期限，过了期限再访问服务端就要重新登录
             */
            response.addCookie(new Cookie("token", token));
            //重定向到首页，redirect:后面跟的是路径，这里返回根目录/
            return "redirect:/";
        } else {
            // 登录失败，重新登录，重定向到首页
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse reponse) {
        //清除session
        request.getSession().removeAttribute("user");

        /**
         * 清除cookie
         * 只要新建一个同名(这里cookie名称叫token)值为null的cookie
         * 然后设置其存活时间为0并加入到response中
         * 就可以删除该cookie
         */
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        reponse.addCookie(cookie);

        //退出登录后返回首页
        return "redirect:/";
    }
}
