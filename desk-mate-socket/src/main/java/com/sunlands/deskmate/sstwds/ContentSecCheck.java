package com.sunlands.deskmate.sstwds;

import com.sunlands.deskmate.enums.MsgCodeType;
import com.sunlands.deskmate.service.WxUtilService;
import com.sunlands.deskmate.utils.StringUtil;
import com.sunlands.deskmate.vo.MsgCodeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * Created by li on 2018/5/11.
 */
@Slf4j
@Component
public class ContentSecCheck {


    @Resource(name = "DFASensitiveWordsChecker")
    private SensitiveWordsCheck sensitiveWordsCheck;

    /**
     * 文本内容是否违规, true违规, false不违规, 默认违规
     * @param words
     * @return
     */
//    public boolean checkMsgRisky(String content) {
//        if (StringUtils.isBlank(content)) return false;
//
//        MsgCodeVO msgCodeVO = wxUtilService.msgSecCheck(content);
//
//        log.info("checkMsgRisky(), content: {}, msgCodeVO: {}", content, msgCodeVO);
//
//        if (MsgCodeType.OK_CONTENT.getCode() == msgCodeVO.getCode()) {
//            return false;
//        }
//
//        if (MsgCodeType.RISKY_CONTENT.getCode() == msgCodeVO.getCode()) {
//            return true;
//        }
//
//        String checkResult = sensitiveWordsCheck.findSenstiveWords(content);
//        if (StringUtil.isEmpty(checkResult)) {
//
//            return false;
//        }
//
//        return true;
//    }

    public String filterSensitiveWords(String words){
        return sensitiveWordsCheck.replaceSenstiveWords(words, "*");
    }

    /**
     * 图片是否违规, true违规, false不违规, 默认不违规
     * @param imageFile
     * @return
     */
//    public boolean checkImgRisky(File imageFile) {
//        if (imageFile == null) return false;
//
//        MsgCodeVO msgCodeVO = wxUtilService.imgSecCheck(imageFile);
//
//        log.info("checkImgRisky(), image name: {}, msgCodeVO: {}", imageFile.getName(), msgCodeVO);
//
//        if (MsgCodeType.OK_CONTENT.getCode() == msgCodeVO.getCode()) {
//            return false;
//        }
//
//        if (MsgCodeType.RISKY_CONTENT.getCode() == msgCodeVO.getCode()) {
//            return true;
//        }
//
//
//        return false;
//    }

}
