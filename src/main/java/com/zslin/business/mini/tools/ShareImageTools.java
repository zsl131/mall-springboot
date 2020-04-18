package com.zslin.business.mini.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

/**
 * 分享图片工具类
 */
@Component
public class ShareImageTools {

    @Autowired
    private QrTools qrTools;

    public BufferedImage createImage(String proTitle, String price, String nickname, String remark,
                            String proImgUrl, String headUrl, String page, String scene) {
        try {
            String fontName = "微软雅黑";
            BufferedImage bi = new BufferedImage(800, 740, BufferedImage.TYPE_INT_BGR);

            Graphics g = bi.getGraphics();
            g.setColor(new Color(255,255,255));
            g.fillRect(0,0,bi.getWidth(),460); //设置上部份背景色

            //产品图片
            BufferedImage proImg = zoomImage(new URL(proImgUrl).openStream(), 800);
            g.drawImage(proImg, 0,0,null);


            g.setColor(new Color(233,233,233));
            g.fillRect(0,460,bi.getWidth(),280); //设置下部份背景色
            //g.dispose();


            //小程序码
            BufferedImage qr = zoomImage(qrTools.getQrB(page, scene), 190);
//            BufferedImage qr = qrTools.getQrB(page, scene);
            g.drawImage(qr, 580,530,null);

            //满山晴字样
            BufferedImage imgIcon = ImageIO.read(new ClassPathResource("logo-msq.png").getInputStream());
            g.drawImage(imgIcon, 30,480,null);

            //头像
            BufferedImage srcImg = ImageIO.read(new URL(headUrl)); //读取图片
            BufferedImage newImage=new BufferedImage(90, 90,BufferedImage.TYPE_INT_RGB);
            newImage.createGraphics().drawImage(srcImg.getScaledInstance(90, 90, Image.SCALE_SMOOTH), 0, 0, null);

            g.drawImage(newImage, 30,630,null);

            Font font = new Font(fontName, Font.PLAIN, 34);
            g.setColor(new Color(39,39,39));
            g.setFont(font);
            g.drawString(proTitle, 150, 515); //写字是从下往上的占高度

            g.setColor(new Color(255, 0, 0));
            g.setFont(new Font(fontName, Font.PLAIN, 50));
            g.drawString(price, 40, 590);

            g.setColor(new Color(100,100,100));
            g.setFont(font);
            g.drawString(nickname, 135, 670);

            g.setColor(new Color(126,126,126));
            g.setFont(new Font(fontName, Font.PLAIN, 26));
            g.drawString(remark, 135, 710);

            g.dispose();

//            ImageIO.write(bi, "jpg", new File("D:/temp/test/share.jpg"));
            return bi;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage zoomImage(InputStream is, int w) throws Exception {
        BufferedImage srcImg = ImageIO.read(is); //读取图片
        return zoomImage(srcImg, w);
    }

    private BufferedImage zoomImage(BufferedImage srcImg, int w) {
        int sw = srcImg.getWidth();
        int sh = srcImg.getHeight();

        int dh = (int)(w*sh/sw); //设置新高度

        BufferedImage newImage=new BufferedImage(w, dh,BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(srcImg.getScaledInstance(w, dh, Image.SCALE_SMOOTH), 0, 0, null);

        return newImage;
    }
}
