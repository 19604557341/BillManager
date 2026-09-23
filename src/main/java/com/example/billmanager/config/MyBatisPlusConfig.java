package com.example.billmanager.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类。
 *
 * <p>
 *     注册 MyBatis-Plus 插件拦截器。
 *     分页查询（{@code selectPage}）依赖 {@link PaginationInnerInterceptor}
 *     才能生成 LIMIT 子句并执行 COUNT 查询，
 *     缺少该拦截器时分页会失效（查出全表数据且 total 恒为 0）。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
@Configuration
@MapperScan("com.example.billmanager.mapper")
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 插件拦截器。
     *
     * <p>
     *     当前注册了分页内部拦截器，数据库类型为 MySQL。
     *     设置单页最大条数上限，防止恶意超大分页请求。
     * </p>
     *
     * @return MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor paginationInnerInterceptor =
                new PaginationInnerInterceptor(DbType.MYSQL);

        // 单页最大条数限制，-1 为不限制，这里与 DTO 中 size 上限保持一致
        paginationInnerInterceptor.setMaxLimit(100L);

        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }
}
