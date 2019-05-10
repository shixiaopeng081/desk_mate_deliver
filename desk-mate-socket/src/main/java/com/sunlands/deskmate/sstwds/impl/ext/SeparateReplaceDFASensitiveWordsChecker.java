package com.sunlands.deskmate.sstwds.impl.ext;

import com.sunlands.deskmate.sstwds.SensitiveModel;
import com.sunlands.deskmate.sstwds.impl.DFASensitiveWordsChecker;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class SeparateReplaceDFASensitiveWordsChecker extends
		DFASensitiveWordsChecker {

	protected String formatReplaceWord(SensitiveModel sensitiveWord, int index,
									   int length, String replaceWord) {
		if (sensitiveWord != null) {
			if (StringUtils.isNotBlank(sensitiveWord.getReplaceWord())) {
				return sensitiveWord.getReplaceWord();
			} else {
				return sensitiveWord.getFilterWord();
			}
		}
		return StringUtils.EMPTY;
	}
}
