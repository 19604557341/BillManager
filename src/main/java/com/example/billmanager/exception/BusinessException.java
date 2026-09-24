package com.example.billmanager.exception;

import com.example.billmanager.enums.ErrorCode;

import java.io.Serial;

/**
 * 业务异常。
 *
 * <p>
 *     用于表示系统业务逻辑执行过程中产生的预期异常，
 *     例如数据不存在、数据重复、业务状态不允许等情况。
 * </p>
 *
 * <p>
 *     业务异常由全局异常处理器 {@code GlobalExceptionHandler}
 *     统一捕获并转换为项目规定的响应格式。
 * </p>
 *
 * <p>
 *     错误码统一使用 {@link ErrorCode} 枚举传入，
 *     不再直接书写 400 / 404 / 409 等数字魔法值，
 *     避免各处状态码含义不一致、写错难以排查的问题。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
public class BusinessException extends RuntimeException {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务异常状态码。
     */
    private final Integer code;

    /**
     * 创建业务异常（使用错误码枚举中的默认提示信息）。
     *
     * @param errorCode 业务错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMsg());
        this.code = errorCode.getCode();
    }

    /**
     * 创建业务异常（使用自定义提示信息）。
     *
     * @param errorCode 业务错误码枚举
     * @param message   业务异常提示信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 获取业务异常状态码。
     *
     * @return 业务异常状态码
     */
    public Integer getCode() {
        return code;
    }

}
