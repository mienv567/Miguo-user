package com.fanwe.utils;

import android.text.TextUtils;

import com.didikee.uilibs.Text.UICharacterCount;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by didik on 2016/8/23.
 */
public class MGStringFormatter {

    /**
     *
     * @param maybeInt 可能是int类型的值
     * @return error Integer.min_value
     */
    public static int getInt(String maybeInt) {
        if (maybeInt == null || maybeInt.equals("")) {
            return 0;
        }
        int a = 0;
        try {
            a = Integer.valueOf(maybeInt);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     *
     * @param maybeFloat 可能是float类型的值
     * @return error 0f
     */
    public static float getFloat(String maybeFloat) {
        if (maybeFloat == null || maybeFloat.equals("")) {
            return 0;
        }
        float a = 0f;
        try {
            a = Float.valueOf(maybeFloat);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 获取 1.0
     * @param maybeFloat
     * @return
     */
    public static String getFloat1(String maybeFloat) {
        String result = "0.0";
        if (!TextUtils.isEmpty(maybeFloat)) {
            int length = maybeFloat.length();
            if (maybeFloat.contains(".")) {
                int middle = maybeFloat.indexOf(".");
                int offset = length - middle;
                if (offset == 1) {
                    result = maybeFloat + "0";
                } else if (offset == 2) {
                    result = maybeFloat;
                } else if (offset > 2) {
                    result = maybeFloat.substring(0, middle + 2);
                }
            } else {
                result = maybeFloat + ".0";
            }
        }

        return result;
    }

    /**
     * 获取 1.00
     * @param maybeFloat
     * @return 1.00
     */
    public static String getFloat2(String maybeFloat) {
        String result = "0.00";
        if (!TextUtils.isEmpty(maybeFloat)) {
            int length = maybeFloat.length();
            if (maybeFloat.contains(".")) {
                int middle = maybeFloat.indexOf(".");
                int offset = length - middle;
                if (offset == 1) {
                    result = maybeFloat + "00";
                } else if (offset == 2) {
                    result = maybeFloat + "0";
                } else if (offset == 3) {
                    result = maybeFloat;
                } else if (offset > 3) {
                    result = maybeFloat.substring(0, middle + 3);
                }
            } else {
                result = maybeFloat + ".00";
            }
        }
        return result;
    }

    public static String getFloat2(double oldFloat) {
        BigDecimal bd = new BigDecimal(oldFloat);
        BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return getFloat2(bd2.toString());
    }

    /**
     *
     * @param time
     * @return
     */
    public static String getDate(String time) {
        String result;
        if (TextUtils.isEmpty(time)) {
            result = "";
        } else {
            Date date = new Date(Long.valueOf(time));
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            result = format.format(date);
        }
        return result;
    }

    public static String getLimitedString(String txt, int limitNum) {
        return getLimitedString(txt, limitNum, "...");
    }

    public static String getLimitedString(String txt, int limitNum, String endStr) {
        return getLimitedString(txt, limitNum, endStr, "");
    }

    /**
     * 获取截取限制的字符串
     * @param txt
     * @param limitNum
     * @param endStr
     * @return
     */
    public static String getLimitedString(String txt, int limitNum, String endStr, String
            emptyStr) {
        String result;
        if (!TextUtils.isEmpty(txt)) {
            float count = UICharacterCount.getCount(txt);
            if (count > limitNum) {
                result = txt.substring(0, limitNum);
                result = result + endStr;
            } else {
                result = txt;
            }
        } else {
            result = emptyStr;
        }
        return result;
    }

    /**
     * 获取限制的中文字符,适合中英混合的场景
     * @param originText 原文字
     * @param limitNum 限制长度
     * @param endStr 如果超出限制,尾部放置什么
     * @param emptyStr 如果原文字是空,那么返回该值
     * @return str
     */
    public static String getLimitedChinese(String originText, int limitNum, String endStr, String
            emptyStr) {
        String result;
        if (TextUtils.isEmpty(originText)) {
            result = emptyStr;
        } else {
            int length = originText.length();
            int charCount = 0;
            int cur = 0;
            for (int i = 0; i < length; i++) {
                char curr = originText.charAt(i);
                boolean chinese = UICharacterCount.isChinese(curr);
                if (chinese) {
                    charCount += 2;
                } else {
                    charCount += 1;
                }
                if (charCount > limitNum * 2) {
                    cur = i;//如果不足则cur=0;
                    break;
                }
            }
            if (cur == 0) {
                return originText;
            }
            result = originText.substring(0, cur);
            result += endStr;
        }
        return result;
    }
    public static String getLimitedChinese(String originText, int limitNum, String endStr){
        return getLimitedChinese(originText,limitNum,endStr,"");
    }
    public static String getLimitedChinese(String originText, int limitNum){
        return getLimitedChinese(originText,limitNum,"...","");
    }
}
