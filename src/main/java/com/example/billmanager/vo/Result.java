package com.example.billmanager.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装类。
 *
 * <p>
 * 项目中所有接口统一使用本类作为返回格式，
 * 保证前端接收到的数据结构一致，便于统一处理。
 * </p>
 *
 * <p>
 * 包含三个字段：
 * <ul>
 *     <li>{@code code}：业务状态码，200 表示成功，其余表示各类错误；</li>
 *     <li>{@code message}：提示信息，用于向用户展示操作结果；</li>
 *     <li>{@code data}：业务数据，泛型，随具体接口而定。</li>
 * </ul>
 * </p>
 *
 * @param <T> 业务数据类型
 * @author 白麝花生
 * @since 2026-09-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 业务状态码。
     * <p>
     * 200 表示成功，其余状态码表示对应的业务或系统错误。
     * </p>
     */
    private Integer code;

    /**
     * 提示信息。
     * <p>
     * 用于向用户展示本次操作的结果说明。
     * </p>
     */
    private String message;

    /**
     * 业务数据。
     * <p>
     * 泛型字段，承载接口返回的具体数据；无数据时为 null。
     * </p>
     */
    private T data;

    /**
     * 构造成功响应。
     *
     * @param message 成功提示信息
     * @param data    业务数据
     * @param <T>     业务数据类型
     * @return 状态码为 200 的成功响应结果
     */
    public static <T> Result<T> success(String message, T data){
        Result<T> result = new Result<>();

        result.setCode(200);
        result.setMessage(message);
        result.setData(data);

        return result;
    }

    /**
     * 构造错误响应。
     *
     * @param code    业务错误状态码
     * @param message 错误提示信息
     * @param data    附加数据（如字段校验错误明细），无数据时传 null
     * @param <T>     业务数据类型
     * @return 携带指定状态码与提示信息的错误响应结果
     */
    public static <T> Result<T> error(Integer code, String message, T data){
        Result<T> result = new Result<>();

        result.setCode(code);
        result.setMessage(message);
        result.setData(data);

        return result;
    }
}
