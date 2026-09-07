package cn.qihangerp.model.bo;

import lombok.Data;

@Data
public class MerchantAddBo {
    private Long id;
    private String name;
    private String number;
    private String usci;
    private String faren;
    private String linkMan;
    private String mobile;
    private String address;
    /**
     * 商户资料新增不创建登录账号，字段仅为兼容旧客户端保留。
     */
    private String loginName;
    /**
     * 商户资料新增不创建登录账号，字段仅为兼容旧客户端保留。
     */
    private String loginPwd;
    private String remark;

}
