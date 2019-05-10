package com.sunlands.deskmate.sstwds;

import com.sunlands.deskmate.sstwds.SensitiveModel;

import java.util.List;

/**
 * 敏感词读取接口
 * 
 * @author sunyx
 *
 */
public interface SensitiveWordsReader {

	/**
	 * 读出所有敏感词
	 * 
	 * @return
	 */
	public List<SensitiveModel> read();
}
