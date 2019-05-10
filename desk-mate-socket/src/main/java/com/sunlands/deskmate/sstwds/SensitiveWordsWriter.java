package com.sunlands.deskmate.sstwds;

import com.sunlands.deskmate.sstwds.SensitiveModel;

/**
 * 敏感词写入接口
 * 
 * @author sunyx
 *
 */
public interface SensitiveWordsWriter {

	/**
	 * 写入一个敏感词
	 * 
	 * @param oneWord
	 */
	public void writeWord(SensitiveModel oneWord);
}
