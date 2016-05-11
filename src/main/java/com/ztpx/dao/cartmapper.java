package com.ztpx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ztpx.model.cartBean;

public interface cartmapper {
	//����userid��ѯ��Ʒ
    List<cartBean> selectByUserid(String userid);
    //ɾ��������Ʒ
    void deleteByid(@Param("userid")int userid,@Param("productid")int productid);
	 //update��Ʒ״̬
    void upstatus (@Param("userid")int userid,@Param("productid")int productid);
}
