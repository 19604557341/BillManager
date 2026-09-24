package com.example.billmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 账单管理系统启动测试类。
 *
 * <p>
 * 用于验证 Spring 应用上下文能否正常加载，
 * 若 Bean 配置、数据库连接等存在问题，该测试会启动失败。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-13
 */
@SpringBootTest
class BillManagerApplicationTests {

    /**
     * 上下文加载测试。
     *
     * <p>
     * 空测试方法，仅依赖 {@code @SpringBootTest} 启动完整应用上下文，
     * 上下文启动失败时测试即失败。
     * </p>
     */
    @Test
    void contextLoads() {
    }

}
