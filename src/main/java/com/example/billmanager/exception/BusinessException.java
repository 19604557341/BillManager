package com.example.billmanager.exception;
/**
 * 业务异常。
 *
 *<p>
 *     用于表示系统业务逻辑执行过程中产生的预期异常，
 *     例如数据不存在、数据重复、业务状态不允许等情况。
 *</p>
 *
 * <p>
 *     业务异常由全局异常处理器 {@code GlobalExceptionHandler}
 *     统一捕获并转换为项目规定的响应格式。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
public class BusinessException extends RuntimeException {

    /**
     * 业务异常状态码
     */
    private final Integer code;

    /**
     * 创建业务异常。
     *
     * @param code 业务异常状态码
     * @param message 业务异常提示信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取业务异常状态码
     *
     * @return 业务异常状态码
     */
    public Integer getCode() {
        return code;
    }


}
