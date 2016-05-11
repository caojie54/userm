package com.ztpx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ztpx.service.cartservice;

@Controller
@RequestMapping("/cart")
public class cart {
	
	
	
	@Autowired
	private cartservice  cs;
	
	@RequestMapping("/show")
	public ModelAndView  show(@RequestParam("id")String userid)
	{
		ModelAndView view =new ModelAndView("/shopcart/show.html");	
		view.addObject("carts",cs.search(userid));	
		view.addObject("master",userid);
		return view;
		
	}
	@ResponseBody
	@RequestMapping(value="/deleteByid",method=RequestMethod.POST)
	public String deleteByid(@RequestParam("userid")String userid,@RequestParam("productid")String productid)
	{
		return cs.deleteByid(userid, productid);
	}
	
	@ResponseBody
	@RequestMapping(value="/submit",method=RequestMethod.POST)
	public String submit(@RequestParam("userid")String userid,@RequestParam("productid")String productid)
	{
		return cs.submit(userid, productid);
	}
	

}
