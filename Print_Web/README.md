#打印web端项目
###pom.xml文件中除了基本的web依赖外还需要添加以下依赖:
````
        <!-- activemq消息服务 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-activemq</artifactId>
        </dependency>

        <!-- PDF文件生成 -->
        <dependency>
            <groupId>com.itextpdf</groupId>
            <artifactId>itextpdf</artifactId>
            <version>5.5.13</version>
        </dependency>

        <!-- 包含中文字的PDF生成 -->
        <dependency>
            <groupId>com.itextpdf</groupId>
            <artifactId>itext-asian</artifactId>
            <version>5.2.0</version>
        </dependency>

        <!-- JSON工具 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.13</version>
        </dependency>
````
###application.properties文件需要添加以下配置:
````
        server.port=8080
        # false是默认值，表示JMS使用点对点模式；true表示JMS使用发布/订阅模式
        spring.jms.pub-sub-domain=false
        # 指定activeMQ服务地址,不写此行会启用内嵌的activeMQ
        spring.activemq.broker-url=tcp://127.0.0.1:61616
        # 用户名
        spring.activemq.user=admin
        # 密码
        spring.activemq.password=yihong123456
````
###PrintController 打印控制类 (可在该类中调整打印的样式)
该类必须注入jms消息服务模板
````
        @Autowired
        JmsTemplate jmsTemplate;
````
####printLabel() 打印标签
参数和返回值:
````
        @Param queueName 打印队列的名称(String)
        @Param printer 打印机的名称(String)
        @Param content 需要打印的内容(根据需要自定义类型)
        @return String 页面提示信息
````
通过解析打印内容content(`这里直接使用固定数据,没有从content中解析数据`),将需要打印的内容根据要求使用set方法set到SendMessage中,
然后将sendMessage转换为JSON发送给指定的queueName(队列)
````
        //创建SendMessage类实例对象
        SendMessage sendMessage = new SendMessage();
        
        //设置打印机名称
        sendMessage.setPrinter(printer);
        
        //设置打印条形码(条形码内容,x坐标,y坐标,条形码宽度,条形码高度)
        sendMessage.setBarCode("270000-00255584-0001", 10, 10, 75, 35);
        
        //设置打印文本(文本内容,x坐标,y坐标,字体样式,字体大小)
        sendMessage.setText("270000-00255584-0001", 10, 55, "微软雅黑", 14);
        sendMessage.setText("上海联易德装饰有限公司", 10, 69, "微软雅黑", 14);
        sendMessage.setText("310117003914965", 10, 83, "微软雅黑", 14);
        sendMessage.setText("270000-00255584", 10, 97, "微软雅黑", 14);
        sendMessage.setText("20180713", 10, 111, "微软雅黑", 14);
        sendMessage.setText("设立", 90, 111, "微软雅黑", 14);
        
        //将打印内容发送给指定队列
        jmsTemplate.convertAndSend(queueName, JSON.toJSONString(sendMessage));
````
###printA4() 打印A4
参数和返回值:
````
        @Param queueName 队列名称
        @Param printer 打印机名称
        @Param content 需要打印的内容(根据需要自定义类型)
        @return String 页面提示信息
````
该方法将createPDF()方法生成的PDF文件通过getFileToByte(File file)方法转换为byte[],
然后通过jmsTemplate.convertAndSend()发送给指定消息队列
````
        //生成PDF文件
        File file = new File(createPDF(content));
        //创建SendMessage类实例对象
        SendMessage sendMessage = new SendMessage();
        //设置打印机名称
        sendMessage.setPrinter(printer);
        //设置转换成byte[]的文件
        sendMessage.setBytes(getFileToByte(file));
````
###createPDF() 生成PDF文件
参数和返回值:
````
        @Param content 需要打印的内容(根据需要自定义类型)
        @return String PDF文件路径
````
通过itextpdf来生成自己所需要的PDF文件
````
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
````
###getFileToByte() 将文件转换为byte[]类型
参数和返回值:
````
    @Param file 文件
    @return byte[] 文件转换的byte数组
````

##页面JS

打印时都是调用print()函数
需要四个参数,分别为
``
queueName, url, printerName, content
``

``
queueName 为队列名称,可通过请求本地打印客户端获取,本例子中请求地址为:http://127.0.0.1:8002/config/getQueueName
``

``
url 为本次打印请求的controller路径,不同的打印请求不同的url路径
``

``
printerName 为打印机名称,为此次打印指定打印机
``

``
content 为需要打印的内容(建议使用JSON字符串)
``

````
    function printLabel() {
        console.log("----进行标签打印----");
        $.ajax({
            url: "http://127.0.0.1:8002/config/getQueueName",
            async: false,
            type: "get",
            dataType: "jsonp",  //数据格式设置为jsonp
            jsonp: "callback",  //Jquery生成验证参数的名称
            jsonpCallback: "successCallback",
            success: function (data) {
                print(data.queueName, "../printLabel", "ZEBRA");
            },
            error: function (data) {
                console.log("错误");
                console.log(data);
            }
        });
    }

    function printA4() {
        console.log("----进行A4打印----");
        $.ajax({
            url: "http://127.0.0.1:8002/config/getQueueName",
            async: false,
            type: "get",
            dataType: "jsonp",  //数据格式设置为jsonp
            jsonp: "callback",  //Jquery生成验证参数的名称
            jsonpCallback: "successCallback",
            success: function (data) {
                print(data.queueName, "../printA4", "Microsoft Print to PDF");
            },
            error: function (data) {
                console.log("错误");
                console.log(data);
            }
        });
    }

    function print(queueName, url, printerName, content) {

        $.ajax({
            url: url,
            type: "post",
            async: false,
            dataType: "json",
            data: {
                queueName: queueName,
                printer: printerName,
                content: content
            },
            success: function (data) {
                if (data.code == 200) {
                    $.messager.alert("提示", data.message, "info");
                } else {
                    $.messager.alert("提示", data.message, "error");
                }
            }

        })
    }
````
