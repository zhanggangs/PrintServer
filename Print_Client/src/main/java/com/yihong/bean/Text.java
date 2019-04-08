package com.yihong.bean;

public class Text {

    private String content;
    private int x;
    private int y;
    private String fontName;
    private int fontSize;

    public Text() {
    }

    public Text(String content, int x, int y, String fontName, int fontSize) {
        this.content = content;
        this.x = x;
        this.y = y;
        this.fontName = fontName;
        this.fontSize = fontSize;
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

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public String toString() {
        return "Text{" +
                "content='" + content + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", fontName='" + fontName + '\'' +
                ", fontSize=" + fontSize +
                '}';
    }
}
