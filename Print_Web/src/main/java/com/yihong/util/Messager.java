package com.yihong.util;

import com.yihong.bean.Message;

/**
 * 返回请求消息的类
 */
public class Messager {
	
	public static <T> Message<T> GetOkMessage(T data)
	{
		Message<T> message=new Message<T>();
		message.setCode(200);
		message.setMessage("成功");
		message.setData(data);
		return message;
	}

	public static <T> Message<T> GetFailMessage(int code,String msg)
	{
		Message<T> message=new Message<T>();
		message.setCode(code);
		message.setMessage(msg);
		message.setData(null); 
		return message;
	}
}
