package com.zhongshu.vegetables.service;

import com.zhongshu.vegetables.bean.Attribute;
import com.zhongshu.vegetables.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class AttributeService extends BaseService {

    public List<Map<String, Object>> getAttributes(Integer islist) {
        return attributeDao.getAttributes(islist);
    }

    public boolean update(Attribute category) {
        int effect = attributeDao.updateById(category);
        return effect == 1;
    }

    public boolean insert(Attribute category) {
        int effect = attributeDao.insert(category);
        return effect == 1;
    }


    public List<Map<String,Object>> getSearchKeys(Long attribute_id,Integer islist) {
       return attributeDao.getSearchKeys(attribute_id,islist);
    }

    public boolean updateSearchKey(Long id, String name, int islist, long attribute_id) {
        int effect = attributeDao.updateSearchKey(id,name,islist,attribute_id);
        return effect ==1;

    }

    public boolean insertSearchKey(String name, int islist, long attribute_id) throws CustomException {
        int effect = attributeDao.insertSearchKey(name,islist,attribute_id);
        return effect ==1;
    }
}
