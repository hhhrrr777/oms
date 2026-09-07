package cn.qihangerp.api.controller;

import cn.qihangerp.common.*;
import cn.qihangerp.model.bo.MerchantAddBo;
import cn.qihangerp.model.entity.ErpMerchant;
import cn.qihangerp.model.query.MerchantQuery;
import cn.qihangerp.service.OShopService;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.security.common.SecurityUtils;
import cn.qihangerp.service.*;
import cn.qihangerp.enums.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;

@AllArgsConstructor
@RestController
@RequestMapping("/api/oms-api/merchant")
public class MerchantController extends BaseController {
    private final ErpMerchantService merchantService;
    private final ErpWarehouseService warehouseService;
    private final ISysUserService userService;
    private final OShopService shopService;

    @GetMapping("/pageList")
    public TableDataInfo pageList(MerchantQuery bo, PageQuery pageQuery) {
        // 管理员
        PageResult<ErpMerchant> pageResult = merchantService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    /**
     * 全部商户（仅总部查询）
     *
     * @return
     */
    @GetMapping("/list_all")
    public AjaxResult listAll() {
        var list = merchantService.list(new LambdaQueryWrapper<ErpMerchant>().eq(ErpMerchant::getStatus, 0));
        long count = list.stream().filter(x -> x.getId() == 0).count();
        if (count == 0) {
            ErpMerchant self = new ErpMerchant();
            self.setId(0L);
            self.setName("总部自营");
            list.add(0, self);
        }
        return AjaxResult.success(list);
    }


    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(merchantService.getById(id));
    }


    @PostMapping("/add")
    public AjaxResult add(@RequestBody MerchantAddBo bo) {
        try {
            ResultVo<ErpMerchant> resultVo = merchantService.add(bo, getUsername());
            if (resultVo.getCode() != ResultVoEnum.SUCCESS.getIndex()) {
                return AjaxResult.error(resultVo.getCode(), resultVo.getMsg());
            }
            ErpMerchant merchant = resultVo.getData();
            return AjaxResult.success(merchant == null ? null : merchant.getId());
        } catch (DataIntegrityViolationException ex) {
            logger.error("新增商户数据校验失败", ex);
            return AjaxResult.error(HttpStatus.CONFLICT, "商户数据保存失败，请检查数据库字段约束");
        }
    }

    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody ErpMerchant bo) {
        if (bo.getId() == null) return AjaxResult.error("缺少参数：id");
        bo.setUpdateBy(getUsername());
        bo.setUpdateTime(new Date());
        merchantService.updateById(bo);
        return toAjax(1);
    }

    @DeleteMapping("/del/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        if (ids == null) return AjaxResult.error("缺少参数：id");
        boolean containsOne = Arrays.asList(ids).contains(1L);
        if (containsOne) {
            return AjaxResult.error("不能删除自营商户数据");
        }

        merchantService.removeBatchByIds(Arrays.stream(ids).toList());
        return AjaxResult.success();
    }


}
