package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired //注入userMapper
    private UserMapper userMapper;

    @RequestMapping("/add")
    public String listStudent(User user) throws Exception {
        userMapper.save(user);
        return "redirect:list";//redirect:list表示重定向当前网页。因为有新增项目
    }

    @RequestMapping("/delete")
    public String deleteStudent(User user) throws Exception {
        userMapper.delete(user.getId());
        return "redirect:list";//删除项目之后需要重定向
    }

    @RequestMapping("/update")
    public String updateStudent(User user) throws Exception {
        userMapper.update(user);
        return "redirect:list";//更新项目之后需要重定向
    }

    @RequestMapping("/edit")
    public String listStudent(int id, Model m) throws Exception {
        User user = userMapper.get(id);
        m.addAttribute("user", user);
        return "edit";
    }

    @RequestMapping("/list")
    public String listStudent(Model m, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        PageHelper.startPage(start, size, "id desc");
        List<User> students = userMapper.findAll();
        PageInfo<User> page = new PageInfo<>(students);
        m.addAttribute("page", page);
        return "list";
    }
}
