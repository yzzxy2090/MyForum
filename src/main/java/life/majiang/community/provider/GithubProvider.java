package life.majiang.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * GithubProvider提供对GitHub授权的Http请求支持能力
 * 包括获取code和state参数通过Post方式调用https://github.com/login/oauth/access_token接口获取access_token
 * 进而通过access_token以Get方式调取https://api.github.com/user?access_token=xxx接口获得github用户user信息
 *
 * Component组件会被初始化到Spring ioc容器的上下文
 * ioc容器负责实例化该GithubProvider
 *
 * AccessTokenDTO是GitHub授权认证的第二步所需要的五个参数
 * 由于参数比较多因此将他们封装成一个对象
 */
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
            //这里请求返回的Json格式信息是access_token=xxx&scope=user&token_type=xxx，而我们需要的token是xxx，因此要对得到的Json字符串进行分割
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //getUser方法通过获取的accessToken信息以GET方式去调“https://api.github.com/user?access_token=”这个API获取GitHub用户信息
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
