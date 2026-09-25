package com.example.billmanager.service;

import com.example.billmanager.dto.amount.BillStatisticsDTO;
import com.example.billmanager.dto.amount.BillTotalAmountDTO;
import com.example.billmanager.dto.amount.CategoryAmountDTO;
import com.example.billmanager.dto.amount.TrendAmountDTO;
import com.example.billmanager.mapper.BillMapper;
import com.example.billmanager.vo.amount.BillStatisticsVO;
import com.example.billmanager.vo.amount.CategoryStatisticsVO;
import com.example.billmanager.vo.amount.TrendStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 账单统计服务。
 *
 * <p>
 * 负责统计数据的查询与组装，整体流程分三步：
 * <ol>
 *     <li>通过 {@link BillMapper} 的三个聚合查询分别获取
 *     总收入/总支出、按分类汇总金额、按日/月分组的收支趋势；</li>
 *     <li>在内存中完成派生计算：结余（收入-支出）、分类占比、趋势日期补零；</li>
 *     <li>组装为 {@link BillStatisticsVO} 一次性返回给前端。</li>
 * </ol>
 * </p>
 *
 * <p>
 * 说明：本服务只做只读统计，不涉及数据修改，因此未加事务注解；
 * 聚合计算尽量下推到 SQL（SUM/GROUP BY）完成，
 * Java 侧仅处理 SQL 不便表达的逻辑（如按天补零、占比格式化）。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Service
@RequiredArgsConstructor
public class BillStatisticsService {

    /**
     * 账单数据访问层。
     * <p>
     * 用于执行总额、分类金额、收支趋势三个聚合统计查询。
     * </p>
     */
    private final BillMapper billMapper;

    /**
     * 金额空值兜底。
     *
     * <p>
     * 聚合查询在无匹配数据时 SUM 结果可能为 null（或整个结果对象为 null），
     * 统一转换为 0 后再参与减法、占比等计算，避免空指针异常。
     * </p>
     *
     * @param value 原始金额，可能为 null
     * @return 原值；为 null 时返回 {@link BigDecimal#ZERO}
     */
    private BigDecimal getValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 查询账单统计数据（服务入口）。
     *
     * <p>
     * 依次执行三个聚合查询并组装结果：
     * <ol>
     *     <li>总额统计：总收入、总支出，结余 = 总收入 - 总支出；</li>
     *     <li>分类统计：各分类总金额与占比（见 {@link #buildCategoryStatistics}）；</li>
     *     <li>趋势统计：按日/月分组的收支明细（见 {@link #buildTrendStatistics}）。</li>
     * </ol>
     * 同时把查询条件中的起止日期回写到 VO，便于前端展示统计区间。
     * </p>
     *
     * @param billStatisticsDTO 统计查询条件（日期范围、账单类型、趋势分组方式）
     * @return 组装完成的统计结果 VO
     */
    public BillStatisticsVO getStatistics(BillStatisticsDTO billStatisticsDTO) {

        // 1. 总额统计：查询结果可能为 null，取值时逐层兜底为 0
        BillTotalAmountDTO totalAmountDTO = billMapper.selectTotalAmount(billStatisticsDTO);

        BigDecimal totalIncome = getValue(totalAmountDTO == null ? null : totalAmountDTO.getTotalIncome());
        BigDecimal totalExpense = getValue(totalAmountDTO == null ? null : totalAmountDTO.getTotalExpense());
        BigDecimal totalBalance = totalIncome.subtract(totalExpense);

        // 2. 分类统计：各分类总金额、占比
        List<CategoryAmountDTO> categoryAmountDTOList = billMapper.selectCategoryTotalAmounrList(billStatisticsDTO);
        List<CategoryStatisticsVO> categoryStatisticsVOList = buildCategoryStatistics(categoryAmountDTOList);

        // 3. 趋势统计：按日分组时需补零，按分组方式走不同组装逻辑
        List<TrendAmountDTO> trendAmountDTOList = billMapper.selectTrendAmoubtDTOList(billStatisticsDTO);
        List<TrendStatisticsVO> trendStatisticsVOList = buildTrendStatistics(billStatisticsDTO, trendAmountDTOList);

        // 4. 组装最终 VO，回显统计起止日期
        BillStatisticsVO billStatisticsVO = new BillStatisticsVO();
        billStatisticsVO.setTotalIncome(totalIncome);
        billStatisticsVO.setTotalExpense(totalExpense);
        billStatisticsVO.setBalance(totalBalance);
        billStatisticsVO.setStartDate(billStatisticsDTO.getStartDate());
        billStatisticsVO.setEndDate(billStatisticsDTO.getEndDate());
        billStatisticsVO.setCategoryStatisticsVOList(categoryStatisticsVOList);
        billStatisticsVO.setTrendStatisticsVOList(trendStatisticsVOList);
        return billStatisticsVO;
    }

    /**
     * 组装收支趋势统计列表。
     *
     * <p>
     * 按分组方式分两种处理：
     * <ul>
     *     <li><b>按日（groupBy = "day"）</b>：SQL 只会返回有账单的日期，
     *     直接返回会导致折线图 X 轴断档。因此这里从开始日期到结束日期
     *     逐天生成完整日期序列，没有账单的日期收入/支出/结余统一补 0，
     *     保证前端拿到的是连续、等长的趋势数据；</li>
     *     <li><b>按月（groupBy = "month"）或其他值</b>：
     *     月份区间跨度不确定，不做补零，按 SQL 返回的月份原样转换。</li>
     * </ul>
     * </p>
     *
     * @param billStatisticsDTO  统计查询条件（提供起止日期与分组方式）
     * @param trendAmountDTOList 趋势聚合查询结果，可能为 null
     * @return 趋势统计 VO 列表（按日期升序）
     */
    private List<TrendStatisticsVO> buildTrendStatistics(BillStatisticsDTO billStatisticsDTO, List<TrendAmountDTO> trendAmountDTOList) {
        if (trendAmountDTOList == null) {
            trendAmountDTOList = new ArrayList<>();
        }

        // 以日期字符串为 key 建立索引，便于按天补零时 O(1) 查找；
        // 第三个参数 (a, b) -> a 表示 key 冲突时保留第一条（GROUP BY 后正常不会重复，属防御性写法）
        Map<String, TrendAmountDTO> trendAmountDTOMap = trendAmountDTOList.stream()
                .collect(Collectors.toMap(
                        TrendAmountDTO::getDate,
                        Function.identity(),
                        (a, b) -> a
                ));

        List<TrendStatisticsVO> trendStatisticsVOList = new ArrayList<>();

        // 按日分组：从开始日期逐天推进到结束日期，生成完整日期序列
        if ("day".equals(billStatisticsDTO.getGroupBy())) {
            LocalDate start = billStatisticsDTO.getStartDate();
            LocalDate end = billStatisticsDTO.getEndDate();

            while (!start.isAfter(end)) {
                // LocalDate.toString() 输出 yyyy-MM-dd，与 SQL 中 DATE_FORMAT 的 '%Y-%m-%d' 一致
                String date = start.toString();
                TrendAmountDTO item = trendAmountDTOMap.get(date);
                TrendStatisticsVO trendStatisticsVO = new TrendStatisticsVO();
                trendStatisticsVO.setDate(date);

                if (item != null) {
                    // 当天有账单：取查询结果并计算当日结余
                    BigDecimal income = getValue(item.getIncome());
                    BigDecimal expense = getValue(item.getExpense());

                    trendStatisticsVO.setIncome(income);
                    trendStatisticsVO.setExpense(expense);
                    trendStatisticsVO.setBalance(income.subtract(expense));
                }else {
                    // 当天没有账单：收入/支出/结余全部补 0，保证趋势数据连续
                    trendStatisticsVO.setIncome(BigDecimal.ZERO);
                    trendStatisticsVO.setExpense(BigDecimal.ZERO);
                    trendStatisticsVO.setBalance(BigDecimal.ZERO);
                }

                trendStatisticsVOList.add(trendStatisticsVO);
                start = start.plusDays(1);
            }
            return trendStatisticsVOList;
        }

        // 按月分组（或其他 groupBy 值）：不补零，按 SQL 返回的月份原样转换
        return trendAmountDTOList.stream().map(item -> {
            BigDecimal income = getValue(item.getIncome());
            BigDecimal expense = getValue(item.getExpense());

            TrendStatisticsVO trendStatisticsVO = new TrendStatisticsVO();
            trendStatisticsVO.setDate(item.getDate());
            trendStatisticsVO.setIncome(income);
            trendStatisticsVO.setExpense(expense);
            trendStatisticsVO.setBalance(income.subtract(expense));
            return trendStatisticsVO;
        }).toList();
    }

    /**
     * 组装分类统计列表（含占比计算）。
     *
     * <p>
     * 占比 = 该分类总金额 ÷ 所有分类金额合计 × 100：
     * <ul>
     *     <li>先以 4 位小数精度做除法（四舍五入），再乘 100，
     *     最后保留 2 位小数，避免除法无限循环小数抛
     *     {@code ArithmeticException}；</li>
     *     <li>合计为 0 时（查询区间内没有账单）占比直接置 0，避免除零；</li>
     *     <li>同时生成 {@code percentageText}（如 "12.34%"），
     *     前端可直接展示，无需再做格式化。</li>
     * </ul>
     * 注意：由于各分类占比分别四舍五入，合计可能不严格等于 100%（如 99.99%），
     * 属于常见的展示精度误差。
     * </p>
     *
     * @param categoryAmountDTOList 分类金额聚合查询结果，可能为 null 或空
     * @return 分类统计 VO 列表（按金额降序，与 SQL 排序一致）；无数据时返回空列表
     */
    private List<CategoryStatisticsVO> buildCategoryStatistics(List<CategoryAmountDTO> categoryAmountDTOList) {
        if (categoryAmountDTOList == null || categoryAmountDTOList.isEmpty()) {
            return new ArrayList<>();
        }

        // 所有分类金额合计，作为占比计算的分母
        BigDecimal total = categoryAmountDTOList.stream()
                .map(item -> getValue(item.getTotalAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return categoryAmountDTOList.stream().map(item -> {
            CategoryStatisticsVO categoryStatisticsVO = new CategoryStatisticsVO();
            categoryStatisticsVO.setCategoryId(item.getCategoryId());
            categoryStatisticsVO.setCategoryName(item.getCategoryName());
            categoryStatisticsVO.setCategoryType(item.getCategoryType());
            categoryStatisticsVO.setTotalAmount(getValue(item.getTotalAmount()));

            if (total.compareTo(BigDecimal.ZERO) == 0) {
                // 合计为 0：占比直接置 0，避免除零异常
                categoryStatisticsVO.setPercentage(BigDecimal.ZERO);
                categoryStatisticsVO.setPercentageText("0.00%");
            } else {
                // 先除（保留 4 位小数）再乘 100，最后保留 2 位小数并四舍五入
                BigDecimal percentage = getValue(item.getTotalAmount())
                        .divide(total, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);

                categoryStatisticsVO.setPercentage(percentage);
                categoryStatisticsVO.setPercentageText(percentage + "%");
            }
            return categoryStatisticsVO;
        }).toList();
    }
}
