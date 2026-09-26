package com.example.billmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.billmanager.dto.amount.BillStatisticsDTO;
import com.example.billmanager.dto.amount.BillTotalAmountDTO;
import com.example.billmanager.dto.amount.CategoryAmountDTO;
import com.example.billmanager.dto.amount.TrendAmountDTO;
import com.example.billmanager.entity.Bill;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 账单数据访问层。
 *
 * <p>
 * 基于 MyBatis-Plus {@link BaseMapper} 提供账单数据的基础
 * 增、删、改、查操作。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 统计日期范围内的总收入与总支出。
     *
     * <p>
     * 只统计未逻辑删除（{@code deleted = 0}）的账单；
     * 虽然 WHERE 已按 {@code billType} 过滤（实际只会命中其中一列），
     * SQL 仍同时汇总收入与支出两列，由服务层按需取用；
     * {@code COALESCE} 保证无匹配数据时返回 0 而不是 null。
     * </p>
     *
     * @param billStatisticsDTO 统计查询条件（日期范围、账单类型）
     * @return 总收入与总支出的汇总结果
     */
    @Select(
            """
            SELECT
                COALESCE(SUM(IF(bill_type = 'INCOME', bill_amount, 0)), 0) AS totalIncome,
                COALESCE(SUM(IF(bill_type = 'EXPENSE', bill_amount, 0)), 0) AS totalExpense
            FROM bill
            WHERE bill_date >= #{startDate}
            AND bill_date <= #{endDate}
            AND bill_type = #{billType}
            AND deleted = 0
            """
    )
    BillTotalAmountDTO selectTotalAmount(BillStatisticsDTO billStatisticsDTO);

    /**
     * 按分类分组统计日期范围内的账单总金额。
     *
     * <p>
     * 通过 LEFT JOIN 分类表取分类名称，且只关联启用状态（{@code status = 0}，
     * 见 {@code CategoryStatus#ENABLED}）的分类：
     * 分类被禁用或已删除时，对应账单金额仍会计入统计，但 {@code categoryName} 为 null，
     * 使用 LEFT JOIN（而非 INNER JOIN）正是为了保证这部分账单不会被丢掉。
     * 结果按总金额降序排列，便于前端直接展示分类排行。
     * </p>
     *
     * @param billStatisticsDTO 统计查询条件（日期范围、账单类型）
     * @return 各分类的金额汇总列表（按金额降序）
     */
    @Select(
            """
            SELECT
                b.category_id AS categoryId,
                c.category_name AS categoryName,
                b.bill_type AS categoryType,
                COALESCE(SUM(b.bill_amount), 0) AS totalAmount
            FROM bill b
            LEFT JOIN category c on b.category_id = c.category_id AND c.status = 0
            WHERE b.bill_date >= #{startDate}
            AND b.bill_date <= #{endDate}
            AND b.bill_type = #{billType}
            AND b.deleted = 0
            GROUP BY
                b.category_id, c.category_name, b.bill_type
            ORDER BY totalAmount DESC
            """
    )
    List<CategoryAmountDTO> selectCategoryTotalAmounrList(BillStatisticsDTO billStatisticsDTO);

    /**
     * 按日或按月分组统计日期范围内的收支趋势。
     *
     * <p>
     * 通过 {@code DATE_FORMAT + IF} 根据 {@code groupBy} 参数动态选择分组粒度：
     * "month" 时按月（yyyy-MM）分组，其余取值一律按日（yyyy-MM-dd）分组。
     * 只返回存在账单的日期分组，按日分组时的"无账单日期补 0"
     * 由服务层 {@code BillStatisticsService#buildTrendStatistics} 完成。
     * 结果按日期升序排列，与前端趋势图的 X 轴方向一致。
     * </p>
     *
     * @param billStatisticsDTO 统计查询条件（日期范围、账单类型、分组方式）
     * @return 各日期分组的收入/支出汇总列表（按日期升序）
     */
    @Select(
            """
            SELECT
                DATE_FORMAT(bill_date, IF(#{groupBy} = 'month', '%Y-%m', '%Y-%m-%d')) AS date,
                COALESCE(SUM(IF(bill_type = 'INCOME', bill_amount, 0)), 0) AS income,
                COALESCE(SUM(IF(bill_type = 'EXPENSE', bill_amount , 0)), 0) AS expense
            FROM bill
            WHERE bill_date >= #{startDate}
            AND bill_date <= #{endDate}
            AND bill_type = #{billType}
            AND deleted = 0
            GROUP BY date
            ORDER BY date
            """
    )
    List<TrendAmountDTO> selectTrendAmoubtDTOList(BillStatisticsDTO billStatisticsDTO);
}
