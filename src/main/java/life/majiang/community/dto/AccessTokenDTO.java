package life.majiang.community.dto;

import lombok.Data;

/**
 * 这是GitHub授权认证的第二步
 * 带着code和state参数通过过Post方式调用https://github.com/login/oauth/access_token接口获取access_token
 * 所需的五个参数
 *
 * DTO是Data Transfer Model即数据传输模型
 * 封装用于传输的数据
 *
 * 对比model层的数据，model层数据对应数据库中的持久化数据
 */
@Data
public class AccessTokenDTO {
    private String client_id;//注册OAuth Apps后由GitHub提供
    private String client_secret;//注册OAuth Apps后由GitHub提供
    private String code;//GitHub回调callback页面时会带回一个code参数
    private String redirect_uri;//注册OAuth Apps时自定义的Authorization callback URL，这里是http://localhost:8887/callback
    private String state;//参数非必需，用于防治跨域伪造请求攻击
}
