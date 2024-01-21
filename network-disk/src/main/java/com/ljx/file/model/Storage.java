package com.ljx.file.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/24 21:56
 */
@Data
@Table(name = "storage")
@Entity
@TableName("storage")
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)")
    private Long storageId;

    @Column(columnDefinition="bigint(20)")
    private Long userId;

    @Column(columnDefinition="bigint(20)")
    private Long storageSize;
}
