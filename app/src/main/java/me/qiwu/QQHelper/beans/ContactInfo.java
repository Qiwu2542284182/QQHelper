package me.qiwu.QQHelper.beans;

import android.graphics.drawable.Drawable;

/**
 * Created by Deng on 2018/8/2.
 */

public class ContactInfo {
    public String uin;//QQ号
    public String name;//昵称
    public int istroop;//好友还是群聊
    public Drawable head;//头像

    public void setUin(String uin){
        this.uin=uin;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setIstroop(int istroop){
        this.istroop=istroop;
    }

    public void setHead(Drawable head){
        this.head=head;
    }

    public String getUin(){
        return uin;
    }

    public String getName(){
        return name;
    }

    public int getIstroop(){
        return istroop;
    }

    public Drawable getHead(){
        return head;
    }

    public String getId(){
        String msg="";
        if (uin.length()<10){
            for (int i=0;i<10-uin.length();i++){
                msg=msg+"0";
            }
        }
        return msg+uin+String.valueOf(istroop);
    }
}
