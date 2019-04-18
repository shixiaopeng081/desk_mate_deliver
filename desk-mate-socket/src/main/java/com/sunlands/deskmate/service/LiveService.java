package com.sunlands.deskmate.service;

/**
 * @author lishuai
 *
 */
public interface LiveService {
    
    /**
     * 更新在线状态
     * 
     * @param classType
     * @param onlinePerson
     */
    public void refreshOnlinePersonNum(String classType, int onlinePerson);

}
