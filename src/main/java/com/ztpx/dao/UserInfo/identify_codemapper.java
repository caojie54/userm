package com.ztpx.dao.UserInfo;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ztpx.model.UserInfo.identify_codeBean;

@Repository  
@Transactional  
public interface identify_codemapper {
	
	//插入数据
	public int insert(identify_codeBean identify_code);
	
	//查询指定UserID的验证码
	public identify_codeBean selectByUserId(int UserId);
	
	//删除指定ID的验证码
	public int delete(int id);
}
