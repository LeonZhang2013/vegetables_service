package com.zhongshu.vegetables.controller;

import com.alibaba.fastjson.JSON;
import com.zhongshu.vegetables.bean.Role;
import com.zhongshu.vegetables.bean.Version;
import com.zhongshu.vegetables.dao.CommonDao;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.BaseService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.swing.text.html.parser.Entity;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("common")
public class CommonController extends BaseController {


	@RequestMapping(value="getArea",method={RequestMethod.POST})
//	@ApiMethod(summary = "获得地域列表",
	public MultiResult<Map<String,Object>> getArea(String parentId){
		MultiResult<Map<String,Object>> result = new MultiResult<>();
		try {
			if(null != parentId){
				result = commonService.getArea(Long.parseLong(parentId));
			}else{
				result.setCode(Code.EXP_PARAM);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(Code.ERROR);
		}
		return result;
	}

	/**
	 * 获得对应的字典值
	 * @param key
	 * @return
	 * @throws Exception
	 */
//	public SingleResult<Map<String,Object>> getDictionary(String key)throws Exception{
//		SingleResult<Map<String,Object>> result = new SingleResult<>();
//		List<Map<String,Object>> data = entityManagerDao.executeQuery("select * from dictionary where `key` = :key", Entity.options().add("key", key).build(), 1, 1);
//		if(null != data && data.size() > 0){
//			result.setCode(Code.SUCCESS);
//			result.setData(data.get(0));
//		}else{
//			result.setCode(Code.NO_DATA);
//		}
//		return result;
//	}

	/**
	 * 获取权限。
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getRoles")
	public SingleResult<Object> getRoles(){
		SingleResult<Object> result = new SingleResult<>();
		try {
			List<Role> roles = commonService.getRoles(getUser().getRole_id());
			result.setCode(Code.SUCCESS);
			result.setData(roles);
		} catch (Exception e) {
			result.setCode(Code.ERROR);
			result.setMessage(e.getMessage());
		}
		return result;
	}



	/**
	 * 检查更新
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("checkVersion")
	public SingleResult<Object> checkVersion(Integer code,String packageName){
		SingleResult<Object> result = new SingleResult<>();
		try {
			Version version = commonService.checkVersion(code,packageName);
			result.setCode(Code.SUCCESS);
			if(version!=null) {
				result.setData(JSON.toJSON(version));
			}else {
				result.setMessage("没有数据");
			}
		} catch (Exception e) {
			result.setCode(Code.ERROR);
			result.setMessage(e.getMessage());
		}
		return result;
	}

}
