package com.example.billmanager.controller;

import com.example.billmanager.dto.amount.BillStatisticsDTO;
import com.example.billmanager.service.BillStatisticsService;
import com.example.billmanager.vo.Result;
import com.example.billmanager.vo.amount.BillStatisticsVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账单统计控制器。
 *
 * <p>
 * 提供指定日期范围内的账单统计查询接口，一次请求返回三部分数据：
 * <ul>
 *     <li>总收入、总支出与结余；</li>
 *     <li>按分类汇总的金额与占比（用于饼图/环形图）；</li>
 *     <li>按日或按月的收支趋势（用于折线图/柱状图）。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 注意：统计条件（日期范围、账单类型、分组方式）通过 JSON 请求体传递，
 * 即 GET 请求搭配 {@code @RequestBody} 使用，
 * 要求客户端（前端 axios / ApiFox 等）支持 GET 携带请求体。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@RestController
@RequestMapping("/api/bills")
@Tag(name = "账单统计", description = "账单统计接口")
public class BillStatisticsController {

    /**
     * 账单统计服务。
     * <p>
     * 通过构造方法注入，负责统计数据的查询与组装。
     * </p>
     */
    private final BillStatisticsService billStatisticsService;

    /**
     * 构造方法（构造器注入）。
     *
     * @param billStatisticsService 账单统计服务
     */
    public BillStatisticsController(BillStatisticsService billStatisticsService) {
        this.billStatisticsService = billStatisticsService;
    }

    /**
     * 查询账单统计数据。
     *
     * <p>
     * 请求参数先经过 {@code @Valid} 完成 JSR-303 基础校验
     * （日期、账单类型必填）；{@code billType} 为枚举类型，
     * 非法取值在 Jackson 反序列化阶段即被拦截，
     * 由全局异常处理器统一返回 400 提示。
     * </p>
     *
     * @param billStatisticsDTO 统计查询条件（日期范围、账单类型、趋势分组方式）
     * @return 统一响应格式包装的统计结果
     */
    @GetMapping("/statistics")
    public Result<BillStatisticsVO> billStatisticsVO(@Valid BillStatisticsDTO billStatisticsDTO) {
        BillStatisticsVO vo = billStatisticsService.getStatistics(billStatisticsDTO);
        return Result.success("查询成功", vo);
    }
}
