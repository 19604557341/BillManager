package com.example.billmanager.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器。
 *
 * <p>
 * 用于在插入、更新记录时自动填充实体中标注了
 * {@code @TableField(fill = ...)} 的时间字段，
 * 避免在每个业务方法中手动设置创建时间和修改时间。
 * </p>
 *
 * <p>
 * 当前填充规则：
 * <ul>
 *     <li>插入时：填充 {@code createdTime} 和 {@code updateTime}；</li>
 *     <li>更新时：填充 {@code updateTime}。</li>
 * </ul>
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-13
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充。
     *
     * <p>
     * 创建时间与修改时间均填充为当前时间；
     * 使用 {@code strictInsertFill} 严格模式，
     * 仅当字段为 null 时才填充，不会覆盖业务代码已设置的值。
     * </p>
     *
     * @param metaObject 元对象，封装了当前正在插入的实体
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        LocalDateTime now = LocalDateTime.now();

        this.strictInsertFill(
                metaObject,
                "createdTime",
                LocalDateTime.class,
                now
        );

        this.strictInsertFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                now
        );
    }

    /**
     * 更新时自动填充。
     *
     * <p>
     * 将修改时间填充为当前时间。
     * </p>
     *
     * @param metaObject 元对象，封装了当前正在更新的实体
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );
    }
}
