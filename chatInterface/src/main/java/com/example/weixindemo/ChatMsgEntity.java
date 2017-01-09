package com.example.weixindemo;

public class ChatMsgEntity {
	private static final String TAG = ChatMsgEntity.class.getSimpleName();
	private String name;//聊天的对象，也可以理解为会话对象名称
	private String date;//消息发送时间
	private String text;//接到的内容，包括文字、语音、图片
	private String time;//语音消息的时长
	private boolean isComMeg = true;//是否是自己发送的消息


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isComMeg() {
		return isComMeg;
	}
	   public boolean getMsgType() {
	        return isComMeg;
	    }
	    public void setMsgType(boolean isComMsg) {
	    	isComMeg = isComMsg;
	    }
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ChatMsgEntity() {
	}
	public ChatMsgEntity(String name, String date, String text, boolean isComMeg) {
		super();
		this.name = name;
		this.date = date;
		this.text = text;
		this.isComMeg = isComMeg;
	}
@Override
public String toString() {
	return "ChatMsgEntity [name="+name+",date="+date+",text="+text+",time="+time+",isComMeg="+isComMeg+"]";
}


}
