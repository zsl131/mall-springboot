package com.zslin.core.common;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通用工具类
 */
@Slf4j
public class NormalTools {

    public static String getNow(String pattern) {
        /*SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String res =  sdf.format(new Date());
        return res;*/
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        String res = df.format(LocalDateTime.now());
        return res;
    }

    public static String curDatetime() {
        return getNow("yyyy-MM-dd HH:mm:ss");
    }

    public static String curDate() {
        return getNow("yyyy-MM-dd");
        /*LocalDate localDate = LocalDate.now();
        return localDate.toString();*/
    }

    public static String getFileType(String fileName) {
        if(fileName!=null && fileName.indexOf(".")>=0) {
            return fileName.substring(fileName.lastIndexOf("."), fileName.length());
        }
        return "";
    }

    /**
     * 判断文件是否为图片文件
     * @param fileName
     * @return
     */
    public static Boolean isImageFile(String fileName) {
        String [] img_type = new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        if(fileName==null) {return false;}
        fileName = fileName.toLowerCase();
        for(String type : img_type) {
            if(fileName.endsWith(type)) {return true;}
        }
        return false;
    }

    /*public static String curDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }*/

    /**
     * 生成两位小数的数字
     * @param d double类型的数字
     * @return
     */
    public static double buildPoint(double d) {
        /*BigDecimal bg = new BigDecimal(d);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        f1 = Math.rint(f1);
        return f1;*/
        return Math.ceil(d);
    }

    /** 判断是否为空 */
    public static boolean isNull(String val) {
        return (val==null || "".equals(val));
    }

    /** 有一个为空则为空 */
    public static boolean isNullOr(String ...values) {
        boolean res = false;
        for(String val : values) {
            if(isNull(val)) {res = true; break;}
        }
        return res;
    }
}
