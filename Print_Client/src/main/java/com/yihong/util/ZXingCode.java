package com.yihong.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.yihong.bean.BarCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

/**
 * 条形码生成工具类
 *
 * @ClassName ZXingCode
 * @Author ZhangGang
 * @Date 2019/1/23 10:34
 **/
public class ZXingCode {
    /**
     * 条形码编码
     *
     * @param contents 条形码内容
     * @param width    条形码宽度
     * @param height   条形码高度
     */
    public static BufferedImage encode(BarCode barCode) {

        String contents = barCode.getContent();
        int width = barCode.getWidth();
        int height = barCode.getHeight();

        BitMatrix bitMatrix;
        try {
            bitMatrix = new Code128Writer().encode(contents, BarcodeFormat.CODE_128, width, height, null);
            MatrixToImageWriter.toBufferedImage(bitMatrix);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            if (image.getWidth() > width) {
                BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = bufImg.createGraphics();
                g2d.drawImage(image, 0, 0, width, height, null);
                return bufImg;
            } else {
                return image;
            }
        } catch (Exception e) {
            System.out.println("Exception Found." + e.getMessage());
        }
        return null;
    }

//    public static void main(String[] argvs) throws Exception{
//        BufferedImage img = encode("270000-00255584-0001", 180, 35);
//        FileOutputStream fos = new FileOutputStream("E:/barcode.png");
//        ImageIO.write(img, "png", fos);
//        fos.close();
//        System.out.println("ok");
//    }
//    public static BufferedImage encode(String contents, int width, int height) {
//        BitMatrix bitMatrix;
//        try {
//            bitMatrix = new Code128Writer().encode(contents, BarcodeFormat.CODE_128, width, height, null);
//            MatrixToImageWriter.toBufferedImage(bitMatrix);
//            BufferedImage img = MatrixToImageWriter.toBufferedImage(bitMatrix);
//
//            FileOutputStream fos = new FileOutputStream("E:/s-barcode.png");
//            ImageIO.write(img, "png", fos);
//            fos.close();
//
//            if(img.getWidth() > width){
//                BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//                Graphics2D g = newImg.createGraphics();
//                g.drawImage(img, 0, 0, width, height, null);
//                return newImg;
//            }else{
//                return img;
//            }
//
//        } catch (Exception e) {
//            System.out.println("Exception Found." + e.getMessage());
//        }
//        return null;
//    }

}
