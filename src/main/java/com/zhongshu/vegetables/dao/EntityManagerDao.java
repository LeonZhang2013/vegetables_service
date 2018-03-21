package com.zhongshu.vegetables.dao;

import com.zhongshu.vegetables.dao.beans.Entity;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EntityManagerDao {

	@Autowired
	private EntityManager entityManager;
	
	/**
	 * 新增
	 * @param entity
	 * @throws Exception
	 */
	public void executeInsert(Entity entity)throws Exception{
		StringBuilder builder = new StringBuilder(String.format("insert into %s(", entity.getTable()));
		Map<String,Object> params = entity.getField();
		boolean first = true;
		List<String> list = new ArrayList<>();
		for(Map.Entry<String, Object> entry : params.entrySet()){
			if(first == false){
				builder.append(',');
			}else{
				first = false;
			}
			builder.append(entry.getKey());
			list.add(entry.getKey());
		}
		builder.append(") values (");
		first = true;
		for(int i = 0,len = list.size();i < len;i++){
			if(first == false){
				builder.append(',');
			}else{
				first = false;
			}
			builder.append(":").append(list.get(i));
		}
		builder.append(')');
		Query query = entityManager.createNativeQuery(builder.toString());
		for(int i = 0,len = list.size();i < len;i++){
			String key = list.get(i);
			query.setParameter(key, params.get(key));
		}
		query.executeUpdate();
	}
	
	/**
	 * 更新/删除
	 * @param sql
	 * @throws Exception
	 */
	public int executeUpdate(String sql,Entity entity)throws Exception{
		Query query = entityManager.createNativeQuery(sql);
		if(null != entity){
			Map<String,Object> params = entity.getField();
			if(null != params && params.size() > 0){
				for(Map.Entry<String, Object> entry : params.entrySet()){
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}
		int count = query.executeUpdate();
		System.out.println("count="+count+",sql="+sql+",entity="+entity.getField());
		return count;
	}
	
	/**
	 * 查询多条数据
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> executeQuery(String sql,Entity entity,Integer pageNumber,int pageSize)throws Exception{
		List<Map<String,Object>> list = new ArrayList<>();
		Query query = entityManager.createNativeQuery(sql);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if(null != pageNumber){
			query.setFirstResult((pageNumber - 1) * pageSize);
			query.setMaxResults(pageSize);
		}
		if(null != entity){
			Map<String,Object> params = entity.getField();
			if(null != params && params.size() > 0){
				for(Map.Entry<String, Object> entry : params.entrySet()){
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}

		List<Object> result = query.getResultList();
		if(null != result && result.size() > 0){
			for(Object object : result){
				Map<String,Object> rows = (Map<String,Object>)object;
				list.add(rows);
			}
		}
		return list;
	}
	
	/**
	 * 查询单个数据
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> executeQuery(String sql,Entity entity)throws Exception{
		Query query = entityManager.createNativeQuery(sql);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if(null != entity){
			Map<String,Object> params = entity.getField();
			if(null != params && params.size() > 0){
				for(Map.Entry<String, Object> entry : params.entrySet()){
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}
		try {
			Object object = query.getSingleResult();
			if(null != object){
				return (Map<String,Object>)object;
			}
			return null;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	/**
	 * 查询数量
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public long executeCount(String sql,Entity entity)throws Exception{
		Query query = entityManager.createNativeQuery(sql);
		if(null != entity){
			Map<String,Object> params = entity.getField();
			if(null != params && params.size() > 0){
				for(Map.Entry<String, Object> entry : params.entrySet()){
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}
		return Long.parseLong(query.getSingleResult().toString());
	}
	
	/**
	 * 获得自增id
	 * @return
	 * @throws Exception
	 */
	public long getLastInsertId()throws Exception{
		//LAST_INSERT_ID
		Map<String,Object> data = this.executeQuery("SELECT LAST_INSERT_ID() id", null);
		return Long.parseLong(data.get("id").toString());
	}
}
