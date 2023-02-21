package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.IEmployeeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 员工控制器
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author ilovend
 * @date 2023/02/12
 * @since 2023-02-06
 */
@RestController
@RequestMapping("/employee")
@Slf4j
@Api(tags = "员工信息")
public class EmployeeController {

    /**
     * 员工服务
     */
    @Autowired
    private IEmployeeService employeeService;


    /**
     * 登录
     *
     * @param employee 员工
     * @param request  请求
     * @return {@link R}
     */
    @PostMapping("/login")
    public R login(@RequestBody Employee employee, HttpServletRequest request) {

        String password = employee.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        if (emp == null) {
            return R.error("用户名不存在");
        }

        if (emp.getStatus() == 0) {
            return R.error("账号已被禁用");
        }

        String salt = emp.getPassword().substring(32);
        String miwen = DigestUtils.md5DigestAsHex((employee.getPassword() + salt).getBytes()) + salt;
        if (!miwen.equals(emp.getPassword())) {
            return R.error("密码错误");
        }

//        如果登录成功需要在session中存入id 并返回登录成功信息
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 登录了
     * 登出
     *
     * @param request 请求
     * @return {@link R}
     */
    @PostMapping("/logout")
    public R loginOut(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.success("退出成功");
    }


    /**
     * 保存
     *
     * @param request  请求
     * @param employee 员工
     * @return {@link R}<{@link String}>
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息为：{}", employee);

        //初始密码设置为123456，需要进行MD5加密处理
        String salt = UUID.randomUUID().toString().replace("-", "");
        String md5Password = DigestUtils.md5DigestAsHex(("123456" + salt).getBytes()) + salt;
        employee.setPassword(md5Password);

/*        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/
        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    /**
     * 页面
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @param name     名字
     * @return {@link R}<{@link Page}<{@link Employee}>>
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造查询构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据名称模糊查询
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //根据创建时间倒序
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 更新
     *
     * @param request  请求
     * @param employee 员工
     * @return {@link R}<{@link String}>
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());
 /*       Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());*/
        employeeService.updateById(employee);
        return R.success("修改员工成功");
    }

    /**
     * 通过id
     *
     * @param id id
     * @return {@link R}<{@link Employee}>
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }





}
