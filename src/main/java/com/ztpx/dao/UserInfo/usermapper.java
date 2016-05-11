package com.ztpx.dao.UserInfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ztpx.model.UserInfo.UserInfoBean;

@Repository  
@Transactional  
public interface usermapper {
	
	//向数据库插入数据
	public int insert(UserInfoBean userinfo);
	
	//修改数据
	public int updateById(@Param("user")UserInfoBean userinfo,@Param("userId")int id);
	
	//查询全部用户数据
	public List<UserInfoBean> selectAll();
	
	//查询指定用户信息
	public UserInfoBean selectById(int id);
	
	//根据用户名查询用户
	public UserInfoBean selectByUsername(String name);
	
	//根据邮箱查询用户
	public UserInfoBean selectByEmail(String email);
	
	//删除指定ID的用户
	public int delete(int id);
}
