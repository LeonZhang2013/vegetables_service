package com.zhongshu.vegetables.controller;

import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.service.*;
import com.zhongshu.vegetables.service.back.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class BaseController {

    @Autowired
    protected AliYunService aliYunService;

    @Autowired
    protected CommonService commonService;

    @Autowired
    protected PurchaseService purchaseService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected AuthService auth;

    @Autowired
    protected  ManagerUserService managerUserService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected OrderManagerService orderManagerService;

    @Autowired
    protected AttributeService attributeService;

    @Autowired
    protected DistributionService distributionService;

    @Autowired
    protected WalletService walletService;

    public User getUser(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (User) requestAttributes.getAttribute("user", 0);
    }

    protected Long getUserId(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        User user = (User) requestAttributes.getAttribute("user", 0);
        return null == user?null:user.getId();
    }
}
