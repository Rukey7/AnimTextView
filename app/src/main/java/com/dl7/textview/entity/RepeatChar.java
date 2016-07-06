package com.dl7.textview.entity;

/**
 * Created by long on 2016/7/6.
 * 重复字符实体
 */
public class RepeatChar {

    // 上一个索引
    private int oldIndex;
    // 当前索引
    private int curIndex;

    public RepeatChar(int oldIndex, int curIndex) {
        this.oldIndex = oldIndex;
        this.curIndex = curIndex;
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public void setOldIndex(int oldIndex) {
        this.oldIndex = oldIndex;
    }

    public int getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }
}
