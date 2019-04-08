package com.yihong.bean;

public class BarCode {

    private String content;
    private int x;
    private int y;
    private int width;
    private int height;
    private Float moduleWidth;
    private int dpi = 203;

    public BarCode() {
    }

    public BarCode(String content, int x, int y, int width, int height, Float moduleWidth) {
        this.content = content;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.moduleWidth = moduleWidth;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Float getModuleWidth() {
        return moduleWidth;
    }

    public void setModuleWidth(Float moduleWidth) {
        this.moduleWidth = moduleWidth;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    @Override
    public String toString() {
        return "BarCode{" +
                "content='" + content + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", moduleWidth=" + moduleWidth +
                ", dpi=" + dpi +
                '}';
    }
}
