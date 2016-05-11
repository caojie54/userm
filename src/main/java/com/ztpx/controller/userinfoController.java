package com.ztpx.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ztpx.dao.UserInfo.*;
import com.ztpx.model.UserInfo.MailSenderModel;
import com.ztpx.model.UserInfo.SimpleMailSender;
import com.ztpx.model.UserInfo.UserInfoBean;
import com.ztpx.model.UserInfo.identify_codeBean;

@Controller
@RequestMapping("/userinfo")
public class userinfoController {
	
	//窗口回话列表
	private Map<String,String> sessionList=new HashMap<String,String>();
	
	@Resource
	private TaskExecutor taskExecutor;//注入Spring封装的异步执行器

	@Autowired
	private usermapper usermapper;
	@Autowired
	identify_codemapper identify_codemapper;
	
	//首页
	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request){
		ModelAndView view =new ModelAndView("index.html");
		if(sessionList.containsKey(request.getSession().getId())){
			view.addObject("islogged","1");
			view.addObject("userid",request.getSession().getAttribute("userid"));
			view.addObject("nickname",request.getSession().getAttribute("nickname"));
		}else{
			view.addObject("islogged","0");
		}
		return view;
	}

	// 注册页面
	@ResponseBody
	@RequestMapping("/regPage")
	public ModelAndView regPage() {
		ModelAndView view = new ModelAndView("/userinfo/regPage.html");
		return view;
	}

	// 验证码生成器
	// 获取验证码
	@RequestMapping("/getCode")
	public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 验证码图片的宽度和高度
		int width = 120, height = 35;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		OutputStream os = response.getOutputStream();
		Graphics g = image.getGraphics();
		Random random = new Random();
		g.setColor(new Color(200 + random.nextInt(50), 200 + random.nextInt(50), 200 + random.nextInt(50)));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Arial", Font.PLAIN, 18));
		g.setColor(new Color(150 + random.nextInt(50), 150 + random.nextInt(50), 150 + random.nextInt(50)));
		// 画出200条干扰线
		for (int i = 0; i < 200; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		String sRand = "";
		g.setFont(new Font("Arial", Font.PLAIN, 30));
		// 生成四位随机验证码
		for (int j = 0; j < 4; j++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(rand, 30 * j + 5, 30);
		}
		request.getSession().setAttribute("idCode", sRand);
		g.dispose();
		ImageIO.write(image, "JPEG", os);
		os.flush();
		os.close();
		os = null;
		response.flushBuffer();

	}

	// 检查验证码是否正确
	@RequestMapping("/checkCode")
	public void checkCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String code = (String) request.getSession().getAttribute("idCode");
		String sCode = request.getParameter("idenCode");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		if (code.equals(sCode)) {
			response.getWriter().append("ok");
		} else {
			response.getWriter().append("error_code");
		}
	}

	// 审核username
	@ResponseBody
	@RequestMapping(value = "/selectByUserName", method = RequestMethod.POST)
	public String selectByUserName(@RequestParam("username") String username) {
		UserInfoBean user = null;
		if ((user = usermapper.selectByUsername(username)) != null) {
			return "error";
		} else {
			return "ok";
		}

	}

	// 审核email
	@ResponseBody
	@RequestMapping(value = "/selectByEmail", method = RequestMethod.POST)
	public String selectByEmail(@RequestParam("email") String email) {
		if (usermapper.selectByEmail(email) != null) {
			return "error";
		} else {
			return "ok";
		}
	}

	private // 邮件发送模板
	MailSenderModel m = new MailSenderModel();
	// 注册审核 向邮箱发送验证码
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(UserInfoBean userinfo) {
		
		// 向数据库中添加新用户信息
		usermapper.insert(userinfo);
		// 向数据库中插入一个验证码信息
		int code = (int) (Math.random() * 100000);
		int userId = usermapper.selectByUsername(userinfo.getUsername()).getId();
		identify_codeBean identify = new identify_codeBean();
		identify.setUserId(userId);
		identify.setIdentify_code(code + "");
		identify_codemapper.insert(identify);

		// 邮件信息
		m.setSubject("激活注册邮箱");
		m.setToAddress(userinfo.getEmail());

		String confirmUrl = "http://127.0.0.1:8080/userinfo/identify?userId=" + userId + "&code=" + code;
		// 设置邮件内容
		String emailContent = "<style>a:hover{color:#00FFFF;}</style><h3 style=\"color:blue\">您好，你已经注册了***，请点击下面的链接激活</h3>"
				+ "<p style=\"color:#00FFFF;font-size:30px;\">" + code + "</p></br>" + "<a href=\"" + confirmUrl
				+ "\" target=\"_blank\" style=\"\">点击链接激活</a>";
		m.setContent(emailContent);
		// 发送邮件
		taskExecutor.execute(new Runnable(){
			   public void run(){
			    try {
			    	//在这里写发送邮件
			    	boolean b = SimpleMailSender.sendHtmlMail(m);
			    } catch (Exception e) {
			    	System.out.println(e);
			    }
			   }
			  });
			return "ok";
		
	}

	// 注册结果页面
	@RequestMapping("/regResult")
	public ModelAndView regResult() {
		ModelAndView view = new ModelAndView("userinfo/regMessage.html");
		view.addObject("title", "注册结果");
		view.addObject("message", "系统已经向你的邮箱发送一封激活邮件，请登录你的邮箱完成注册！");
		return view;
	}

	// 邮箱激活链接 注册审核 接收验证码信息
	@ResponseBody
	@RequestMapping(value = "/identify", method = RequestMethod.GET)
	public ModelAndView identifyReg(@RequestParam("userId") int userId, @RequestParam("code") int code) {
		// 判断传过来的userId的验证码是否匹配
		if (identify_codemapper.selectByUserId(userId)!=null && identify_codemapper.selectByUserId(userId).getIdentify_code().equals(code + "")) {
			// 验证通过，注册成功
			UserInfoBean userinfo = usermapper.selectById(userId);
			// 注册账号状态激活
			userinfo.setActive(1);
			usermapper.updateById(userinfo, userId);
			// 显示注册成功页面
			ModelAndView view = new ModelAndView("/userinfo/regResult.html");
			view.addObject("title", "注册成功");
			view.addObject("message", "账号:" + userinfo.getUsername() + "  激活成功");
			//销毁验证码信息
			identify_codemapper.delete(identify_codemapper.selectByUserId(userId).getId());
			return view;

		} else {
			System.out.println("验证码不匹配或者数据库中不存在该用户");
			ModelAndView view = new ModelAndView("/userinfo/regResult.html");
			view.addObject("title", "注册结果");
			view.addObject("message", "该邮箱已经注册！");
			return view;
		}

	}

	// 返回登录页面
	@RequestMapping("/login")
	public ModelAndView LoginPage(HttpServletRequest request) {
		if(sessionList.containsKey(request.getSession().getId())){
			sessionList.remove(request.getSession().getId());
			
		}
		ModelAndView view = new ModelAndView("/userinfo/loginPage.html");
		return view;
	}

	// 审核登录,登录成功，进入网站内容首页
	@ResponseBody
	@RequestMapping(value = "/logged", method = RequestMethod.POST)
	public String login(HttpServletRequest request,@RequestParam("username") String username, @RequestParam("password") String password) {
		UserInfoBean user = new UserInfoBean();
		// 根据填写的账号名或者邮箱登录
		if (usermapper.selectByUsername(username) != null) {
			user = usermapper.selectByUsername(username);
		} else if (usermapper.selectByEmail(username) != null) {
			user = usermapper.selectByUsername(username);
		} else {
			return "error";
		}
		// 判断密码是否匹配
		if (user.getPassword().equals(password)) {
			// 用户在线
			user.setIsOnline(1);
			sessionList.put(request.getSession().getId(), "1");
			request.getSession().setAttribute("userid", user.getId());
			request.getSession().setAttribute("nickname", user.getNickname());
			return "ok";
		} else {
			return "error";
		}

	}
	
	// 登录成功页面
	@RequestMapping("/loggedPage")
	public ModelAndView LoggedPage() {
		ModelAndView view = new ModelAndView("/userinfo/logged.html");
		return view;
	}

	// 找回密码页面,
	@RequestMapping("/findPW")
	public ModelAndView findPW() {
		ModelAndView view = new ModelAndView("/userinfo/findPW.html");
		view.addObject("message", "");
		return view;
	}

	// 找回密码功能
	@ResponseBody
	@RequestMapping(value = "/findPwfunc", method = RequestMethod.POST)
	public void findPwfunc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Writer out = response.getWriter();
		response.setContentType("text");

		// user
		UserInfoBean user = new UserInfoBean();

		// 要找回密码的账号是否存在
		if (usermapper.selectByUsername(username) != null) {
			user = usermapper.selectByUsername(username);
			System.out.println(user.getEmail());
		} else if (usermapper.selectByEmail(username) != null) {
			user = usermapper.selectByEmail(username);
			System.out.println(user.getUsername());
		} else {
			out.append("账号名或者邮箱填写错误");
			return;
		}
		System.out.println(user.getActive());
		// 账号存在，判断账号的邮箱是否可用
		if (user.getActive() > 0) {
			// 向注册邮箱发送重置密码的链接
			String email = user.getEmail();
			// 发送邮件,user.getId();
			m.setSubject("重置密码");
			m.setToAddress(email);
			String confirmUrl = "http://127.0.0.1:8080/userinfo/resetPW?userId=" + user.getId();
			String content = "<style>a:hover{color:#00FFFF;}</style><h3 style=\"color:blue\">您好，请点击下面的链接，完成重置密码的操作</h3>"
					+ "<a href=\"" + confirmUrl + "\" target=\"_blank\" style=\"\">重置密码</a>";
			m.setContent(content);
			// 发送邮件
			taskExecutor.execute(new Runnable(){
				   public void run(){
				    try {
				    	//在这里写发送邮件
				    	boolean b = SimpleMailSender.sendHtmlMail(m);
				    } catch (Exception e) {
				    	System.out.println(e);
				    }
				   }
				  });
			// 页面提示，已经向注册邮箱发送重置密码的链接，
			String message = "已经向注册邮箱" + email + "发送重置密码的邮件，请登录邮箱完成操作";
			out.append(message);
		} else {
			String message = "该账号邮箱未激活，请完成激活操作";
			out.append(message);
		}
		out.close();
	}

	// 重置密码页面
	@RequestMapping("/resetPW")
	public ModelAndView resetPW(@RequestParam("userId") Integer userId) {
		ModelAndView view = new ModelAndView("/userinfo/resetPW.html");
		UserInfoBean user = new UserInfoBean();
		user = usermapper.selectById(userId);
		view.addObject("userId", userId);
		view.addObject("username", user.getUsername());
		return view;
	}

	// 重置密码功能
	@RequestMapping(value = "/resetPwfunc", method = RequestMethod.POST)
	public void resetPwfunc(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 获取传递的参数
		String password = req.getParameter("password");
		String id = req.getParameter("userId");
		// 转化为int
		int userid = Integer.parseInt(id);
		System.out.println("userid" + userid);
		// 设置字符编码
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");

		Writer out = resp.getWriter();
		resp.setContentType("text");
		// 通过参数id，修改用户密码
		UserInfoBean user = new UserInfoBean();
		user = usermapper.selectById(userid);
		System.out.println("257line:user.username=" + user.getUsername());
		// 数据库中 修改密码
		user.setPassword(password);
		int res = usermapper.updateById(user, userid);
		System.out.println("261line:res=" + res);
		// 数据库中修改密码成功，
		if (res > 0) {
			String message = "修改密码成功";
			out.append(message);
		}

		out.close();

	}

	// 用户完善信息或修改信息页面
	@RequestMapping("/modifyInfo")
	public ModelAndView modify(@RequestParam("userid") Integer id) {
		ModelAndView view = new ModelAndView("/userinfo/editPage.html");
		// 通过参数id，修改用户密码
		UserInfoBean user = new UserInfoBean();
		user = usermapper.selectById(id);
		view.addObject("user", user);
		return view;

	}

	// 用户修改信息
	@ResponseBody
	@RequestMapping(value = "/modifyInfo", method = RequestMethod.POST)
	public String modify(HttpServletRequest request,UserInfoBean userinfo) {
		// 获得id
		int id = userinfo.getId();
		// 通过id判断邮箱是否被修改
		UserInfoBean olduserinfo = usermapper.selectById(id);
		if (!olduserinfo.getEmail().equals(userinfo.getEmail())) {
			// 邮箱被修改，向新邮箱发送激活链接
			m.setSubject("激活新邮箱");
			String url = "http://127.0.0.1:8080/userinfo/updateEmail?userId=" + id + "&email=" + userinfo.getEmail();
			String content = "<style>a:hover{color:#00FFFF;}</style><h3 style=\"color:blue\">您好，请点击下面的链接，完成修改邮箱的操作</h3>"
					+ "<a href=\"" + url + "\" target=\"_blank\" style=\"\">修改邮箱</a>";
			m.setContent(content);
			m.setToAddress(userinfo.getEmail());
			// 发送邮件
			taskExecutor.execute(new Runnable(){
				   public void run(){
				    try {
				    	//在这里写发送邮件
				    	boolean b = SimpleMailSender.sendHtmlMail(m);
				    } catch (Exception e) {
				    	System.out.println(e);
				    }
				   }
				  });
			userinfo.setEmail(olduserinfo.getEmail());
			
			userinfo.setUsername(olduserinfo.getUsername());
			userinfo.setIsOnline(olduserinfo.getIsOnline());
			userinfo.setActive(olduserinfo.getActive());
			request.getSession().setAttribute("nickname", userinfo.getNickname());
			
			int res = usermapper.updateById(userinfo, id);
			return "wait";

		} else {
			
			userinfo.setUsername(olduserinfo.getUsername());
			userinfo.setIsOnline(olduserinfo.getIsOnline());
			userinfo.setActive(olduserinfo.getActive());
			
			request.getSession().setAttribute("nickname", userinfo.getNickname());
			int m = usermapper.updateById(userinfo, id);
			return "ok";
		}
	}

	// 修改邮箱
	@RequestMapping("/updateEmail")
	public ModelAndView updateEmail(@RequestParam("userId") Integer id, @RequestParam("email") String email) {
		ModelAndView view = new ModelAndView("/userinfo/regMessage.html");
		UserInfoBean user = new UserInfoBean();
		user = usermapper.selectById(id);
		user.setEmail(email);
		int res = usermapper.updateById(user, id);
		view.addObject("title", "激活新的邮箱");
		view.addObject("message", "新的邮箱("+email+")已经激活");
		return view;

	}

}
