package com.example.rmesaage;

import java.io.Serializable;

public class Response<T>  implements Serializable {
    private T data;
    public Response(T data){
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
