package com.xxxx.secondkill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author seb
 * @since 2026-07-18
 */
@Getter
@Setter
@ToString
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，手机号
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("nickname")
    private String nickname;

    /**
     * MD5(MD5(pass明文+固定salt)+salt)
     */
    @TableField("password")
    private String password;

    @TableField("salt")
    private String salt;

    @TableField("head")
    private String head;

    @TableField("register_date")
    private Date registerDate;

    @TableField("last_login_date")
    private Date lastLoginDate;

    @TableField("login_count")
    private Integer loginCount;

}
