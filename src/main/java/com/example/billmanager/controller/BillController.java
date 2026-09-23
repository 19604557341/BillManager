package com.example.billmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.billmanager.dto.bill.BillQueryDTO;
import com.example.billmanager.vo.BillPageVO;
import com.example.billmanager.vo.Result;
import com.example.billmanager.entity.Bill;
import com.example.billmanager.service.BillService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 账单控制器。
 * <p>
 *     负责处理账单相关的 HTTP 请求，
 *     接收并校验前端请求参数，然后调用账单业务层完成具体业务处理。
 * </p>
 *
 * <p>
 *     Controller 层不直接处理数据库操作和具体业务逻辑，
 *     以保证控制层职责单一。
 * </p>
 * @author 白麝花生
 * @since 2026-09-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    /**
     * 账单业务接口。
     */
    private final BillService billService;

    /**
     * 根据账单ID查询账单详情。
     * <p>
     *     根据路径中的账单ID查询对应账单。
     *     如果账单不存在，由业务层抛出业务异常，
     *     再由全局异常处理器统一处理。
     * </p>
     * @param billId 账单ID
     * @return 账单详情
     */
    @GetMapping("/{billId}")
    public Result<Bill> getBillById(
            @PathVariable @NotNull(message = "账单ID不能为空") Long billId
    ) {
        Bill bill = billService.getBillById(billId);
        return Result.success("查询成功", bill);
    }

    /**
     * 分页查询账单列表。
     * <p>
     *     根据指定的页码和每页数据量查询账单列表，
     *     避免一次性加载全部账单数据。
     * </p>
     * @param billQueryDTO billQueryDTO
     * @return 分页账单数据
     */
    @GetMapping("/page")
    public Result<IPage<BillPageVO>> getBillPage(@Valid BillQueryDTO billQueryDTO) {
        IPage<BillPageVO> billIPage = billService.getBillPage(billQueryDTO);

        return Result.success("查询成功", billIPage);
    }
}
