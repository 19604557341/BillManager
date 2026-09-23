package com.example.billmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("bill")
public class Bill {

    @TableId(value = "bill_id", type = IdType.ASSIGN_ID)
    private Long billId;

    private BigDecimal billAmount;

    private String billType;

    private Long categoryId;

    private String remark;

    private LocalDate billDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

}
