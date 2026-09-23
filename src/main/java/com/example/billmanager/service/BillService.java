package com.example.billmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.billmanager.dto.bill.BillQueryDTO;
import com.example.billmanager.entity.Bill;
import com.example.billmanager.vo.BillPageVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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

    Bill getBillById(Long billId);
    /**
     * 分页查询账单列表。
     *
     * <p>
     * 根据页码和每页数据量查询账单，
     * 避免一次性加载全部账单数据。
     * </p>
     *
     * @param page 页码，从1开始
     * @param size 每页数据量
     * @return 账单分页结果
     */
    IPage<BillPageVO> getBillPage(@Valid BillQueryDTO billQueryDTO);
}
