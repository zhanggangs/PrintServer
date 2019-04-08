package com.yihong.util;

import com.yihong.bean.BarCode;
import com.yihong.bean.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

/**
 * 绘制打印的内容
 *
 * @ClassName DrawPrintContent
 * @Author ZhangGang
 * @Date 2019/1/23 9:57
 **/
public class DrawPrintContent implements Printable {

    public static List<Text> textList = new ArrayList();
    public static List<BarCode> barCodeList = new ArrayList();

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {

        if (pageIndex != 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2 = (Graphics2D) graphics;
        g2.scale(90.0 / 203, 90.0 / 203);
        //g2.scale(1, 1);

        double startX = pageFormat.getImageableX();
        double startY = pageFormat.getImageableY();

        //打印字体
        for (int i = 0; i < textList.size(); i++) {
            Text text = textList.get(i);
            Font font = new Font(text.getFontName(), Font.PLAIN, text.getFontSize());
            g2.setFont(font);
            g2.drawString(text.getContent(), (float) (startX + text.getX()), (float) (startY + text.getY()));
        }

        //打印条形码图片
        for (int i = 0; i < barCodeList.size(); i++) {
            BarCode barCode = barCodeList.get(i);
            //BufferedImage barCodeImage = ZXingCode.encode(barCode);
            BufferedImage barCodeImage = barcode4jUtil.geneBarcode(barCode);
            boolean b = g2.drawImage(barCodeImage, barCode.getX(), barCode.getY(), null);
        }
        return PAGE_EXISTS;
    }

}
