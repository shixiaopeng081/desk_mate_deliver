//package com.sunlands.deskmate.service.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.sunlands.deskmate.enums.MsgCodeType;
//import com.sunlands.deskmate.service.WxUtilService;
//import com.sunlands.deskmate.utils.DozerMapper;
//import com.sunlands.deskmate.utils.HttpClientUtil;
//import com.sunlands.deskmate.utils.StringUtil;
//import com.sunlands.deskmate.vo.CounselorInfoVO;
//import com.sunlands.deskmate.vo.MsgCodeVO;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.*;
//import java.util.List;
//
///**
// * Created with IntelliJ IDEA.
// * User: xiyuefei
// * Date: 2017/10/10
// * Time: 上午10:56
// */
//@Service
//@Slf4j
//public class WxUtilServiceImp implements WxUtilService {
//
//    private static final int wx_msg_ok = 0;
//    private static final int wx_msg_risky = 87014;
//    private static final int wx_token_invalid = 40001;
//
//    /** 微信图片内容检测, 宽或者高, 最大不能超过1334, 减一点 */
////    private static final int wx_image_max_side1 = 1320;
////    private static final int wx_image_max_side2 = 742;
//
//    private static final int wx_image_max_side1 = 1334;
//    private static final int wx_image_max_side2 = 750;
//
//    private static final double wx_image_standard_ratio = wx_image_max_side1 * 1.0 / wx_image_max_side2;
//
//    @Autowired
//    RedisTemplate redisTemplate;
//
//
//    private String apiContentCheck(String token, String content) {
//        String url = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token=" + token;
//        Map<String, String> params = new HashMap<>();
//        params.put("content", content);
//
//        try {
//            String result = HttpClientUtil.postJSON(url, params);
//
//            log.info("apiContentCheck(), content: {}, token: {}", content, token);
//
//            return result;
//        } catch (Exception e) {
//            log.error("apiContentCheck(), error, token: {}, content: {}, error: {}", token, content, e);
//        }
//
//        return null;
//    }
//
//
//    private MsgCodeVO processContentCheckResult(String jsonString) {
//
//        MsgCodeVO msgCodeVO = new MsgCodeVO();
//        msgCodeVO.setCode(MsgCodeType.REQ_FAIL.getCode());
//        msgCodeVO.setMsg(MsgCodeType.REQ_FAIL.getMsg());
//
//        if (StringUtils.isBlank(jsonString)) return msgCodeVO;
//
//        JSONObject jsonObject = JSONObject.parseObject(jsonString);
//        if (jsonObject.containsKey("errcode") && jsonObject.containsKey("errmsg")) {
//            int errcode = jsonObject.getIntValue("errcode");
//            String errmsg = jsonObject.getString("errmsg");
//            if (errcode == wx_msg_ok) {
//
//                msgCodeVO.setCode(MsgCodeType.OK_CONTENT.getCode());
//                msgCodeVO.setMsg(MsgCodeType.OK_CONTENT.getMsg());
//
//                return msgCodeVO;
//            }
//
//            if (errcode == wx_msg_risky) {
//
//                msgCodeVO.setCode(MsgCodeType.RISKY_CONTENT.getCode());
//                msgCodeVO.setMsg(MsgCodeType.RISKY_CONTENT.getMsg());
//
//                return msgCodeVO;
//            }
//
//            msgCodeVO.setCode(errcode);
//            msgCodeVO.setMsg(errmsg);
//        }
//
//        return msgCodeVO;
//    }
//
//    private File validAndProcessImgFile(File file) {
//
//        if (file == null) return null;
//
//        try {
//            BufferedImage image = ImageIO.read(file);
//
//            if (image == null) return null;
//
//            int imageWidth = image.getWidth();
//            int imageHeight = image.getHeight();
//
//            int side1 = (imageWidth >= imageHeight ? imageWidth : imageHeight);
//            int side2 = (side1 == imageWidth ? imageHeight : imageWidth);
//
//            if (side1 <= wx_image_max_side1 && side2 <= wx_image_max_side2) return file;
//
//            int resize1 = wx_image_max_side1;
//            int resize2 = wx_image_max_side2;
//
//            if (side1 > wx_image_max_side1 && side2 <= wx_image_max_side2) {
//                resize1 = wx_image_max_side1;
//                resize2 = wx_image_max_side1 * side2 / side1;
//            }
//
//            if (side2 > wx_image_max_side2 && side1 <= wx_image_max_side1) {
//                resize2 = wx_image_max_side2;
//                resize1 = wx_image_max_side2 * side1 / side2;
//            }
//
//            if (side1 > wx_image_max_side1 && side2 > wx_image_max_side2) {
//
//                if (side1 * 1.0 / side2 >= wx_image_standard_ratio) {
//
//                    //长边按照1334缩放
//                    resize1 = wx_image_max_side1;
//                    resize2 = wx_image_max_side1 * side2 / side1;
//
//
//                } else {
//
//                    //短边按照750缩放
//                    resize2 = wx_image_max_side2;
//                    resize1 = wx_image_max_side2 * side1 / side2;
//                }
//            }
//
//            int finalWidth = (imageWidth >= imageHeight ? resize1 : resize2);
//            int finalHeight = (finalWidth == resize1 ? resize2 : resize1);
//
////            int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
//            int type = BufferedImage.TYPE_INT_RGB;
//
//            BufferedImage finalImage = new BufferedImage(finalWidth, finalHeight, type);
//            Graphics2D g = finalImage.createGraphics();
//            g.drawImage(image, 0, 0, finalWidth, finalHeight, null);
//            g.dispose();
//
//            //必须保持格式一致
//            String postfix = file.getAbsolutePath();
//            postfix = postfix.substring(postfix.lastIndexOf(".") + 1).toLowerCase();
//
//            String fileName = "wx-" + UUID.randomUUID() + "." + postfix;
//
//            File finalFile = new File(fileName);
//
//            FileOutputStream fos = new FileOutputStream(finalFile);
//
//            ImageIO.write(finalImage, postfix, fos);
//            fos.close();
//
//            return finalFile;
//
//        } catch (Exception e) {
//
//            log.error("validAndProcessImgFile(), e: {}", e);
//
//        }
//
//        return null;
//
//    }
//
//    public static void main(String[] args) throws IOException {
//
////        File file = new File("my001.txt");
////
////        FileOutputStream fileOutputStream = new FileOutputStream(file);
////        fileOutputStream.write("hello world".getBytes());
////        fileOutputStream.close();
////
////        System.out.println(file.getPath());
////        System.out.println(file.getAbsolutePath());
////        IntStream.rangeClosed(1, 100).forEach(i -> System.out.println(UUID.randomUUID()));
//
//
//        File imgFile = new File("/Users/li/Downloads/d_picture/QQ20170729-1@2x.png");
//        System.out.println("file size: " + imgFile.length());
//
//    }
//
//}
//
