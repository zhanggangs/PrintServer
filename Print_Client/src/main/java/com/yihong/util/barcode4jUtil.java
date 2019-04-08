package com.yihong.util;

import com.yihong.bean.BarCode;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.*;
import java.awt.image.BufferedImage;

public class barcode4jUtil {

    /**
     * @return
     * @Author ZhangGang
     * @Date 2019/2/21 13:24
     * @Param code 条形码内容
     * @Param barHeight 条形码高度
     * @Param moduleWidth 条形码密度
     **/
    public static BufferedImage geneBarcode(BarCode barCode) {

        String code = barCode.getContent();
        Integer barWidth = barCode.getWidth();
        Integer barHeight = barCode.getHeight();
        Float moduleWidth = barCode.getModuleWidth();
        Integer dpi = barCode.getDpi();

        BitmapCanvasProvider canvas = null;
        try {
            //创建条形码bean
            Code128Bean bean = new Code128Bean();
            //设置条形码的密度
            bean.setModuleWidth(UnitConv.in2mm(moduleWidth / dpi));
            //设置条形码高度
            bean.setBarHeight(barHeight);
            //宽度正好一个像素
            //bean.setWideFactor(3);
            //不显示条码下方字体
            bean.setFontSize(0);
            //两边空白区
            bean.doQuietZone(false);

            canvas = new BitmapCanvasProvider(null, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            //生成条形码
            bean.generateBarcode(canvas, code);
            //信号产生结束
            canvas.finish();
            //如果条码太长则进行缩小
            BufferedImage image = canvas.getBufferedImage();

            return image;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
