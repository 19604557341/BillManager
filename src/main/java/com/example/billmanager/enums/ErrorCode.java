package com.example.billmanager.enums;

import lombok.Getter;

/**
 * 业务错误码枚举。
 *
 * <p>
 * 将项目中散落在各处的状态码魔法值 {@code 400} / {@code 404} / {@code 409} / {@code 500}
 * 收敛为统一的错误码枚举，配合 {@code BusinessException} 使用：
 * </p>
 *
 * <pre>{@code
 * // 改造前：数字含义靠记忆，容易写混
 * throw new BusinessException(404, "账单不存在");
 *
 * // 改造后：语义明确，IDE 可自动补全、可"查找用法"
 * throw new BusinessException(ErrorCode.NOT_FOUND, "账单不存在");
 * }</pre>
 *
 * <p>
 * 错误码取值与 HTTP 状态码语义保持一致，
 * 便于前端按 code 统一处理（如 404 跳转、409 提示重复等）。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-24
 */
@Getter
public enum ErrorCode {

    /** 请求参数错误：参数校验失败、参数取值非法、业务状态不允许等 */
    BAD_REQUEST(400, "请求参数错误"),

    /** 资源不存在：账单、分类等数据未找到 */
    NOT_FOUND(404, "资源不存在"),

    /** 数据冲突：分类重名等违反唯一性约束的业务场景 */
    CONFLICT(409, "数据冲突"),

    /** 系统内部错误：未预期异常的兜底错误码 */
    INTERNAL_ERROR(500, "系统内部错误");

    /**
     * 业务错误码。
     * <p>
     * 与统一响应对象 {@code Result} 中的 {@code code} 字段对应。
     * </p>
     */
    private final Integer code;

    /**
     * 默认错误提示信息。
     * <p>
     * 当抛出异常时未指定具体提示信息，则使用该默认文案。
     * </p>
     */
    private final String defaultMsg;

    /**
     * 构造方法。
     *
     * @param code       业务错误码
     * @param defaultMsg 默认错误提示信息
     */
    ErrorCode(Integer code, String defaultMsg) {
        this.code = code;
        this.defaultMsg = defaultMsg;
    }
}
