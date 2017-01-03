package com.kwsoft.version;

import com.kwsoft.kehuhua.adcustom.R;

/**
 * Created by Administrator on 2016/9/19 0019.
 *
 */
public class StuPra {


    //5704e45c7cf6c0b2d9873da6 主项目苏州项目
//57159822f07e75084cb8a1fe 学员端苏州项目
    //设置1
    //教师端项目id    56cad8f89dc516b25972bacc
    //学员端项目id    56df76b60cf2beac602aaed3
    //汉普森教师端项目id    5704e45c7cf6c0b2d9873da6
    public final static String studentProId = "5704e45c7cf6c0b2d9873da6";//项目的Id

    //设置2
    //教师端拼接别名Title    user
    //学员端拼接别名TTitle   stu
    public final static String aliasTitle = "user";//别名的前缀


    //设置3
    //教师角色值    1703
    //学员角色值    1702
    public final static String configRole = "1703";//发送聊天时选择的类型



    public final static String stuProId = "56df76b60cf2beac602aaed3";//项目的Id
    public final static String userProId = "56cad8f89dc516b25972bacc";//项目的Id

    //    public static String studentProId = "56df76b60cf2beac602aaed3";//学员端项目的Id56cad8f89dc516b25972bacc
    public final static String changePsw = "phone_resetStuPassword.do?";//修改密码
    public  static String stuInfoTableId = "19";//学员个人资料tableId
    public static String stuInfoPageId = "2512";//学员个人资料pageId

    // public static String fileName;//头像名称
    public static String hpsStuHeadPath;//头像保存路径

    public static final String KEY_CHATTING = "chatting";
    public static final String KEY_IS_CHANNEL = "isChannel";
    public static final String KEY_HOST = "host";

    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static final String KEY_CHANNEL = "channel";
    public static final String KEY_ALL = "all";

    public static final String PATH_MAIN = "/main";
    public static final String PATH_CHATTING = "/chatting";
    public static final String PATH_UNREAD = "/api/unreadMessage";
    public static final String PATH_USER = "/api/user";

    public static final String PREF_CURRENT_CHATTING = "pushtalk_chatting";
    public static final String PREF_CURRENT_SERVER = "pushtalk_server";





    /**
     * 会话列表级别字段
     */
    public static final String channelListTableId = "17796";//会话列表的tableId
    public static final String channelListPageId = "7686";//会话列表的pageId
    public static final String channelImageUrl = "CHANNELIMAGE";//会话图片
    public static final String customChannelName = "CUSTOMNAME";//自定义会话名称
    public static final String defaultChannelName = "DEFAULTNAME";//系统生成的默认会话名称，如果无自定义名称则取这个默认的
    public static final String chanelLatestMsg = "CHANNELLATESTMESSAGE";//最新一次的聊天内容
    public static final String chanelLatestTime = "CHANNELLATESTTIME";//最新一次的聊天内容
    /**
     * 聊天界面级别字段
     */
    public static final String messageSendDate = "SENDDATE";//消息的发送日期+时间
    public static final String messageUserId = "USERID";//消息的发送者的userId
    public static final String messageFromName = "SENDERNAME";//消息发送者的名字
    public static final String messageFromMe = "我";//对自己的昵称
    public static final String messageContent = "MESSCONTENT";//消息内容
    public static final String messageFromSys = "系统消息";//对系统的称呼

    //存储老师端父类菜单的图标
    public static int[] imgs = {R.drawable.edus_nav_market_normal,
            R.drawable.edus_nav_market_press,
            R.drawable.edus_nav_assistant_tool_normal,
            R.drawable.edus_nav_assistant_tool_press,
            R.drawable.edus_nav_sale_normal,
            R.drawable.edus_nav_sale_press,
            R.drawable.edus_nav_finance_normal,
            R.drawable.edus_nav_finance_press,
            R.drawable.edus_nav_educational_normal,
            R.drawable.edus_nav_educational_press,
            R.drawable.edus_nav_teaching_normal,
            R.drawable.edus_nav_teaching_press,
            R.drawable.edus_nav_service_normal,
            R.drawable.edus_nav_service_press,
            R.drawable.edus_nav_assistant_normal,
            R.drawable.edus_nav_assistant_press,



            R.drawable.edus_nav_market_normal,
            R.drawable.edus_nav_market_press,
            R.drawable.edus_nav_assistant_tool_normal,
            R.drawable.edus_nav_assistant_tool_press,
            R.drawable.edus_nav_sale_normal,
            R.drawable.edus_nav_sale_press,
            R.drawable.edus_nav_finance_normal,
            R.drawable.edus_nav_finance_press,
            R.drawable.edus_nav_educational_normal,
            R.drawable.edus_nav_educational_press,
            R.drawable.edus_nav_teaching_normal,
            R.drawable.edus_nav_teaching_press,
            R.drawable.edus_nav_service_normal,
            R.drawable.edus_nav_service_press,
            R.drawable.edus_nav_assistant_normal,
            R.drawable.edus_nav_assistant_press,

            R.drawable.edus_nav_market_normal,
            R.drawable.edus_nav_market_press,
            R.drawable.edus_nav_assistant_tool_normal,
            R.drawable.edus_nav_assistant_tool_press,
            R.drawable.edus_nav_sale_normal,
            R.drawable.edus_nav_sale_press,
            R.drawable.edus_nav_finance_normal,
            R.drawable.edus_nav_finance_press,
            R.drawable.edus_nav_educational_normal,
            R.drawable.edus_nav_educational_press,
            R.drawable.edus_nav_teaching_normal,
            R.drawable.edus_nav_teaching_press,
            R.drawable.edus_nav_service_normal,
            R.drawable.edus_nav_service_press,
            R.drawable.edus_nav_assistant_normal,
            R.drawable.edus_nav_assistant_press,

            R.drawable.edus_nav_market_normal,
            R.drawable.edus_nav_market_press,
            R.drawable.edus_nav_assistant_tool_normal,
            R.drawable.edus_nav_assistant_tool_press,
            R.drawable.edus_nav_sale_normal,
            R.drawable.edus_nav_sale_press,
            R.drawable.edus_nav_finance_normal,
            R.drawable.edus_nav_finance_press,
            R.drawable.edus_nav_educational_normal,
            R.drawable.edus_nav_educational_press,
            R.drawable.edus_nav_teaching_normal,
            R.drawable.edus_nav_teaching_press,
            R.drawable.edus_nav_service_normal,
            R.drawable.edus_nav_service_press,
            R.drawable.edus_nav_assistant_normal,
            R.drawable.edus_nav_assistant_press,};




    //存储老师端父类菜单的图标
    public static int[] images = {
            R.mipmap.f1,
            R.mipmap.f2,
            R.mipmap.f3,
            R.mipmap.f4,

            R.mipmap.f5,
            R.mipmap.f6,
            R.mipmap.f7,
            R.mipmap.f8,


            R.mipmap.f9,
            R.mipmap.f10,
            R.mipmap.f11,
            R.mipmap.f12,


            R.mipmap.f13,
            R.mipmap.f14,
            R.mipmap.f15,
            R.mipmap.f16,


            R.mipmap.f17,
            R.mipmap.f18,
            R.mipmap.f19,
            R.mipmap.f20,


            R.mipmap.f1,
            R.mipmap.f2,
            R.mipmap.f3,
            R.mipmap.f4,


            R.mipmap.f5,
            R.mipmap.f6,
            R.mipmap.f7,
            R.mipmap.f8};
}
