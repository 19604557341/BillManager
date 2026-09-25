package com.example.billmanager.exception;

import com.example.billmanager.enums.ErrorCode;
import com.example.billmanager.vo.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 *
 * <p>
 * 统一处理系统运行过程中产生的异常，
 * 将异常信息转换为项目统一的 {@link com.example.billmanager.vo.Result} 返回格式，
 * 避免 Spring 默认异常信息直接暴露给前端。
 * </p>
 *
 * <p>
 * 当前处理的异常类型：
 * <ul>
 *     <li>请求体参数校验异常（{@code MethodArgumentNotValidException}）；</li>
 *     <li>请求参数约束校验异常（{@code ConstraintViolationException}）；</li>
 *     <li>方法级参数校验异常（{@code HandlerMethodValidationException}）；</li>
 *     <li>请求体解析异常（{@code HttpMessageNotReadableException}，如枚举字段传入非法值）；</li>
 *     <li>业务异常（{@code BusinessException}）。</li>
 * </ul>
 * 后续可以继续扩展数据库异常、系统未知异常等。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求体参数校验异常。
     *
     * <p>
     * 当 Controller 方法参数使用 {@code @Valid} 进行校验，
     * 且 DTO 中的校验规则不满足时，
     * Spring 会抛出 {@link MethodArgumentNotValidException}。
     * </p>
     *
     * @param methodArgumentNotValidException 请求参数校验异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        // 收集所有字段校验错误：字段名 -> 错误提示信息
        methodArgumentNotValidException
                .getBindingResult()
                .getFieldErrors()
                .forEach(fieldError ->
                        errors.put(
                                fieldError.getField(),
                                fieldError.getDefaultMessage()
                        ));

        return Result.error(ErrorCode.BAD_REQUEST.getCode(), "参数校验失败", errors);
    }

    /**
     * 处理方法参数校验异常。
     *
     * <p>
     * 主要用于处理 {@code @RequestParam}、
     * {@code @PathVariable} 等参数的校验异常。
     * </p>
     *
     * @param constraintViolationException 参数校验异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Map<String, String>> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        // 收集所有约束校验错误：参数路径 -> 错误提示信息
        constraintViolationException
                .getConstraintViolations()
                .forEach(constraintViolation ->
                        errors.put(
                                constraintViolation.getPropertyPath().toString(),
                                constraintViolation.getMessage()
                        ));

        return Result.error(ErrorCode.BAD_REQUEST.getCode(), "参数校验失败", errors);
    }

    /**
     * 处理方法级参数校验异常。
     *
     * <p>
     * Spring Framework 6.1+ 中，当 Controller 类标注 {@code @Validated}，
     * 且 {@code @PathVariable}、{@code @RequestParam} 等简单参数上的
     * 约束注解（如 {@code @NotNull}）校验失败时，
     * 抛出的是 {@link HandlerMethodValidationException}，
     * 而不再是 {@link ConstraintViolationException}。
     * </p>
     *
     * @param exception 方法级参数校验异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Result<Map<String, String>> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        // 收集所有方法级参数校验错误：参数名 -> 错误提示信息
        exception.getParameterValidationResults().forEach(validationResult ->
                validationResult.getResolvableErrors().forEach(resolvableError ->
                        errors.put(
                                validationResult.getMethodParameter().getParameterName(),
                                resolvableError.getDefaultMessage()
                        )));

        return Result.error(ErrorCode.BAD_REQUEST.getCode(), "参数校验失败", errors);
    }

    /**
     * 处理请求体解析异常。
     *
     * <p>
     * <b>为什么枚举字段需要单独处理：</b>
     * {@code BillCreatedDTO} / {@code BillUpdateDTO} 中的 {@code billType} 字段
     * 已从 {@code String} 改为 {@code BillType} 枚举类型。
     * 当前端传入非法值（如 {@code "XXX"}）时，错误发生在 Jackson
     * 将 JSON 反序列化为 DTO 对象的阶段，此时对象尚未构建成功，
     * <b>JSR-303 参数校验（{@code @NotNull}、{@code @Pattern} 等）根本没有机会执行</b>，
     * 因此不会抛出 {@code MethodArgumentNotValidException}，
     * 而是抛出 {@link HttpMessageNotReadableException}。
     * </p>
     *
     * <p>
     * 若不在此处捕获，Spring 会把默认的错误响应（含后端类名、堆栈片段等内部信息）
     * 直接返回给前端，既暴露了实现细节，也不符合项目统一的 {@code Result} 响应格式。
     * </p>
     *
     * <p>
     * 处理策略：
     * <ul>
     *     <li>若根因是 {@link InvalidFormatException}（字段值格式/取值非法），
     *     解析出字段名与非法值；目标类型为枚举时，额外列出全部合法取值，
     *     便于前端直接提示用户；</li>
     *     <li>其他情况（请求体不是合法 JSON、字段类型完全不匹配等）
     *     统一返回"请求体格式错误"，不透出 Jackson 的原始报错。</li>
     * </ul>
     * </p>
     *
     * @param exception 请求体解析异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();

        // 根因为"字段值格式非法"时，提取字段名与非法值，给出精确提示
        // 注意：本项目为 Spring Boot 4，默认使用 Jackson 3，
        // 其异常类包名为 tools.jackson.databind（Jackson 2 是 com.fasterxml.jackson.databind），不要导错包
        if (cause instanceof InvalidFormatException invalidFormatException) {
            // 拼接出错字段的属性路径（嵌套对象时为 a.b 形式），根路径的 propertyName 为 null 需过滤
            // 注意：Jackson 3 中该方法由 Jackson 2 的 getFieldName() 改名为 getPropertyName()
            String fieldName = invalidFormatException.getPath().stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));
            Object invalidValue = invalidFormatException.getValue();
            Class<?> targetType = invalidFormatException.getTargetType();

            // 目标类型是枚举（如 BillType）：把全部合法取值列出来，前端可直接展示给用户
            if (targetType != null && targetType.isEnum()) {
                String allowedValues = Arrays.stream(targetType.getEnumConstants())
                        .map(String::valueOf)
                        .collect(Collectors.joining("、"));
                return Result.error(ErrorCode.BAD_REQUEST.getCode(),
                        String.format("参数 %s 取值无效：%s，只允许：%s", fieldName, invalidValue, allowedValues),
                        null);
            }

            // 非枚举类型（如日期、数字格式错误）：只提示字段与非法值
            return Result.error(ErrorCode.BAD_REQUEST.getCode(),
                    String.format("参数 %s 格式错误：%s", fieldName, invalidValue),
                    null);
        }

        // 其他解析失败（请求体不是合法 JSON 等）：返回兜底提示，避免暴露后端内部报错
        return Result.error(ErrorCode.BAD_REQUEST.getCode(), "请求体格式错误，无法解析", null);
    }

    /**
     * 处理业务异常。
     *
     * <p>
     *     业务层通过 {@link BusinessException} 表示预期的业务异常，
     *     由全局异常处理器统一转换为项目规定的响应格式。
     * </p>
     *
     * @param businessException 业务异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException businessException) {
        return Result.error(businessException.getCode(), businessException.getMessage(), null);
    }
}
