package com.example.melvin.fllowme.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by chairyfish on 2016/8/13.
 */
public class Users extends BmobUser {

    private static final long serialVersionUID = 1L;
    private String nickname;    //昵称
    private String headPic;       //头像
    private Boolean sex;            //性别
    private Boolean isOnline;
    private BmobRelation contacts;

    public Users() {
        isOnline = false;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public BmobRelation getContacts() {
        return contacts;
    }

    public void setContacts(BmobRelation contacts) {
        this.contacts = contacts;
    }
//    private int usernumber;         //最大用户ID
    //   private int maxnumber;           //返回记录数

/*
    public Users(){
        setTableName("Users");          //操作的表为_USER
    }
*/

    /************************
     * nickname
     *****************************/
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    /************************
     * 用户头像
     *****************************/
    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }


    /*******************
     * 性别
     ***************************/
    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }


//    /*********************查询表服务***********************/
//    public void queryUserTable(){
//        BmobQuery query =new BmobQuery("Users");


}




