package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.enums.HttpStatus;
import cn.qihangerp.mapper.ErpMerchantMapper;
import cn.qihangerp.model.bo.MerchantAddBo;
import cn.qihangerp.model.entity.ErpMerchant;
import cn.qihangerp.model.query.MerchantQuery;
import cn.qihangerp.service.ErpMerchantService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_tenant(租户用户表)】的数据库操作Service实现
* @createDate 2024-06-23 11:10:08
*/
@AllArgsConstructor
@Service
public class ErpMerchantServiceImpl extends ServiceImpl<ErpMerchantMapper, ErpMerchant>
    implements ErpMerchantService {
    private final ErpMerchantMapper mapper;

    @Override
    public PageResult<ErpMerchant> queryPageList(MerchantQuery bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpMerchant> queryWrapper = new LambdaQueryWrapper<ErpMerchant>()
                .eq(bo.getStatus()!=null,ErpMerchant::getStatus,bo.getStatus())
                .like(StringUtils.hasText(bo.getNumber()),ErpMerchant::getNumber,bo.getNumber())
                .like(StringUtils.hasText(bo.getUsci()),ErpMerchant::getUsci,bo.getUsci())
                .like(StringUtils.hasText(bo.getMobile()),ErpMerchant::getMobile,bo.getMobile())
                .eq(bo.getMerchantId()!=null,ErpMerchant::getId,bo.getMerchantId())
                .like(StringUtils.hasText(bo.getName()),ErpMerchant::getName,bo.getName());

        Page<ErpMerchant> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ErpMerchant selectUserByUserName(String userName) {
        List<ErpMerchant> scmDistributors = mapper.selectList(new LambdaQueryWrapper<ErpMerchant>()
                .eq(ErpMerchant::getLoginName, userName)
                .eq(ErpMerchant::getDelFlag, "0"));
        if(scmDistributors == null || scmDistributors.size()==0)
            return null;
        else
            return scmDistributors.get(0);
    }

    @Override
    public void updateByUserId(ErpMerchant tenant, Long userId) {
        tenant.setId(userId);
        mapper.updateById(tenant);
//        mapper.update(tenant,new LambdaQueryWrapper<OmsTenant>().eq(OmsTenant::getId,userId));
    }

    @Override
    @Transactional
    public ResultVo<ErpMerchant> add(MerchantAddBo bo, String createBy) {
        String validationMessage = validateAddRequest(bo);
        if (validationMessage != null) {
            return ResultVo.error(HttpStatus.BAD_REQUEST, validationMessage);
        }

        ErpMerchant merchant = new ErpMerchant();
        BeanUtils.copyProperties(bo,merchant);
        // 商户资料新增不创建登录账号，避免将登录凭据写入商户资料表。
        merchant.setId(null);
        merchant.setLoginName(null);
        merchant.setPassword(null);
        merchant.setStatus("0");
        merchant.setDelFlag("0");
//        merchant.setLoginIp(IpUtils.getIpAddr());
//        merchant.setLoginDate(new Date());
        merchant.setCreateBy(createBy);
        merchant.setCreateTime(new Date());
        mapper.insert(merchant);
        return ResultVo.success(merchant);
    }

    /**
     * 校验商户资料新增请求。
     * 登录账号属于独立账号配置流程，新增接口只兼容 null/空值；一旦提交非空值就明确拒绝，
     * 防止旧客户端误把明文密码写入 erp_merchant.password。
     */
    private String validateAddRequest(MerchantAddBo bo) {
        if (bo == null) {
            return "请求参数不能为空";
        }
        if (StringUtils.hasText(bo.getLoginName()) || StringUtils.hasText(bo.getLoginPwd())) {
            return "新增商户不支持设置登录账号，请先创建商户后再配置账号";
        }
        if (!StringUtils.hasText(bo.getName())) {
            return "商户名称不能为空";
        }
        if (!StringUtils.hasText(bo.getNumber())) {
            return "商户编码不能为空";
        }
        if (!StringUtils.hasText(bo.getUsci())) {
            return "社会信用代码不能为空";
        }
        if (!StringUtils.hasText(bo.getFaren())) {
            return "法人不能为空";
        }
        if (!StringUtils.hasText(bo.getLinkMan())) {
            return "联系人不能为空";
        }
        if (!StringUtils.hasText(bo.getMobile())) {
            return "手机号不能为空";
        }
        if (!StringUtils.hasText(bo.getAddress())) {
            return "联系地址不能为空";
        }
        if (bo.getName().trim().length() > 255) {
            return "商户名称长度不能超过255个字符";
        }
        if (bo.getNumber().trim().length() > 25) {
            return "商户编码长度不能超过25个字符";
        }
        if (bo.getUsci().trim().length() > 255) {
            return "社会信用代码长度不能超过255个字符";
        }
        if (bo.getFaren().trim().length() > 25) {
            return "法人长度不能超过25个字符";
        }
        if (bo.getLinkMan().trim().length() > 25) {
            return "联系人长度不能超过25个字符";
        }
        if (bo.getMobile().trim().length() > 15) {
            return "手机号长度不能超过15个字符";
        }
        if (bo.getAddress().trim().length() > 255) {
            return "联系地址长度不能超过255个字符";
        }
        if (bo.getRemark() != null && bo.getRemark().length() > 500) {
            return "备注长度不能超过500个字符";
        }
        return null;
    }

}



