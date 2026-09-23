package com.example.billmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 账单管理系统启动类。
 *
 * <p>
 * 基于 Spring Boot 构建的个人账单管理系统，
 * 提供账单的记录、查询、统计以及分类管理等功能。
 * </p>
 *
 * <p>
 * {@code @SpringBootApplication} 为组合注解，
 * 包含自动配置、组件扫描等能力，
 * 应用启动后会自动扫描本包及其子包下的所有组件。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-13
 */
@SpringBootApplication
public class BillManagerApplication {

    /**
     * 应用程序入口方法。
     *
     * @param args 命令行启动参数
     */
    static void main(String[] args) {
        SpringApplication.run(BillManagerApplication.class, args);
    }

}
