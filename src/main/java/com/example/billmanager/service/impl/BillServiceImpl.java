package com.example.billmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.billmanager.dto.bill.BillQueryDTO;
import com.example.billmanager.entity.Bill;
import com.example.billmanager.entity.Category;
import com.example.billmanager.mapper.BillMapper;
import com.example.billmanager.mapper.CategoryMapper;
import com.example.billmanager.exception.BusinessException;
import com.example.billmanager.service.BillService;
import com.example.billmanager.vo.BillPageVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 账单业务实现类。
 *
 * <p>
 * 负责处理账单相关的业务逻辑。
 * 基础的数据库操作由 MyBatis-Plus 提供，
 * 具体业务规则在本类中进行统一处理。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    private final CategoryMapper categoryMapper;

    public BillServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 根据账单ID查询账单详情。
     * <p>
     *     账单不存在时抛出业务异常，
     *     由全局异常处理器统一转换为错误响应。
     * </p>
     * @param billId 账单ID
     * @return 账单详情
     */
    @Override
    public Bill getBillById(Long billId) {
        Bill bill = baseMapper.selectById(billId);

        if (bill == null) {
            throw new BusinessException(404, "账单不存在");
        }

        return bill;
    }

    /**分页查询账单。
     * <p>
     *     根据前端传入的查询条件动态构造查询条件，
     *     然后使用 MyBatis-Plus 执行分页查询。
     * </p>
     * <p>
     *     查询完成后，根据当前页账单中的分类ID批量查询分类信息，
     *     并将分类名称组装到 {@link BillPageVO} 中。
     * </p>
     * @param billQueryDTO 账单分页查询条件
     * @return 账单分页查询结果
     */
    @Override
    public IPage<BillPageVO> getBillPage(BillQueryDTO billQueryDTO) {

        Page<Bill> page = new Page<>(billQueryDTO.getPage(), billQueryDTO.getSize());

        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(StringUtils.hasText(billQueryDTO.getBillType()), Bill::getBillType, billQueryDTO.getBillType())
                .eq(billQueryDTO.getCategoryId() != null, Bill::getCategoryId, billQueryDTO.getCategoryId())
                 .ge(billQueryDTO.getStartDate() != null, Bill::getBillDate, billQueryDTO.getStartDate())
                .le(billQueryDTO.getEndDate() != null, Bill::getBillDate, billQueryDTO.getEndDate())
                .orderByDesc(Bill::getBillDate)
                .orderByDesc(Bill::getCreatedTime);

        IPage<Bill> billIPage = baseMapper.selectPage(page, queryWrapper);

        if (billIPage.getRecords().isEmpty()) {
            return new Page<>(billIPage.getCurrent(), billIPage.getSize(), billIPage.getTotal());
        }

        List<Long> categoryIds = billIPage.getRecords()
                .stream()
                .map(Bill::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, Category> categoryMap = getCategoryMap(categoryIds);

        List<BillPageVO> records = billIPage.getRecords()
                .stream()
                .map(bill -> convertToPageVO(bill, categoryMap))
                .toList();

        Page<BillPageVO> resultPage = new Page<>();
        resultPage.setCurrent(billIPage.getCurrent());
        resultPage.setSize(billIPage.getSize());
        resultPage.setTotal(billIPage.getTotal());
        resultPage.setRecords(records);

        return resultPage;
    }

    private Map<Long, Category> getCategoryMap(List<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .in(Category::getCategoryId, categoryIds);

        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        Map<Long, Category> categoryMap = new HashMap<>();

        for (Category category:categoryList) {
            categoryMap.put(category.getCategoryId(), category);
        }

        return categoryMap;
    }

    private BillPageVO convertToPageVO(Bill bill, Map<Long, Category> categoryMap) {
        BillPageVO vo = new BillPageVO();

        vo.setBillId(bill.getBillId());
        vo.setBillAmount(bill.getBillAmount());
        vo.setBillType(bill.getBillType());
        vo.setCategoryId(bill.getCategoryId());
        vo.setRemark(bill.getRemark());
        vo.setBillDate(bill.getBillDate());
        vo.setCreatedTime(bill.getCreatedTime());
        vo.setUpdateTime(bill.getUpdateTime());

        Category category = categoryMap.get(bill.getCategoryId());

        if(category != null) {
            vo.setCategoryName(category.getCategoryName());
        }

        return vo;
    }


}
