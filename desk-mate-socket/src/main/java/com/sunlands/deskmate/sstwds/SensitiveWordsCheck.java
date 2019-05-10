package com.sunlands.deskmate.sstwds;

import com.sunlands.deskmate.sstwds.SensitiveModel;

public interface SensitiveWordsCheck {

	/**
	 * 重新加载数据
	 * 会重新构建敏感词树
	 */
	public void reloadWord();
	
	/**
	 * 新增一个敏感词
	 * @param word
	 */
	public void addSenstiveWord(SensitiveModel model);
	
	/**
	 * <pre>
	 * 将content中涉及到的敏感词全部替换为replaceWord
	 * 返回替换后的content
	 * </pre>
	 * 
	 * @param content
	 * @param replaceWord
	 * @return
	 */
	public String replaceSenstiveWords(String content, String replaceWord);
	
	/**
	 * 是否包含敏感词
	 * 
	 * @param content
	 * @return
	 */
	public boolean hasSenstiveWords(String content);

	/**
	 * 查找content中的第一个敏感词
	 * @param content
	 * @return
	 */
	public String findSenstiveWords(String content);

	/**
	 * 重新加载敏感词库
	 */
	public void reloadSenstiveWords();
}
