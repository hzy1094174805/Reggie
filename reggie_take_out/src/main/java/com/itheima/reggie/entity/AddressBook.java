package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 地址管理
 * </p>
 *
 * @author ilovend
 * @since 2023-02-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("address_book")
@ApiModel(value="AddressBook对象", description="地址管理")
public class AddressBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "收货人")
    @TableField("consignee")
    private String consignee;

    @ApiModelProperty(value = "性别 0 女 1 男")
    @TableField("sex")
    private String sex;

    @ApiModelProperty(value = "手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "省级区划编号")
    @TableField("province_code")
    private String provinceCode;

    @ApiModelProperty(value = "省级名称")
    @TableField("province_name")
    private String provinceName;

    @ApiModelProperty(value = "市级区划编号")
    @TableField("city_code")
    private String cityCode;

    @ApiModelProperty(value = "市级名称")
    @TableField("city_name")
    private String cityName;

    @ApiModelProperty(value = "区级区划编号")
    @TableField("district_code")
    private String districtCode;

    @ApiModelProperty(value = "区级名称")
    @TableField("district_name")
    private String districtName;

    @ApiModelProperty(value = "详细地址")
    @TableField("detail")
    private String detail;

    @ApiModelProperty(value = "标签")
    @TableField("label")
    private String label;

    @ApiModelProperty(value = "默认 0 否 1是")
    @TableField("is_default")
    private Integer isDefault;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @ApiModelProperty(value = "修改人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    @ApiModelProperty(value = "是否删除")
    @TableField("is_deleted")
    private Integer isDeleted;


}
