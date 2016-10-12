package com.example.melvin.fllowme.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by Melvin on 2016/9/8.
 */
public class PushMessages extends BmobObject {
    private String message;
    private String senderId;
    private BmobPointer receiver;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public BmobPointer getReceiver() {
        return receiver;
    }

    public void setReceiver(BmobPointer receiver) {
        this.receiver = receiver;
    }


}
