package com.kwsoft.version;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.List;
import java.util.Map;

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
    public final static String studentProId = "57159822f07e75084cb8a1fe";//项目的Id

    //设置2
    //教师端拼接别名Title    user
    //学员端拼接别名TTitle   stu
    public final static String aliasTitle = "stu";//别名的前缀


    //设置3
    //教师角色值    1703
    //学员角色值    1702
    public final static String configRole = "1702";//发送聊天时选择的类型



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
 *
 * 会话列表
 * tableId:370     pageId:4685
 *
 *
 * 主项目聊天记录
 tableId:373
 mainTableId:370
 mainPageId:4685
 pageId:4698
 */


    /**
     * 会话列表级别字段
     */
    public static final String channelListTableId = "370";//会话列表的tableId
    public static final String channelListPageId = "4685";//会话列表的pageId
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



    public static final String classTypeGetSearchUrl = "dataPlAdd_interfaceShowClassType.do";//对系统的称呼
    public static final String classTypeCommitSearchUrl= "dataPlAdd_interfaceShowDateClassTypeCourse.do";//对系统的称呼








    //存储学员端父类菜单的图标
    public static int[] imgs = {R.drawable.stu_see_order, R.drawable.stu_see_task,
            R.drawable.stu_see_journal, R.drawable.stu_see_attendance,
            R.drawable.stu_see_leave, R.drawable.stu_see_visit,
            R.mipmap.stu_see_achievement,R.mipmap.stu_my_evaluate,
            R.mipmap.stu_my_evaluate_curriculum,R.mipmap.stu_my_meeting,R.mipmap.stu_my_report,R.drawable.stu_see_order, R.drawable.stu_see_task,
            R.drawable.stu_see_journal, R.drawable.stu_see_attendance,
            R.drawable.stu_see_leave, R.drawable.stu_see_visit,
            R.mipmap.stu_see_achievement,R.mipmap.stu_my_evaluate,
            R.mipmap.stu_my_evaluate_curriculum,R.mipmap.stu_my_meeting,R.mipmap.stu_my_report,R.drawable.stu_see_order, R.drawable.stu_see_task,
            R.drawable.stu_see_journal, R.drawable.stu_see_attendance,
            R.drawable.stu_see_leave, R.drawable.stu_see_visit,
            R.mipmap.stu_see_achievement,R.mipmap.stu_my_evaluate,
            R.mipmap.stu_my_evaluate_curriculum,R.mipmap.stu_my_meeting,R.mipmap.stu_my_report,R.drawable.stu_see_order, R.drawable.stu_see_task,
            R.drawable.stu_see_journal, R.drawable.stu_see_attendance,
            R.drawable.stu_see_leave, R.drawable.stu_see_visit,
            R.mipmap.stu_see_achievement,R.mipmap.stu_my_evaluate,
            R.mipmap.stu_my_evaluate_curriculum,R.mipmap.stu_my_meeting,R.mipmap.stu_my_report
            };


   public static List<Map<String, Object>> loginRoleFollowList,loginMenuList;


    public static Map<String, String> commitCourseSearch;




}
