package com.zzlh.auth.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zzlh.auth.conf.CacheBuilderConf;

/**
 * @Description 登录控制器
 * @author liulei
 * @date 2018年11月12日 下午4:52:30
 */
@Controller
public class LoginController {
	@Autowired
	private CacheBuilderConf cacheBuilderConf;
	/**
	 * @Description 获取验证码
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(path="/code",method=RequestMethod.GET)
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	//创建图像缓存区对象（等价于画纸），参数：宽，高，图像类型
		int width = 80;
		int height = 30;
    	BufferedImage bi=new BufferedImage(width,height,BufferedImage.TYPE_USHORT_555_RGB);
    	//得到制图对象，即画笔
    	Graphics g=bi.getGraphics();
    	//设置上下文颜色（背景）
    	g.setColor(Color.gray);
    	//填充
    	g.fillRect(0, 0, 80, 30);
    	//设置验证码
    	String code="";
    	//验证码可取字符
    	String str="qwertyupasdfghjkxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM3456789";
    	//在字符串中取4个任意字符组成字符串验证码
    	Random rd=new Random();
    	for(int i=0;i<4;i++){
    		code+=str.charAt(rd.nextInt(str.length()));
    	}
    	//存入缓存用于验证登陆
    	String cache = code.toLowerCase();
    	cacheBuilderConf.putCode(cache, cache);
    	//设置字体颜色
    	g.setColor(Color.orange);
    	g.setFont(new Font("微软雅黑",Font.BOLD,18));
    	for(int i=0; i<25; i++){
    		int x11 = rd.nextInt(width);
    		int y11 = rd.nextInt(height);
    		int x22 = rd.nextInt(width);
    		int y22 = rd.nextInt(height);
    		g.drawLine(x11, y11, x11+x22, y11+y22);
    	}
    	//画验证码
    	g.drawString(code, 15, 20);
    	ImageIO.write(bi, "jpeg", response.getOutputStream());
    }
    
}
