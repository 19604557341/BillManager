package com.example.billmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.billmanager.dto.bill.BillCreatedDTO;
import com.example.billmanager.dto.bill.BillQueryDTO;
import com.example.billmanager.dto.bill.BillUpdateDTO;
import com.example.billmanager.entity.Bill;
import com.example.billmanager.entity.Category;
import com.example.billmanager.enums.BillType;
import com.example.billmanager.enums.CategoryStatus;
import com.example.billmanager.enums.ErrorCode;
import com.example.billmanager.exception.BusinessException;
import com.example.billmanager.mapper.BillMapper;
import com.example.billmanager.mapper.CategoryMapper;
import com.example.billmanager.service.BillService;
import com.example.billmanager.vo.BillPageVO;
import org.springframework.stereotype.Service;

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

    /**
     * 分类数据访问层。
     * <p>
     * 用于查询账单关联的分类信息（分类名称组装、新增账单时的分类校验）。
     * </p>
     */
    private final CategoryMapper categoryMapper;

    /**
     * 构造方法注入分类数据访问层。
     *
     * @param categoryMapper 分类数据访问层
     */
    public BillServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 根据账单ID查询账单详情。
     * <p>
     * 账单不存在时抛出业务异常，
     * 由全局异常处理器统一转换为错误响应。
     * </p>
     *
     * @param billId 账单ID
     * @return 账单详情
     * @throws BusinessException 当账单不存在时抛出
     */
    @Override
    public Bill getBillById(Long billId) {
        Bill bill = baseMapper.selectById(billId);

        if (bill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "账单不存在");
        }

        return bill;
    }

    /**
     * 分页查询账单。
     * <p>
     * 根据前端传入的查询条件动态构造查询条件，
     * 然后使用 MyBatis-Plus 执行分页查询。
     * </p>
     * <p>
     * 查询完成后，根据当前页账单中的分类ID批量查询分类信息，
     * 并将分类名称组装到 {@link BillPageVO} 中。
     * </p>
     *
     * @param billQueryDTO 账单分页查询条件
     * @return 账单分页查询结果
     */
    @Override
    public IPage<BillPageVO> getBillPage(BillQueryDTO billQueryDTO) {

        Page<Bill> page = new Page<>(billQueryDTO.getPage(), billQueryDTO.getSize());

        // 账单类型为可选条件：DTO 已改为枚举类型，非法值在 Jackson 反序列化阶段即被拦截，
        // 此处直接取值参与过滤；未传入时为 null，不参与过滤
        BillType billTypeFilter = billQueryDTO.getBillType();

        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(billTypeFilter != null, Bill::getBillType, billTypeFilter)
                .eq(billQueryDTO.getCategoryId() != null, Bill::getCategoryId, billQueryDTO.getCategoryId())
                .ge(billQueryDTO.getStartDate() != null, Bill::getBillDate, billQueryDTO.getStartDate())
                .le(billQueryDTO.getEndDate() != null, Bill::getBillDate, billQueryDTO.getEndDate())
                .orderByDesc(Bill::getBillDate)
                .orderByDesc(Bill::getCreatedTime);

        IPage<Bill> billIPage = baseMapper.selectPage(page, queryWrapper);

        // 当前页无数据时直接返回空分页对象，避免后续无意义的分类查询
        if (billIPage.getRecords().isEmpty()) {
            return new Page<>(billIPage.getCurrent(), billIPage.getSize(), billIPage.getTotal());
        }

        // 收集当前页账单涉及的分类ID（去重、去 null）
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

    /**
     * 根据分类ID列表批量查询分类信息。
     * <p>
     * 使用 IN 查询一次性获取当前页账单涉及的所有分类，
     * 避免在循环中逐条查询数据库（N+1 查询问题）。
     * </p>
     *
     * @param categoryIds 分类ID列表（已去重、去 null）
     * @return 分类ID -> 分类实体 的映射；入参为空时返回空 Map
     */
    private Map<Long, Category> getCategoryMap(List<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .in(Category::getCategoryId, categoryIds);

        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        Map<Long, Category> categoryMap = new HashMap<>();

        for (Category category : categoryList) {
            categoryMap.put(category.getCategoryId(), category);
        }

        return categoryMap;
    }

    /**
     * 将账单实体转换为分页展示对象。
     * <p>
     * 复制账单基础字段，并根据分类ID从分类映射中
     * 取出分类名称一并组装到 VO 中；
     * 分类不存在时分类名称保持为 null，不影响账单数据返回。
     * </p>
     *
     * @param bill        账单实体
     * @param categoryMap 分类ID -> 分类实体 的映射
     * @return 账单分页展示对象
     */
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

        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }

        return vo;
    }

    /**
     * 新增账单。
     * <p>
     * 新增前根据分类ID查询分类信息，并进行业务校验：
     *     <ol>
     *         <li>分类必须存在，否则抛出 404 业务异常；</li>
     *         <li>分类必须处于启用状态（status=1），已禁用分类不允许记账；</li>
     *         <li>分类类型必须与账单类型一致（如收入账单只能选择收入分类），
     *             防止出现"收入账单挂支出分类"的脏数据。</li>
     *     </ol>
     * </p>
     * <p>
     *     提前在业务层校验分类，可以避免无效数据直接落库时
     *     触发数据库外键约束异常（表现为 500 系统错误），
     *     转而返回语义明确的业务错误提示。
     * </p>
     * <p>
     *     账单ID由 MyBatis-Plus 雪花算法自动生成（{@code IdType.ASSIGN_ID}），
     *     创建时间、修改时间由 {@code MyMetaObjectHandler} 自动填充，
     *     无需在此手动设置。
     * </p>
     *
     * @param billCreatedDTO 账单新增请求参数（基础参数校验已在控制层完成）
     * @return 新增成功后的账单信息（包含系统生成的账单ID、创建时间等）
     * @throws BusinessException 当分类不存在、分类已禁用或分类类型与账单类型不匹配时抛出
     */
    @Override
    public Bill createBill(BillCreatedDTO billCreatedDTO) {

        // 查询账单所选分类，校验分类的合法性
        Category category = categoryMapper.selectById(billCreatedDTO.getCategoryId());

        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "所选分类不存在");
        }

        if (!Objects.equals(category.getStatus(), CategoryStatus.ENABLED.getCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "所选分类已禁用，请重新选择");
        }

        // DTO 中账单类型已是枚举，取值合法性由 Jackson 反序列化与 @NotNull 校验保证，直接使用
        BillType billType = billCreatedDTO.getBillType();

        if (billType != null && !Objects.equals(category.getCategoryType(), billType.getCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "分类类型与账单类型不匹配");
        }

        // 将 DTO 中的字段复制到账单实体
        Bill bill = new Bill();

        bill.setBillAmount(billCreatedDTO.getBillAmount());
        bill.setBillType(billType);
        bill.setCategoryId(billCreatedDTO.getCategoryId());
        bill.setRemark(billCreatedDTO.getRemark());
        bill.setBillDate(billCreatedDTO.getBillDate());

        // 插入数据库，插入成功后实体会回填自动生成的账单ID及填充的时间字段
        baseMapper.insert(bill);

        return bill;
    }

    /**
     * 根据账单ID修改账单。
     * <p>
     * 修改前先根据账单ID查询账单，账单不存在时直接抛出 404 业务异常，
     * 避免对不存在的账单执行无意义的分类校验和更新操作。
     * </p>
     * <p>
     * 然后对账单新选择的分类进行业务校验（与新增账单的校验规则一致）：
     *     <ol>
     *         <li>分类必须存在，否则抛出 404 业务异常；</li>
     *         <li>分类必须处于启用状态（status=1），已禁用分类不允许记账；</li>
     *         <li>分类类型必须与账单类型一致（如收入账单只能选择收入分类），
     *             防止出现"收入账单挂支出分类"的脏数据。</li>
     *     </ol>
     * </p>
     * <p>
     *     校验通过后将 DTO 中的字段覆盖到原账单实体并执行更新，
     *     修改时间（updateTime）由 {@code MyMetaObjectHandler} 在更新时自动填充，
     *     无需在此手动设置。
     * </p>
     *
     * @param billId        账单ID
     * @param billUpdateDTO 账单修改请求参数（基础参数校验已在控制层完成）
     * @return 修改成功后的账单信息
     * @throws BusinessException 当账单不存在、分类不存在、分类已禁用或分类类型与账单类型不匹配时抛出
     */
    @Override
    public Bill updateBillById(Long billId, BillUpdateDTO billUpdateDTO) {

        // 先查询待修改的账单，账单不存在时直接返回 404，不再执行后续校验
        Bill bill = baseMapper.selectById(billId);

        if (bill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "账单不存在");
        }

        // 查询账单新选择的分类，校验分类的合法性
        Category category = categoryMapper.selectById(billUpdateDTO.getCategoryId());

        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "所选分类不存在");
        }

        if (!Objects.equals(category.getStatus(), CategoryStatus.ENABLED.getCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "所选分类已禁用，请重新选择");
        }

        // DTO 中账单类型已是枚举，取值合法性由 Jackson 反序列化与 @NotNull 校验保证，直接使用
        BillType billType = billUpdateDTO.getBillType();

        if (billType != null && !Objects.equals(category.getCategoryType(), billType.getCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "分类类型与账单类型不匹配，请重新选择");
        }

        // 将 DTO 中的字段覆盖到原账单实体
        bill.setBillAmount(billUpdateDTO.getBillAmount());
        bill.setBillType(billType);
        bill.setRemark(billUpdateDTO.getRemark());
        bill.setCategoryId(billUpdateDTO.getCategoryId());
        bill.setBillDate(billUpdateDTO.getBillDate());

        // 更新数据库，修改时间由 MyMetaObjectHandler 自动填充
        baseMapper.updateById(bill);

        return bill;
    }

    /**
     * 根据账单ID删除账单。
     * <p>
     * 删除前先查询账单是否存在，
     * 账单不存在时直接抛出 404 业务异常，
     * 避免对不存在的账单执行无意义的删除操作。
     * </p>
     * <p>
     * 注意：由于 {@link Bill} 实体的 {@code deleted} 字段标注了
     * {@code @TableLogic}，此处执行的是<b>逻辑删除</b>而非物理删除，
     * MyBatis-Plus 会将删除操作自动转换为
     * {@code UPDATE bill SET deleted = 1 WHERE bill_id = ?}，
     * 数据库记录仍然保留，后续所有查询也会自动过滤已删除的账单。
     * </p>
     * <p>
     * 删除后会检查受影响行数，防止"查询后、删除前"
     * 账单被其他请求并发删除时仍然返回删除成功的假象。
     * </p>
     *
     * @param billId 账单ID
     * @throws BusinessException 当账单不存在（或已被并发删除）时抛出
     */
    @Override
    public void deleteBillById(Long billId) {

        // 先查询待删除的账单，账单不存在时直接抛出 404
        Bill bill = baseMapper.selectById(billId);

        if (bill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "账单不存在");
        }

        // 执行逻辑删除（@TableLogic 会将 DELETE 转换为 UPDATE deleted = 1）
        int rows = baseMapper.deleteById(billId);

        // 受影响行数为 0，说明账单在查询之后被其他请求并发删除，同样按 404 处理
        if (rows == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "账单不存在");
        }
    }
}