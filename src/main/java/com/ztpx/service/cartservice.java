package com.ztpx.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ztpx.model.cartBean;
import com.ztpx.dao.cartmapper;



@Service
public class cartservice {
	
	@Autowired
	private cartmapper cartmapper;
	//�����û�id����ʼ�����ﳵ
	public List<cartBean> search(String id)
	{
	return	cartmapper.selectByUserid(id);
	}
    //�����û�ID����Ʒid��ɾ��һ��һ����Ʒ
	public String deleteByid(String id,String pid)
	{  
		
		
		try{
		    int userid=Integer.parseInt(id);
		    int productid=Integer.parseInt(pid);
			cartmapper.deleteByid(userid, productid);
		}
		catch(Exception e)
		{
			return "fail";
		}
		return "ok";
	}
	
	//���ﳵ�ύ��ť
	public String submit(String id,String pid )
	{
		try{
		    int userid=Integer.parseInt(id);
		    int productid=Integer.parseInt(pid);
			cartmapper.upstatus(userid, productid);
		}
		catch(Exception e)
		{
			return "fail";
		}
		return "ok";
	}

}
	
	