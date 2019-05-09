package com.sunlands.deskmate.service;



import com.sunlands.deskmate.vo.CounselorInfoVO;
import com.sunlands.deskmate.vo.MsgCodeVO;

import java.io.File;
import java.util.Map;

/**
 * Created by ajGeGe on 17/8/4.
 */
public interface WxUtilService {


    String getToken(Map<String, String> param) throws Exception;

    void readInFormId(Map<String, String> param) throws Exception;

    /**
     * 收集咨询师信息
     *  openId <unique>
     *  email
     * @param parammap
     * @return
     */
    boolean readInCounselor(Map<String, String> parammap) throws Exception ;

    /**
     * 获取咨询师数据
     * @param parammap
     * @return
     */
    CounselorInfoVO findCounselor(Map<String, String> parammap);

    /**
     * 判断咨询师openId是否有效
     * @param counselorOpenId
     * @return
     */
    boolean isValidCounselor(String counselorOpenId);


    /**
     * 获取线上accessToken, 有的api需要线上的appid才能使用
     * @return
     */
    @Deprecated
    String getProdAccessToken();

    /**
     * 从公共地方拿到token
     * @return
     */
    String getAccessToken();

    /**
     * 从公共地方拿到token
     * @return
     */
    String getRefreshAccessToken();

    /**
     * 文本安全内容检测
     * @param content
     * @return
     */
    MsgCodeVO msgSecCheck(String content);

    /**
     * 图片安全内容检测
     * @param file
     * @return
     */
    MsgCodeVO imgSecCheck(File file);
}
