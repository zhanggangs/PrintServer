package com.yihong.controller;

import com.alibaba.fastjson.JSON;
import com.yihong.bean.SendMessage;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.yihong.util.Messager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

/**
 * 打印控制类
 *
 * @ClassName PrintController
 * @Author ZhangGang
 * @Date 2019/2/15 9:23
 **/
@Controller
public class PrintController {

    @GetMapping("/test")
    public String test() {
        return "views/test";
    }

    @Autowired
    JmsTemplate jmsTemplate;

    /**
     * 打印标签
     *
     * @return
     * @Author ZhangGang
     * @Date 2019/2/13 13:27
     * @Param queueName 打印队列的名称
     * @Param printer 打印机的名称
     * @Param content 需要打印的内容(根据需要自定义类型)
     **/
    @PostMapping("/printLabel")
    @ResponseBody
    public String printLabel(String queueName, String printer, String content) {
        try {
            //创建SendMessage类实例对象
            SendMessage sendMessage = new SendMessage();
            //设置打印机名称
            sendMessage.setPrinter(printer);
            sendMessage.setPrinterType("Label");
            //设置打印条形码(条形码内容,x坐标,y坐标,条形码宽度,条形码高度)
            sendMessage.setBarCode("270000-00255584-0001", 10, 10, 200, 10, 2f);
            //设置打印文本(文本内容,x坐标,y坐标,字体样式,字体大小)
            sendMessage.setText("270000-00255584-0001", 10, 111, "微软雅黑", 26);
            sendMessage.setText("上海联易德装饰有限公司", 10, 138, "微软雅黑", 26);
            sendMessage.setText("310117003914965", 10, 165, "微软雅黑", 26);
            sendMessage.setText("270000-00255584", 10, 192, "微软雅黑", 26);
            sendMessage.setText("20180713", 10, 219, "微软雅黑", 26);
            sendMessage.setText("设立", 200, 219, "微软雅黑", 26);
            //将打印内容发送给指定队列
            jmsTemplate.convertAndSend(queueName, JSON.toJSONString(sendMessage));
            //返回提示信息
            return JSON.toJSONString(Messager.GetOkMessage(""));

        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(Messager.GetFailMessage(400, "消息服务出现异常"));
        }
    }

    /**
     * 打印A4
     *
     * @return
     * @Author ZhangGang
     * @Date 2019/2/13 13:27
     * @Param
     **/
    @PostMapping("/printA4")
    @ResponseBody
    public String printA4(String queueName, String printer, String content) {

        //生成PDF文件
        File file = new File(createPDF(content));
        //创建SendMessage类实例对象
        SendMessage sendMessage = new SendMessage();
        //设置打印机名称
        sendMessage.setPrinter(printer);
        //设置转换成byte[]的文件
        sendMessage.setBytes(getFileToByte(file));

        try {
            //将打印内容发送给指定队列
            jmsTemplate.convertAndSend(queueName, JSON.toJSONString(sendMessage));
            return JSON.toJSONString(Messager.GetOkMessage(""));
        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(Messager.GetFailMessage(400, "消息服务出现异常"));
        }
    }


    /**
     * 将文件转换为byte[]类型
     *
     * @return
     * @Author ZhangGang
     * @Date 2019/2/13 13:27
     * @Param
     **/
    public static byte[] getFileToByte(File file) {
        byte[] by = new byte[(int) file.length()];
        try {
            InputStream is = new FileInputStream(file);
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            byte[] bb = new byte[2048];
            int ch;
            ch = is.read(bb);
            while (ch != -1) {
                bytestream.write(bb, 0, ch);
                ch = is.read(bb);
            }
            by = bytestream.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return by;
    }


    /**
     * 创建生成PDF文件
     *
     * @return
     * @Author ZhangGang
     * @Date 2019/2/15 9:39
     * @Param
     **/
    public static String createPDF(String content) {

        Document document = null;
        String pathName = "temp.pdf";
        try {

            // 创建Document对象(页面的大小为A4,左、右、上、下的页边距为10)
            document = new Document(PageSize.A4, 10, 10, 10, 10);

            //建立一个书写器(Writer) 与document对象关联，通过书写器(Writer) 可以将文档写入到磁盘中
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pathName));

            //设置中文
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            //设置字体大小和样式
            Font font = new Font(bfChinese, 13, Font.NORMAL);

            //打开文档
            document.open();

            // 添加表格，4列
            PdfPTable table = new PdfPTable(4);
            //// 设置表格宽度比例为%100
            table.setWidthPercentage(100);
            // 设置表格的宽度
            table.setTotalWidth(500);
            // 也可以每列分别设置宽度
            table.setTotalWidth(new float[]{160, 70, 130, 100});
            // 锁住宽度
            table.setLockedWidth(true);
            // 设置表格上面空白宽度
            table.setSpacingBefore(10f);
            // 设置表格下面空白宽度
            table.setSpacingAfter(10f);
            // 设置表格默认为无边框
            table.getDefaultCell().setBorder(0);
            PdfContentByte cb = writer.getDirectContent();

            PdfPCell title = new PdfPCell(new Paragraph("title"));
            //title.setBorder(Rectangle.NO_BORDER);
            title.setRowspan(4);
            title.setFixedHeight(30);
            title.setHorizontalAlignment(Element.ALIGN_CENTER);
            title.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(title);

            // 构建每个单元格
            PdfPCell cell1 = new PdfPCell(new Paragraph("Cell 1"));
            // 边框颜色
            cell1.setBorderColor(BaseColor.BLUE);
            // 设置背景颜色
            cell1.setBackgroundColor(BaseColor.ORANGE);
            // 设置跨两行
            cell1.setRowspan(2);
            // 设置距左边的距离
            cell1.setPaddingLeft(10);
            // 设置高度
            cell1.setFixedHeight(20);
            // 设置内容水平居中显示
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            // 设置垂直居中
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("Cell 2"));
            cell2.setBorderColor(BaseColor.GREEN);
            cell2.setPaddingLeft(10);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("Cell 3"));
            cell3.setBorderColor(BaseColor.RED);
            cell3.setPaddingLeft(10);
            // 设置无边框
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell3);

            // 在表格添加图片
            PdfPCell cell4 = new PdfPCell(new Paragraph("Cell 4"));
            cell4.setBorderColor(BaseColor.RED);
            cell4.setPaddingLeft(10);
            cell4.setFixedHeight(30);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell4);

            // 增加一个条形码到表格
            Barcode128 code128 = new Barcode128();
            code128.setCode("14785236987541");
            code128.setCodeType(Barcode128.CODE128);
            // 生成条形码图片
            Image code128Image = code128.createImageWithBarcode(cb, null, null);
            // 加入到表格
            PdfPCell cellcode = new PdfPCell(code128Image, true);
            cellcode.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellcode.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellcode.setFixedHeight(30);
            table.addCell(cellcode);

            PdfPCell cell5 = new PdfPCell(new Paragraph("Cell 5"));
            cell5.setPaddingLeft(10);
            // 设置占用列数
            cell5.setColspan(2);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell5);
            document.add(table);

        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (DocumentException de) {
            de.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭文档
            if (document != null) {
                document.close();
            }
        }
        return pathName;
    }

}
