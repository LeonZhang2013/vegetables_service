package com.zhongshu.vegetables.controller.back;

import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.utils.DateUtils;
import com.zhongshu.vegetables.utils.SQLTools;
import com.zhongshu.vegetables.utils.Tools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("wallet")
public class WalletController extends BaseController {


    @RequestMapping("hasInitWallet")
    public SingleResult<Object> hasInitWallet() {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            Boolean exist = walletService.hasWallet(getUser());
            result.setCode(Code.SUCCESS);
            result.setData(exist);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("updateWallet")
    public SingleResult<Object> updateWallet(String idcard, String password, String vercode, String init) {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            boolean exist = false;
            boolean isOK = commonService.verCode(getUser().getMobile(), vercode);
            if (isOK) {
                if (idcard.length() != 18) throw new CustomException("身份证有误");
                if (!"true".equals(init)) {
                    String temp_idcard = walletService.getIdCard(idcard);
                    if (idcard.equals(temp_idcard)) {
                        exist = walletService.updateWallet(getUser().getId(), idcard, password);
                    }
                } else {
                    exist = walletService.updateWallet(getUser().getId(), idcard, password);
                }
            }
            if (exist) {
                result.setCode(Code.SUCCESS);
            }
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("getWallet")
    public SingleResult<Object> getWallet(String password) {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            Map<String, Object> data = walletService.getWallet(getUser().getId(), password);
            result.setCode(Code.SUCCESS);
            result.setData(data);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("withdrawals")
    public SingleResult<Object> withdrawals(String zfb, String amount, String password) {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            if (null != amount) {
                Map<String, Object> wallet = walletService.getWallet(getUser().getId(), password);
                String data = orderManagerService.withdrawals(wallet, zfb, amount);
                result.setCode(Code.SUCCESS);
                result.setData(data);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @RequestMapping("getRecordType")
    public SingleResult<Object> getRecordType() {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            List<Map<String, Object>> type = walletService.getRecordType();
            result.setCode(Code.SUCCESS);
            result.setData(type);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }


    @RequestMapping("getRecords")
    public SingleResult<Object> getRecords(Pager<Map<String, Object>> pager,Integer type) {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            List<Map<String, Object>> data = walletService.getRecords(pager, getUser().getId(),type);
            Tools.PraseDate(data,"create_time");
            result.setCode(Code.SUCCESS);
            result.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}

