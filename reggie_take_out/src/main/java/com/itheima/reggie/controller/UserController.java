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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

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
public class UserController {
    /**
     * 用户服务
     */
    @Autowired
    private IUserService userService;

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


            session.setAttribute(phone, code);
            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {

        log.info(map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object codeInSession = session.getAttribute(phone);

        if (codeInSession != null && codeInSession.equals(code)) {
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
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
