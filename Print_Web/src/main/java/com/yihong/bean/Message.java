package com.yihong.bean;

/**
 * @author 魏帅奇
 * 提示实体
 * @author admin
 * @date 2018年10月17日
 * @param <T>
 */
public class Message<T> {

	/**
     *  编号
     */
	private Integer code;
	/**
     * 提示内容
     */
	private String message;
	/**
     * 提示类型
     */
	private T data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}	
	
	
	
}
