package me.skean.skeanframework.utils;

import android.graphics.Color;
import android.text.TextUtils;

import com.amulyakhare.textdrawable.TextDrawable;
import com.blankj.utilcode.util.SizeUtils;

/**
 * 简易TextDrawableBuilder
 */

public class TextDrawableBuilder {

    private String text;

    private int textColor = Color.BLACK;

    private int fontSizeInSp = 12;

    private boolean isBold;

    private int backgroundColor = Color.TRANSPARENT;

    private int width = -1;

    private int height = -1;

    private boolean isRound;

    private int radiusInDp = 0;

    public TextDrawableBuilder(String text) {
        this.text = text;
    }

    public TextDrawableBuilder(String text, int textColor) {
        this.text = text;
        this.textColor = textColor;
    }

    public TextDrawableBuilder(String text, int textColor, int fontSizeInSp) {
        this.text = text;
        this.textColor = textColor;
        this.fontSizeInSp = fontSizeInSp;
    }

    public TextDrawableBuilder(String text, int textColor, int fontSizeInSp, boolean isBold) {
        this.text = text;
        this.textColor = textColor;
        this.fontSizeInSp = fontSizeInSp;
        this.isBold = isBold;
    }

    public TextDrawableBuilder(String text, int textColor, int fontSizeInSp, boolean isBold, int backgroundColor) {
        this.text = text;
        this.textColor = textColor;
        this.fontSizeInSp = fontSizeInSp;
        this.isBold = isBold;
        this.backgroundColor = backgroundColor;
    }

    public TextDrawableBuilder(String text,
                               int textColor,
                               int fontSizeInSp,
                               boolean isBold,
                               int backgroundColor,
                               int width,
                               int height,
                               boolean isRound,
                               int radius) {
        this.text = text;
        this.textColor = textColor;
        this.fontSizeInSp = fontSizeInSp;
        this.isBold = isBold;
        this.backgroundColor = backgroundColor;
        this.width = width;
        this.height = height;
        this.isRound = isRound;
        this.radiusInDp = radius;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getFontSizeInSp() {
        return fontSizeInSp;
    }

    public void setFontSizeInSp(int fontSizeInSp) {
        this.fontSizeInSp = fontSizeInSp;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getRadiusInDp() {
        return radiusInDp;
    }

    public void setRadiusInDp(int radiusInDp) {
        this.radiusInDp = radiusInDp;
    }

    public boolean isRound() {
        return isRound;
    }

    public void setRound(boolean round) {
        isRound = round;
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

    public TextDrawable build() {
        if (TextUtils.isEmpty(text)) {
            throw new RuntimeException("必须指定字符!");
        }
        int size = SizeUtils.sp2px(fontSizeInSp);
        if (width < 0) width = size * (text.length() + 2); //左右一个字空白
        if (height < 0) height = size * 2; //上下半个字空白
        TextDrawable.IConfigBuilder innerBuilder = TextDrawable.builder()
                                                               .beginConfig()
                                                               .width(width)
                                                               .height(height)
                                                               .fontSize(size)
                                                               .bold()
                                                               .textColor(textColor);
        if (isBold) innerBuilder.bold();
        TextDrawable td;
        if (radiusInDp == 0) td = innerBuilder.endConfig().buildRect(text, backgroundColor);
        else {
            if (isRound) td = innerBuilder.endConfig().buildRound(text, backgroundColor);
            else td = innerBuilder.endConfig().buildRoundRect(text, backgroundColor, radiusInDp);
        }
        return td;
    }
}
