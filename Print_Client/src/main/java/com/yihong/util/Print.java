package com.yihong.util;

import com.alibaba.fastjson.JSON;
import com.yihong.bean.BarCode;
import com.yihong.bean.SendMessage;
import com.yihong.bean.Text;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.*;
import javax.jms.Queue;
import javax.print.*;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.Sides;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打印工具类
 *
 * @ClassName Print
 * @Author ZhangGang
 * @Date 2019/1/23 9:57
 **/
public class Print {

    private static String NAME = ReadConfig.getConfig("JmsMessageReceiver.name");
    private static String IP = ReadConfig.getConfig("JmsMessageReceiver.ip");
    private static String PORT = ReadConfig.getConfig("JmsMessageReceiver.port");
    private static String USERNAME = ReadConfig.getConfig("JmsMessageReceiver.userName");
    private static String PASSWORD = ReadConfig.getConfig("JmsMessageReceiver.password");

    private JFrame frame = new JFrame();

    private JButton btnNewButton = new JButton();

    private static JLabel lblNewLabel;

    public void createAndShowGUI() {
        try {
            Print window = new Print();
            window.frame.setVisible(true);

            //开启消息接收监听
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, "tcp://" + IP + ":" + PORT);
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 消息消费者： 接收消息
            Queue queue = session.createQueue(NAME);
            MessageConsumer consumer = session.createConsumer(queue);
            consumer.setMessageListener(new ConsumerMessageListener(NAME));
            connection.start();

        } catch (Exception e) {
            e.printStackTrace();
            lblNewLabel.setText("<html><body><p align=\"center\">打印服务启动失败,<br>请检查端口\"" + PORT + "\"是否被占用</p></body></html>");
            System.out.println("启动失败,请查看配置文件是否配置正确");
            lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 20));
            lblNewLabel.setForeground(Color.red);
            JOptionPane.showMessageDialog(null, "打印服务启动失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Create the application.
     */
    public Print() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        frame.getContentPane().setBackground(Color.WHITE);
        frame.setBackground(Color.WHITE);
        frame.setTitle("打印服务客户端");
        frame.setForeground(Color.LIGHT_GRAY);
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        btnNewButton.setText("确定");
        btnNewButton.setBounds(170, 185, 105, 42);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setExtendedState(JFrame.ICONIFIED);
            }
        });
        frame.getContentPane().add(btnNewButton);

        lblNewLabel = new JLabel();
        lblNewLabel.setText("<html><body><p align=\"center\">主机名:" + NAME + "<br>--打印服务已启动--</p></body></html>");
        lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 30));
        lblNewLabel.setForeground(new Color(60, 179, 113));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(43, 67, 359, 108);
        frame.getContentPane().add(lblNewLabel);
    }

    /**
     * 设置文本
     *
     * @Author ZhangGang
     * @Date 2019/1/23 14:01
     * @Param content 文本内容
     * @Param x 相对于图片区域起点x轴坐标
     * @Param y 相对于图片区域起点y轴坐标
     * @Param fontName 字体名称
     * @Param fontSize 字体大小
     **/
    public void addText(Text text) {
        DrawPrintContent.textList.add(text);
    }

    /**
     * 设置条形码
     *
     * @Author ZhangGang
     * @Date 2019/1/23 14:09
     * @Param content 条形码内容
     * @Param x 相对于图片区域起点x轴坐标
     * @Param y 相对于图片区域起点y轴坐标
     * @Param width 条形码宽度
     * @Param height 条形码高度
     **/
    public void addBarCode(BarCode barCode) {
        DrawPrintContent.barCodeList.add(barCode);
    }

    /**
     * 接收消息,执行打印
     *
     * @Author ZhangGang
     * @Date 2019/2/13 9:39
     **/
    public class ConsumerMessageListener implements MessageListener {

        private String consumerName;

        public ConsumerMessageListener(String consumerName) {
            this.consumerName = consumerName;
        }

        public void onMessage(Message message) {
            TextMessage textMessage = (TextMessage) message;

            String msg = null;

            try {
                msg = textMessage.getText();
            } catch (JMSException e) {
                e.printStackTrace();
            }

            //判断接收消息是否为空
            if (msg == null || "".equals(msg)) {
                return;
            }

            //解析发送过来的信息
            SendMessage sendMessage = JSON.parseObject(msg, SendMessage.class);

            String printer = sendMessage.getPrinter();
            if (printer != null) {
                if ("Label".equals(sendMessage.getPrinterType())) {
                    LabelPrint(sendMessage.getBarCode(), sendMessage.getText(), printer);
                } else {
                    byte[] bytes = sendMessage.getBytes();
                    String type = getType(bytes);
                    //如果获取文件图片类型为空,则设置类型为pdf
                    if (type != null) {
                        //读取二进制,生成文件
                        getFileByBytes(bytes, "printTemp", "temp." + type);
                        //获取文件调用打印机打印
                        imagePrint(new File("printTemp\\temp." + type), printer);
                    } else {
                        type = "pdf";
                        //读取二进制,生成文件
                        getFileByBytes(bytes, "printTemp", "temp." + type);
                        //获取文件调用打印机打印
                        PDFprint(new File("printTemp\\temp." + type), printer);
                    }
                }
            }

        }

    }

    /**
     * 标签打印
     *
     * @Author ZhangGang
     * @Date 2019/2/13 9:41
     * @Param msg 接收到的信息
     **/
    public void LabelPrint(List<BarCode> barCodeList, List<Text> textList, String printerName) {
        //解析接收到的消息,进行打印
        try {
            Book book = new Book();
            // 设置成竖打
            PageFormat pf = new PageFormat();
            pf.setOrientation(PageFormat.PORTRAIT);

            // 通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符
            Paper p = new Paper();
            // 纸张大小
            //PageFormat指明打印页格式（页面大小以点为计量单位，1点为1英才的1/72，1英寸为25.4毫米。A4纸大致为595×842点）
            p.setSize(198.5, 141.7);
            //设置图片区域大小
            p.setImageableArea(0, 0, 198.5, 141.7);
            pf.setPaper(p);
            // 把 PageFormat 和 Printable 添加到book中，组成一个页面
            book.append(new DrawPrintContent(), pf);
            Print pr = new Print();

            for (int i = 0; i < barCodeList.size(); i++) {
                pr.addBarCode(barCodeList.get(i));
            }

            for (int i = 0; i < textList.size(); i++) {
                pr.addText(textList.get(i));
            }

            // 获取打印服务对象
            PrinterJob job = PrinterJob.getPrinterJob();
            // 设置打印类
            job.setPageable(book);

            //指定打印机名称
            HashAttributeSet hs = new HashAttributeSet();

            hs.add(new PrinterName(printerName, null));
            PrintService[] pss = PrintServiceLookup.lookupPrintServices(null, hs);

            if (pss.length == 0) {
                JOptionPane.showMessageDialog(null, "打印失败，未找到名称为:" + printerName + "的打印机，请检查。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            job.setPrintService(pss[0]);
            job.print();
            DrawPrintContent.textList.clear();
            DrawPrintContent.barCodeList.clear();

        } catch (Exception ex) {
            ex.printStackTrace();
            DrawPrintContent.textList.clear();
            DrawPrintContent.barCodeList.clear();
        }
    }

    /**
     * 图片打印
     *
     * @Author ZhangGang
     * @Date 2019/2/13 9:37
     * @Param file 文件
     * @Param printerName 打印机名称
     **/
    public void imagePrint(File file, String printerName) {
        String fileName = file.getName();
        InputStream fis = null;
        try {
            // 设置打印格式
            DocFlavor flavor = null;
            if (fileName.endsWith("png")) {
                flavor = DocFlavor.INPUT_STREAM.PNG;
            } else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg")) {
                flavor = DocFlavor.INPUT_STREAM.JPEG;
            } else if (fileName.endsWith("gif")) {
                flavor = DocFlavor.INPUT_STREAM.GIF;
            }
            // 设置打印参数
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(new Copies(1)); //份数
            //aset.add(MediaSize.ISO.A4); //纸张
            // aset.add(Finishings.STAPLE);//装订
            //aset.add(Sides.DUPLEX);//单双面
            // 定位打印服务
            PrintService printService = null;
            if (printerName != null) {
                //获得本台电脑连接的所有打印机
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if (printServices == null || printServices.length == 0) {
                    JOptionPane.showMessageDialog(null, "打印失败，未找到可用打印机，请检查。", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //匹配指定打印机
                for (int i = 0; i < printServices.length; i++) {
                    if (printServices[i].getName().contains(printerName)) {
                        printService = printServices[i];
                        break;
                    }
                }
                if (printService == null) {
                    JOptionPane.showMessageDialog(null, "打印失败，未找到名称为" + printerName + "的打印机，请检查。", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            fis = new FileInputStream(file); // 构造待打印的文件流
            Doc doc = new SimpleDoc(fis, flavor, null);
            DocPrintJob job = printService.createPrintJob(); // 创建打印作业
            job.print(doc, aset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (PrintException e) {
            e.printStackTrace();
        } finally {
            // 关闭打印的文件流
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * PDF文件打印
     *
     * @Author ZhangGang
     * @Date 2019/2/13 9:32
     * @Param file 文件
     * @Param printerName 打印机名称
     **/
    public void PDFprint(File file, String printerName) {
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(file.getName());
            if (printerName != null) {
                // 查找并设置打印机
                //获得本台电脑连接的所有打印机
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if (printServices == null || printServices.length == 0) {
                    JOptionPane.showMessageDialog(null, "打印失败，未找到可用打印机，请检查。", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                PrintService printService = null;
                //匹配指定打印机
                for (int i = 0; i < printServices.length; i++) {
                    if (printServices[i].getName().contains(printerName)) {
                        printService = printServices[i];
                        break;
                    }
                }
                if (printService != null) {
                    printJob.setPrintService(printService);
                } else {
                    JOptionPane.showMessageDialog(null, "打印失败，未找到名称为" + printerName + "的打印机，请检查。", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            //设置纸张及缩放
            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.ACTUAL_SIZE);
            //设置多页打印
            Book book = new Book();
            PageFormat pageFormat = new PageFormat();
            //设置打印方向
            pageFormat.setOrientation(PageFormat.PORTRAIT);//纵向
            pageFormat.setPaper(getPaper());//设置纸张
            book.append(pdfPrintable, pageFormat, document.getNumberOfPages());
            printJob.setPageable(book);
            printJob.setCopies(1);//设置打印份数
            //添加打印属性
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            pars.add(Sides.DUPLEX); //设置单双页
            printJob.print(pars);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PrinterException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 设置纸张
     *
     * @Author ZhangGang
     * @Date 2019/2/13 9:32
     **/
    public Paper getPaper() {
        Paper paper = new Paper();
        // 默认为A4纸张，对应像素宽和高分别为 595, 842
        int width = 595;
        int height = 842;
        // 设置边距，单位是像素，10mm边距，对应 28px
        int marginLeft = 10;
        int marginRight = 0;
        int marginTop = 10;
        int marginBottom = 0;
        paper.setSize(width, height);
        // 下面一行代码，解决了打印内容为空的问题
        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }

    /**
     * 将Byte数组转换成文件
     *
     * @return
     * @Author ZhangGang
     * @Date 2019/2/13 11:20
     * @Param
     **/
    public void getFileByBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && !dir.isDirectory()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断图片类型
     *
     * @return 图片类型
     * @Author ZhangGang
     * @Date 2019/2/13 11:43
     * @Param data 图片转换成的byte数组
     **/
    public String getType(byte[] data) {
        String type = null;
        // Png test:
        if (data[1] == 'P' && data[2] == 'N' && data[3] == 'G') {
            type = "png";
            return type;
        }
        // Gif test:
        if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
            type = "gif";
            return type;
        }
        // JPG test:
        if (data[6] == 'J' && data[7] == 'F' && data[8] == 'I'
                && data[9] == 'F') {
            type = "jpg";
            return type;
        }
        return type;
    }

}
