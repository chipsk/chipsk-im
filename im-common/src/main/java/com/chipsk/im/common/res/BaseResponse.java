package com.chipsk.im.common.res;

import com.chipsk.im.common.enums.StatusEnum;
import com.chipsk.im.common.util.StringUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

	private String code;
	
	private String message;

	/**
	 * 请求号
	 */
	private String reqNo;
	
	private T dataBody;

	public BaseResponse() {
		this.code = StatusEnum.SUCCESS.getCode();
		this.message = StatusEnum.SUCCESS.getMessage();
	}

	public BaseResponse(T dataBody) {
		this.dataBody = dataBody;
	}

	public BaseResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public BaseResponse(String code, String message, T dataBody) {
		this.code = code;
		this.message = message;
		this.dataBody = dataBody;
	}

	public BaseResponse(String code, String message, String reqNo, T dataBody) {
		this.code = code;
		this.message = message;
		this.reqNo = reqNo;
		this.dataBody = dataBody;
	}

	public static <T> BaseResponse<T> create(T t){
		return new BaseResponse<T>(t);
	}

	public static <T> BaseResponse<T> create(T t, StatusEnum statusEnum){
		return new BaseResponse<T>(statusEnum.getCode(), statusEnum.getMessage(), t);
	}

	public static <T> BaseResponse<T> createSuccess(T t, String message){
		return new BaseResponse<T>(StatusEnum.SUCCESS.getCode(), StringUtil.isNullOrEmpty(message) ? StatusEnum.SUCCESS.getMessage() : message, t);
	}

	public static <T> BaseResponse<T> createFail(T t, String message){
		return new BaseResponse<T>(StatusEnum.FAIL.getCode(), StringUtil.isNullOrEmpty(message) ? StatusEnum.FAIL.getMessage() : message, t);
	}

	public static <T> BaseResponse<T> create(StatusEnum statusEnum, String message){

		return new BaseResponse<T>(statusEnum.getCode(), message);
	}

	public static <T> BaseResponse<T> okResult(T data){
		BaseResponse<T> result = create(StatusEnum.SUCCESS, StatusEnum.SUCCESS.getMessage());
		if (data != null) {
			result.setDataBody(data);
		}
		return result;
	}

	public static <T> BaseResponse<T> okResult(String code, String message){
		BaseResponse<T> baseResponse = new BaseResponse<>();
		return baseResponse.ok(code, message, null);
	}

	public static <T> BaseResponse<T> okResult(){
		return create(StatusEnum.SUCCESS, StatusEnum.SUCCESS.getMessage());
	}

	private BaseResponse<T> ok(String code, String message, T dataBody) {
		this.code = code;
		this.dataBody = dataBody;
		this.message = message;
		return this;
	}


}
