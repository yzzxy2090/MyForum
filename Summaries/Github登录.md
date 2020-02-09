# GitHub OAuth授权登录
## OAuth
第三方登入主要基于OAuth 2.0。OAuth（开放授权）协议是一个开放标准，为用户资源的授权提供了一个安全的、开放而又简易的标准。与以往的授权方式不同之处是OAUTH的授权不会使第三方触及到用户的帐号信息（如用户名与密码），即第三方无需使用用户的用户名与密码就可以申请获得该用户资源的授权，因此OAUTH是安全的 
---- 百度百科


## 实现步骤
#### GitHub OAuth API官方文档
[Building OAuth Apps](https://developer.github.com/apps/building-oauth-apps/)
* 创建OAuth App
![OAuth Apps](https://raw.githubusercontent.com/yzzxy2090/MyMarkDownPicture/master/UniCommunity/GitHub_OAuth/assets/OAuth3.png)
>这里的Authorization callback URL在后文中会介绍
这里仅做本地开发，所以填了本地地址，如果项目部署好了，应该填`http://xxx/callback`
打开[OAuth documentation](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)查看官方文档

#### GitHub授权流程
![callback1](https://raw.githubusercontent.com/yzzxy2090/MyMarkDownPicture/master/UniCommunity/GitHub_OAuth/assets/callback1.png)

* 这里介绍了如何使自己的项目获取GitHub授权的流程：
>第一步：跳转至GitHub去请求认证
>第二步：认证成功后，由GitHub跳转回我自己的项目
>第三步：我自己的项目通过GitHub跳转回来时带的`access token`去访问GitHub API

#### Step1.
![callback2](https://raw.githubusercontent.com/yzzxy2090/MyMarkDownPicture/master/UniCommunity/GitHub_OAuth/assets/callback2.png)
>这是第一步去GitHub认证的request所需带的参数，通过`GET`方式发送该请求`https://github.com/login/oauth/authorize`，并带参数
>注册OAuth App后会提供一个`client_id`
认证成功后回跳转到`redirect_uri`页面
`login`参数指定一个账户去获取授权
`scope`是授权后我们想要拿到的信息，这里我只需要user
`state`参数非必需，用于防治跨域伪造请求攻击
>`allow_signup`就是是否向未认证的用户提供注册GitHub的选项，默认是允许的

####Step2
![callback3](https://raw.githubusercontent.com/yzzxy2090/MyMarkDownPicture/master/UniCommunity/GitHub_OAuth/assets/callback3.png)

>如果GitHub用户接受该授权请求，则GitHub将带着`code`和`state`参数重定向到自己之前注册的那个callback地址，我这里填的是`http://localhost:8080/callback`，因此授权成功后会跳转至下面这个网址，这里看到带了`code`参数，`state`就是上一步请求中带的`state`参数，原样返回。这里服务端需要接收这个`code`和`state`参数用于之后获取`access_token`。

``` http
http://localhost:8080/github/oauth/callback?code=14de2c737aa02037132d&state=1496989988474
```
>拿到请求中的`code`参数后服务端向`https://github.com/login/oauth/access_token`这个API发送`POST`请求，并且在该请求中带上`client_id`,`client_secret`,`code`参数，请求成功后会返回带有`access_token`的信息。

>这里请求返回的Json格式信息是`access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer`，而我们需要的token是`e72e16c7e42f292c6912e7710c838347ae178b4a`，因此在处理时要注意。

#### Step3.
![callback4](https://raw.githubusercontent.com/yzzxy2090/MyMarkDownPicture/master/UniCommunity/GitHub_OAuth/assets/callback4.png)
>获取到`access_token`后， 再调用`https://api.github.com/user?access_token=xxx`这个API，就可以获取之前`scope`中对应的GitHub用户信息。 用户的基本信息内容如下所示， 根据第一步传入的不同的 scope，获取到的用户信息也是不同的，博客后台使用 login 字段作为用户的唯一标示。

#### 整个流程的顺序图
![业务流程](https://github.com/yzzxy2090/MyMarkDownPicture/blob/master/UniCommunity/GitHub_OAuth/assets/%E4%B8%9A%E5%8A%A1%E6%B5%81%E7%A8%8B%E9%A1%BA%E5%BA%8F%E5%9B%BE.png?raw=true)
这里再盗一张[linwalker](https://www.jianshu.com/u/16eae93551cd)在[第三方登入例子-GitHub授权登入（node-koa）](https://www.jianshu.com/p/a9c0b277a3b3)中的图
![示意图](https://raw.githubusercontent.com/yzzxy2090/MyMarkDownPicture/master/UniCommunity/GitHub_OAuth/assets/%E4%B8%9A%E5%8A%A1%E6%B5%81%E7%A8%8B%E7%A4%BA%E6%84%8F%E5%9B%BE.png)

## 实现
#### Step1.
点击“登录”，向GitHub发送授权请求
``` html
<li th:if="${session.user == null}">
    <a href="https://github.com/login/oauth/authorize?client_id=xxx&redirect_uri=http://localhost:8080/callback&scope=user&state=1">登录</a>
</li>
```
#### Step2.
GitHub带参数code回调callback地址
服务端拿到code参数POST请求GitHub获取access_token
GitHub返回access_token
服务端拿到access_token请求用户信息，之后将该用户信息存到数据库实现持久化
>`AuthorizeController`处理`callback`页面
``` java
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
         * 新建一个论坛用户并将其信息和该GitHub用户信息绑定起来，并将相关信息保存到数据库
         */
        if (githubUser != null && githubUser.getId() != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());

            //将新用户对象插入到数据库中
            userMapper.insert(user);
            /**
             * 并将token作为Cookie加入到response中
             */
            response.addCookie(new Cookie("token", token));
            //重定向到首页
            return "redirect:/";
        } else {
            // 登录失败，重新登录，重定向到首页
            return "redirect:/";
        }
    }
}
```
>`GithubProvider`是用于处理相关`GET`、`POST`请求的类
``` java
@Component
public class GithubProvider {
    //getAccessToken通过参数以POST方式得到相应的access_token
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        //用OkHttp模拟Http的Post、Get请求
        OkHttpClient client = new OkHttpClient();

        //用FastJson将accessTokenDTO对象转化为Json字符串
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            //对请求返回的Json字符串进行分割从而获取token
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //getUser方法通过获取的accessToken信息以GET方式去调API获取GitHub用户信息
    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            //将接受到的包含GitHub user信息的Json字符串通过FastJson转化为GithubUser对象
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
        }
        return null;
    }

}
```

#### OkHttp
本项目中采用[OkHttp](https://square.github.io/okhttp/)来模拟Http的`GET`、`POST`请求
>Get a URL
``` java
OkHttpClient client = new OkHttpClient();

String run(String url) throws IOException {
  Request request = new Request.Builder()
      .url(url)
      .build();

  try (Response response = client.newCall(request).execute()) {
    return response.body().string();
  }
}
```

>Post to a Server
``` java
public static final MediaType JSON
    = MediaType.get("application/json; charset=utf-8");

OkHttpClient client = new OkHttpClient();

String post(String url, String json) throws IOException {
  RequestBody body = RequestBody.create(json, JSON);
  Request request = new Request.Builder()
      .url(url)
      .post(body)
      .build();
  try (Response response = client.newCall(request).execute()) {
    return response.body().string();
  }
}
```

![模拟访问token1](https://raw.githubusercontent.com/yzzxy2090/MyMarkDownPicture/master/UniCommunity/GitHub_OAuth/assets/callback6.png)
![模拟访问token2](https://github.com/yzzxy2090/MyMarkDownPicture/blob/master/UniCommunity/GitHub_OAuth/assets/callback7.png?raw=true)
>这是通过获取的accessToken信息去调`https://api.github.com/user?access_token=`这个API获取GitHub用户信息
我的论坛项目所需的信息包括`id`作为用户唯一标识，`name  `作为论坛昵称，`bio`作为用户简述以及`avatarUrl`作为用户头像

