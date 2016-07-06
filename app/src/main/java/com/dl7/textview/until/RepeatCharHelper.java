package com.dl7.textview.until;

import com.dl7.textview.entity.RepeatChar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by long on 2016/7/6.
 * 字符串帮助类
 */
public final class RepeatCharHelper {

    private RepeatCharHelper() {
        throw new RuntimeException("RepeatCharHelper cannot be initialized!");
    }

    /**
     * 查找重复的字符
     * @param curText 当前字符串
     * @param oldText 旧的字符串
     * @return
     */
    public static List<RepeatChar> findRepeatChar(CharSequence curText, CharSequence oldText) {
        List<RepeatChar> charList = new ArrayList<>();
        if (curText == null || oldText == null) {
            return charList;
        }
        // 用来保存已经重复的当前字符串索引
        Set<Integer> skip = new HashSet<>();
        for (int i = 0; i < oldText.length(); i++) {
            char c = oldText.charAt(i);
            for (int j = 0; j < curText.length(); j++) {
                if (!skip.contains(j) && c == curText.charAt(j)) {
                    skip.add(j);
                    charList.add(new RepeatChar(i, j));
                    break;
                }
            }
        }
        return charList;
    }

    /**
     * 判断旧字符是否需要移动
     * @param oldIndex  旧索引
     * @param charList  重复的字符列表
     * @return  当前字符索引
     */
    public static int needMove(int oldIndex, List<RepeatChar> charList) {
        for (RepeatChar repeatChar : charList) {
            if (repeatChar.getOldIndex() == oldIndex) {
                return repeatChar.getCurIndex();
            }
        }
        return -1;
    }

    /**
     * 判断当前字符是否要做变换处理
     * @param curIndex  字符索引
     * @param charList  重复的字符列表
     * @return
     */
    public static boolean isNoChange(int curIndex, List<RepeatChar> charList) {
        for (RepeatChar repeatChar : charList) {
            if (repeatChar.getCurIndex() == curIndex) {
                return true;
            }
        }
        return false;
    }
}
