package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.IUserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户控制器
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author ilovend
 * @date 2023/02/18
 * @since 2023-02-06
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户管理")
/*
* 优化验证码5分钟过期
* 1.生成验证码之后，以前存在session中，现在存在redis中
* 2.登录的时候，从redis中取出验证码，和用户输入的验证码进行比较，优化后，验证码5分钟过期
* 3.登录成功之后，删除redis中的验证码
* */
public class UserController {
    /**
     * 用户服务
     */
    @Autowired
    private IUserService userService;


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送短信
     *
     * @param user    用户
     * @param session 会话
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码={}",code);
//            SMSUtils.sendMessage("瑞吉外卖", "", phone, code);
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

//            session.setAttribute(phone, code);
            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {

        log.info(map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
//        Object codeInSession = session.getAttribute(phone);
//        从redis中取出验证码
        String codeForRedis = (String) redisTemplate.opsForValue().get(phone);
        if (codeForRedis != null && codeForRedis.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            redisTemplate.delete(phone);//登录成功之后，删除redis中的验证码
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
