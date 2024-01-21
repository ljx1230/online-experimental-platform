package com.ljx.file;

import com.ljx.file.mapper.UserMapper;
import com.ljx.file.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: ljx
 * @Date: 2023/12/19 16:58
 */
@SpringBootTest
public class ApplicationTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void test01() throws Exception {
        User user = new User();
        user.setUsername("ljx1230");
        user.setPassword("qwaszx123");
        user.setTelephone("19914628371");
        userMapper.insertUser(user);
        List<User> users = userMapper.selectUser();
        users.forEach(System.out::println);
    }

    @Test
    public void test02() throws Exception {
        User user = new User();
        user.setUsername("xjy1201");
        user.setTelephone("12345678910");
        user.setPassword("qwaszx123");
        userMapper.insert(user);
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
    }
}
