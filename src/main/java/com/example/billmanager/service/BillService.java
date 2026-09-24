package com.example.billmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.billmanager.dto.bill.BillCreatedDTO;
import com.example.billmanager.dto.bill.BillQueryDTO;
import com.example.billmanager.dto.bill.BillUpdateDTO;
import com.example.billmanager.entity.Bill;
import com.example.billmanager.vo.BillPageVO;

/**
 * 账单业务接口。
 * <p>
 *     在 MyBatis-Plus 通用业务接口的基础上，
 *     对账单相关业务进行统一定义。
 * </p>
 * @author 白麝花生
 * @since 2026-09-14
 */
public interface BillService extends IService<Bill> {

    /**
     * 根据账单ID查询账单详情。
     *
     * <p>
     * 账单不存在时抛出业务异常，
     * 由全局异常处理器统一转换为错误响应。
     * </p>
     *
     * @param billId 账单ID
     * @return 账单详情
     */
    Bill getBillById(Long billId);

    /**
     * 分页查询账单列表。
     *
     * <p>
     * 根据页码和每页数据量查询账单，
     * 避免一次性加载全部账单数据。
     * </p>
     *
     * @param billQueryDTO 账单分页查询条件（页码、每页数量、账单类型、分类、日期范围）
     * @return 账单分页结果
     */
    IPage<BillPageVO> getBillPage(BillQueryDTO billQueryDTO);

    /**
     * 新增账单。
     *
     * <p>
     * 新增前校验分类是否存在、是否启用，
     * 以及分类类型与账单类型是否匹配；
     * 校验通过后将账单数据写入数据库。
     * </p>
     *
     * @param billCreatedDTO 账单新增请求参数（参数基础校验已在控制层完成）
     * @return 新增成功后的账单信息（包含系统生成的账单ID、创建时间等）
     */
    Bill createBill(BillCreatedDTO billCreatedDTO);

    /**
     * 根据账单ID修改账单。
     *
     * <p>
     * 修改前校验账单是否存在，以及新选择的分类是否存在、是否启用、
     * 分类类型与账单类型是否匹配；
     * 校验通过后将账单数据更新到数据库。
     * </p>
     *
     * @param billId        账单ID
     * @param billUpdateDTO 账单修改请求参数（参数基础校验已在控制层完成）
     * @return 修改成功后的账单信息
     */
    Bill updateBillById(Long billId, BillUpdateDTO billUpdateDTO);

    /**
     * 根据账单ID删除账单。
     *
     * <p>
     * 删除前校验账单是否存在，
     * 不存在时抛出业务异常；
     * 存在则执行逻辑删除（将 {@code deleted} 字段标记为 1，
     * 数据库记录仍然保留，后续查询会自动过滤已删除的账单）。
     * </p>
     *
     * @param billId 账单ID（非空校验已在控制层完成）
     * @throws com.example.billmanager.exception.BusinessException 当账单不存在时抛出
     */
    void deleteBillById(Long billId);
}
