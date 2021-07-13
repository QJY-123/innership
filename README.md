
#SPringBoot+Mybatis注解+thymeleaf整合练习

目录结构：

![image-20210713145529446](.\springboot\images\image-20210713143117611.png)

##### 1、创建数据库

```
create database db_spring;
use db_spring;
create table user(
id int primary key auto_increment,
name varchar(20),
age int
);
```

##### 2、修改application.properties（用户名和密码需要换成自己的）

```
#thymeleaf 配置
spring.thymeleaf.mode=HTML5
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.servlet.content-type=text/html
#缓存设置为false, 这样修改之后马上生效，便于调试
spring.thymeleaf.cache=false
#数据库
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/db_spring?characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

注意：新版本的mysqldeURL设置与旧版本的不同,8.0版本的使用以下url和driver-class

```
#新版本的mysqldeURL设置和旧版本的不同,8.0版本的使用以下配置
spring.datasource.url=jdbc:mysql://localhost:3306/db_spring?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
9

```



##### 3、在pom.xml文件中添加如下依赖

```
 <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.0</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- pageHelper -->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper</artifactId>
            <version>4.1.6</version>
        </dependency>
    </dependencies>
```

##### 4、创建实体类User：

```
package com.inership.springproject.test1.entity;

public class User {
    private int id;
    private String name;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}


```

##### 

##### 5、增加UserMapper

```
package com.inership.springproject.test1.mapper;

import com.inership.springproject.test1.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    /*@Result是结果映射列表
    property是User类的属性名，colomn是数据库表的字段名
     */
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "age", column = "age")
    })
    //插入用户
    @Insert("INSERT INTO user(name, age) VALUES (#{name}, #{age})")
    void save(User user);

    //根据年龄查询用户
    @Select("SELECT * FROM user WHERE age = #{age}")
    List<User> select(int age);

    //根据年龄查询用户
    @Select("SELECT * FROM user ")
    List<User> findAll();

    //根据id删除用户
    @Delete("delete from user where id= #{id} ")
    void delete(int id);

    //根据id查找用户
    @Select("select * from user where id= #{id} ")
    User get(int id);

    //更新用户信息
    @Update("update user set name=#{name} where id=#{id} ")
    int update(User user);
}




```



##### 6、UserController类

```java
package com.inership.springproject.test1.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inership.springproject.test1.entity.User;
import com.inership.springproject.test1.mapper.UserMapper;
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

```

##### 7、新建【config】包，并在下面新建【PageHelperConfig】类

```
package com.wmyskxz.demo.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class PageHelperConfig {

    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        pageHelper.setProperties(p);
        return pageHelper;
    }
}
```

##### 8、list.html

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Thymeleaf快速入门-CRUD和分页实例</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>

<div style="width:500px;margin:20px auto;text-align: center">
    <table align='center' border='1' cellspacing='0'>
        <tr>
            <td>id</td>
            <td>name</td>
            <td>age</td>
            <td>编辑</td>
            <td>删除</td>
        </tr>
        <tr th:each="user:${page.list}">
            <td th:text="${user.id}"></td>
            <td th:text="${user.name}"></td>
            <td th:text="${user.age}"></td>
            <td><a th:href="@{/edit(id=${user.id})}">编辑</a></td>
            <td><a th:href="@{/delete(id=${user.id})}">删除</a></td>
        </tr>
    </table>
    <br/>
    <div>
        <a th:href="@{/list(start=0)}">[首 页]</a>
        <a th:href="@{/list(start=${page.getPageNum()-1})}">[上一页]</a>
        <a th:href="@{/list(start=${page.getPageNum()+1})}">[下一页]</a>
        <a th:href="@{/list(start=${page.getPages()})}">[末 页]</a>
    </div>
    <br/>
    <form action="add" method="post">
        name: <input name="name"/> <br/>
        age: <input name="age"/> <br/>
        <button type="submit">提交</button>
    </form>
</div>
</body>
</html>

```

##### 9、edit.html

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Thymeleaf快速入门-CRUD和分页实例</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<div style="margin:0px auto; width:500px">
    <form action="update" method="post">
        name: <input name="name" th:value="${user.name}"/> <br/>
        age: <input name="name" th:value="${user.age}"/> <br/>
        <input name="id" type="hidden" th:value="${user.id}"/>
        <button type="submit">提交</button>
    </form>
</div>
</body>

</html>

```

##### 10、主函数

```
package com.inership.springproject.test1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

@SpringBootApplication
@MapperScan(basePackages = "com.inership.springproject.test1.mapper",annotationClass = Repository.class)
public class Test1Application {

  public static void main(String[] args) {
    SpringApplication.run(Test1Application.class, args);
  }
}
```

注意：@MapperScan(basePackages = "com.inership.springproject.test1.mapper",annotationClass = Repository.class)中basePackages =“你的mapper所在包的路径”



##### 11、结果图：

网址：`http://localhost:8080/list`（端口号使用默认的8080，如果不是则自行修改）



![image-20210713144829586](.\springboot\images\image-20210713144829586.png)

#### 解决问题方案：

##### 1、注入userMapper出现红线报错

```
@Autowired //注入userMapper
private UserMapper userMapper;
```

参考文档：https://blog.csdn.net/weixin_38004638/article/details/88708354（使用里边的方法二）

##### 2、spring boot 下的mybatis入门

参考文档：https://blog.csdn.net/hju22/article/details/88197213

##### 3、Thymeleaf如何使用？如何使用mybatis实现CRUD？如何实现分页？

参考文档：https://www.jianshu.com/p/ac8201031334

