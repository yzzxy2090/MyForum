package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
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
    private UserMapper userMapper;

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
         * 新建一个论坛用户并将其信息和该GitHub用户信息绑定起来
         */
        if (githubUser != null && githubUser.getId() != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());

            if(githubUser.getAvatarUrl() == null) {
                user.setAvatarUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1582056311949&di=111ee0ab64d52684fed42d7f6845188b&imgtype=jpg&src=http%3A%2F%2Fimg.qqzhi.com%2Fuploads%2F2018-12-15%2F094927264.jpg");
            } else {
                user.setAvatarUrl(githubUser.getAvatarUrl());
            }

            //将新用户对象插入到数据库中
            userMapper.insert(user);
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
}
