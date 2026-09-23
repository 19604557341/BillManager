package com.example.billmanager.exception;

import com.example.billmanager.vo.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.Map;

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

        methodArgumentNotValidException
                .getBindingResult()
                .getFieldErrors()
                .forEach(fieldError ->
                        errors.put(
                                fieldError.getField(),
                                fieldError.getDefaultMessage()
                        ));

        return Result.error(400, "参数校验失败", errors);
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
    public Result<Map<String, String>> handleConstrainViolationException(
            ConstraintViolationException constraintViolationException
    ){
        Map<String, String> errors = new LinkedHashMap<>();

        constraintViolationException
                .getConstraintViolations()
                .forEach(constraintViolation ->
                        errors.put(
                                constraintViolation.getPropertyPath().toString(),
                                constraintViolation.getMessage()
                        ));

        return Result.error(400, "参数校验失败", errors);
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

        exception.getParameterValidationResults().forEach(validationResult ->
                validationResult.getResolvableErrors().forEach(resolvableError ->
                        errors.put(
                                validationResult.getMethodParameter().getParameterName(),
                                resolvableError.getDefaultMessage()
                        )));

        return Result.error(400, "参数校验失败", errors);
    }

    /**
     * 处理业务异常。
     *
     *  <p>
     *     业务层通过 {@link BusinessException} 表示预期的业务异常，
     *     由全局异常处理器统一转换为项目规定的响应格式。
     * </p>
     * @param businessException 业务异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException businessException) {
        return Result.error(businessException.getCode(), businessException.getMessage(),null);
    }
}
