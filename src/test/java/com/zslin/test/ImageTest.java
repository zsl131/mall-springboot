package com.zslin.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class ImageTest {

    @Test
    public void test01() {
        BufferedImage bi = new BufferedImage(800, 1137, BufferedImage.TYPE_INT_BGR);
        File file = new File("D:/temp/test.jpg");
        try {
            if(file.exists()) {
                file.delete();
                file.createNewFile();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        writeImage(bi, "jpg", file);
        System.out.println("绘图成功");
    }

    private boolean writeImage(BufferedImage bi, String picType, File file) {
        Graphics g = bi.getGraphics();
        g.setColor(new Color(255,255,255));
        g.fillRect(0,0,bi.getWidth(),bi.getHeight()); //设置背景色
        g.dispose();
        boolean val = false;
        try {
            val = ImageIO.write(bi, picType, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return val;
    }

    /** 绽放照片 */
    @Test
    public void test02() throws Exception {
        zoomImage("D:/temp/pro.jpg", "D:/temp/pro-1.jpg", 800);
        zoomImage("D:/temp/pro.jpg", "D:/temp/pro-2.jpg", 300);
    }

    private void zoomImage(String src,String dest,int w) throws Exception {
        File srcFile = new File(src);
        File destFile = new File(dest);

        BufferedImage srcImg = ImageIO.read(srcFile); //读取图片
        int sw = srcImg.getWidth();
        int sh = srcImg.getHeight();

        System.out.println(sw+"========"+sh);

        int dh = (int)(w*sh/sw); //设置新高度

        System.out.println(w+"----------"+dh);


        BufferedImage newImage=new BufferedImage(w, dh,BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(srcImg.getScaledInstance(w, dh, Image.SCALE_SMOOTH), 0, 0, null);

        ImageIO.write(newImage, dest.substring(dest.lastIndexOf(".")+1), destFile);
/*
        Image Itemp = srcImg.getScaledInstance(w, dh, srcImg.SCALE_FAST);//设置缩放目标图片模板
//        Image Itemp = srcImg.getScaledInstance(bufImg.getWidth(), bufImg.getHeight(), bufImg.SCALE_SMOOTH);
        *//*wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();*//*


        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(w, dh), null);
        Itemp = ato.filter(srcImg, null);
        try {
            ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    }
}
