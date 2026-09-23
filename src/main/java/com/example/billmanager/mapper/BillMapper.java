package com.example.billmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.billmanager.dto.bill.BillQueryDTO;
import com.example.billmanager.entity.Bill;
import com.example.billmanager.vo.BillPageVO;
import org.apache.ibatis.annotations.Param;
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
}
