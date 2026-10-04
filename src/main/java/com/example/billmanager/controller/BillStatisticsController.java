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
 * 注意：统计条件（日期范围、分组方式）通过 Query String 传递，
 * 不再包含账单类型——一次请求同时返回收支总额、结余、收支两条趋势线与全部分类，
 * 前端按需取用，无需为"总结余"分别发送收入、支出两次请求。
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
     * 请求参数先经过 {@code @Valid} 完成 JSR-303 基础校验（起止日期必填）；
     * 账单类型不再作为查询条件，收支数据一次性返回。
     * </p>
     *
     * @param billStatisticsDTO 统计查询条件（日期范围、趋势分组方式）
     * @return 统一响应格式包装的统计结果
     */
    @GetMapping("/statistics")
    public Result<BillStatisticsVO> billStatisticsVO(@Valid BillStatisticsDTO billStatisticsDTO) {
        BillStatisticsVO vo = billStatisticsService.getStatistics(billStatisticsDTO);
        return Result.success("查询成功", vo);
    }
}
