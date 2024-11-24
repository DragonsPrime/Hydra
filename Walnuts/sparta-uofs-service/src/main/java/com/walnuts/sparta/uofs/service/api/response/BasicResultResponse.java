package com.walnuts.sparta.uofs.service.api.response;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSONEncoder;

public class BasicResultResponse<T> implements Pinenut, Serializable {
    private Integer    code = HttpStatus.OK.value();
    private String     msg; //错误信息
    private T          data; //数据

    public static <T> BasicResultResponse<T > success() {
        BasicResultResponse<T> result = new BasicResultResponse<>();
        result.code = HttpStatus.OK.value();
        return result;
    }

    public static <T> BasicResultResponse<T > successMsg( String msg  ) {
        BasicResultResponse<T> result = new BasicResultResponse<>();
        result.msg  = msg;
        result.code = HttpStatus.OK.value();
        return result;
    }

    public static <T> BasicResultResponse<T > success( T object ) {
        BasicResultResponse<T> result = new BasicResultResponse<>();
        result.data = object;
        result.code = HttpStatus.OK.value();
        return result;
    }

    public static <T> BasicResultResponse<T > error( String msg ) {
        BasicResultResponse<T> result = new BasicResultResponse<>();
        result.msg  = msg;
        result.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        return result;
    }


    /**
     * 获取
     * @return code
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * 设置
     * @param code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取
     * @return msg
     */
    public String getMsg() {
        return this.msg;
    }

    /**
     * 设置
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取
     * @return data
     */
    public T getData() {
        return this.data;
    }

    /**
     * 设置
     * @param data
     */
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toJSONString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "code" , this.code ),
                new KeyValue<>( "msg"  , this.msg ),
                new KeyValue<>( "data" , this.data )
        } );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
