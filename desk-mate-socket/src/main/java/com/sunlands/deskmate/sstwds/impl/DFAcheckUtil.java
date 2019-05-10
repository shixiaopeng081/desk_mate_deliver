package com.sunlands.deskmate.sstwds.impl;

import com.sunlands.deskmate.sstwds.SensitiveModel;
import com.sunlands.deskmate.sstwds.SensitiveWordsCheck;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * DFA实现的敏感词过滤 静态代理类，方便使用
 * 
 * @author sunyx
 * 
 */
public class DFAcheckUtil {
	
	protected static final Log         logger = LogFactory.getLog(DFAcheckUtil.class);
	
	public static String DEFAULT_TYPE = "default_type";
	
	private static Map<String, SensitiveWordsCheck> _instances = Collections
			.synchronizedMap(new HashMap<String, SensitiveWordsCheck>());

	public static void register(String type, SensitiveWordsCheck checker) {
		logger.error("register type=" + type);
		if(StringUtils.isBlank(type)){
			type = DEFAULT_TYPE;
		}
		if(!_instances.containsKey(type)){
			_instances.put(type, checker);
		}
	}

	public static void addSenstiveWord(SensitiveModel word) {
		if (null != _instances && _instances.containsKey(DEFAULT_TYPE)) {
			_instances.get(DEFAULT_TYPE).addSenstiveWord(word);
		}
	}

	/**
	 * wanning: 此方法会导致性能下降，谨慎使用
	 */
	public static void reloadWord() {
		logger.info("reloadWord all ");
		if (null != _instances && !_instances.isEmpty()) {
			Set<String> keys = _instances.keySet();
			logger.info("reloadWord all keys=" + keys);
			for(String key : keys){
				SensitiveWordsCheck swc = _instances.get(key);
				swc.reloadWord();
			}
			_instances.get(DEFAULT_TYPE).reloadWord();
		}
	}

	public static String replaceSenstiveWords(String content, String replaceWord) {
		if (null != _instances && _instances.containsKey(DEFAULT_TYPE)) {
			return _instances.get(DEFAULT_TYPE).replaceSenstiveWords(content, replaceWord);
		}
		return content;
	}

	public static boolean hasSenstiveWords(String content) {
		if (null != _instances && _instances.containsKey(DEFAULT_TYPE)) {
			return _instances.get(DEFAULT_TYPE).hasSenstiveWords(content);
		}
		return false;
	}
	
	
	// ---------重载，用于有多个实现----------------------------
	public static void addSenstiveWord(String type, SensitiveModel word) {
		if (null != _instances && _instances.containsKey(type)) {
			_instances.get(type).addSenstiveWord(word);
		}
	}

	public static String replaceSenstiveWords(String type, String content, String replaceWord) {
		if (null != _instances && _instances.containsKey(type)) {
			return _instances.get(type).replaceSenstiveWords(content, replaceWord);
		}
		return content;
	}

	public static boolean hasSenstiveWords(String type, String content) {
		if (null != _instances && _instances.containsKey(type)) {
			return _instances.get(type).hasSenstiveWords(content);
		}
		return false;
	}
	//---------------------------------------------------
}
