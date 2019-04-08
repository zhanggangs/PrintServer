package com.yihong.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendMessage {

    private String printer;
    private List<BarCode> barCode = new ArrayList();
    private List<Text> text = new ArrayList();
    private byte[] bytes;
    private String printerType;

    public String getPrinterType() {
        return printerType;
    }

    public void setPrinterType(String printerType) {
        this.printerType = printerType;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public List<BarCode> getBarCode() {
        return barCode;
    }

    public void setBarCode(String content, int x, int y, int width, int height, Float moduleWidth) {
        BarCode barCode = new BarCode(content, x, y, width, height, moduleWidth);
        this.barCode.add(barCode);
    }

    public List<Text> getText() {
        return text;
    }

    public void setText(String content, int x, int y, String fontName, int fontSize) {
        com.yihong.bean.Text text = new com.yihong.bean.Text(content, x, y, fontName, fontSize);
        this.text.add(text);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "SendMessage{" +
                "printer='" + printer + '\'' +
                ", barCode=" + barCode +
                ", text=" + text +
                ", bytes=" + Arrays.toString(bytes) +
                ", printerType='" + printerType + '\'' +
                '}';
    }
}
