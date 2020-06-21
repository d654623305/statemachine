package com.statemachine;

public class BaseResponse {
    public boolean success;
    public String   message;
    public Object   data;

    public BaseResponse(){
        this.success = false;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
