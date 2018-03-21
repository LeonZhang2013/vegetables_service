package com.zhongshu.vegetables.service.back;

import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.service.BaseService;
import com.zhongshu.vegetables.utils.Tools;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WalletService extends BaseService {

    public boolean hasWallet(User user) {
        return walletDao.hasWallet(user.getId());
    }

    public boolean updateWallet(Long id, String idcard, String password) throws Exception {
        String encryptPass = Tools.encryptPass(id.toString(),password);
        return walletDao.updateWallet(id,idcard,encryptPass);
    }

    public String getIdCard(String user_id) throws CustomException {
        return walletDao.getIdCard(user_id);
    }

    public Map<String, Object> getWallet(Long id, String password) throws Exception {
        String encryptPass = Tools.encryptPass(id.toString(),password);
        return walletDao.verPass(id, encryptPass);
    }

    public List<Map<String, Object>> getRecordType() {
        return walletDao.getRecordTypes();
    }

    public List<Map<String, Object>> getRecords(Pager<Map<String, Object>> pager, Long user_id, Integer type) {
        return walletDao.getRecords(pager,user_id.toString(),type);
    }
}
