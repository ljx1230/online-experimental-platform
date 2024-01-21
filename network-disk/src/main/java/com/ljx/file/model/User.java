package com.ljx.file.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @Author: ljx
 * @Date: 2023/12/19 16:27
 */
@Data
@Table(name = "user")
@Entity
@TableName("user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition = "bigint(20) comment '用户id'")
    private Long userId;

    @Column(columnDefinition = "varchar(30) comment '用户名'")
    private String username;

    @Column(columnDefinition = "varchar(35) comment '密码'")
    private String password;

    @Column(columnDefinition = "varchar(15) comment '手机号码'")
    private String telephone;

    @Column(columnDefinition = "varchar(20) comment '盐值'")
    private String salt;

    @Column(columnDefinition = "varchar(30) comment '注册时间'")
    private String registerTime;
}
