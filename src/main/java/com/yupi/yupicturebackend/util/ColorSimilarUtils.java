package com.yupi.yupicturebackend.util;

import java.awt.Color;

/**
 * 工具类：计算颜色相似度
 */
public final class ColorSimilarUtils {

    private static final double MAX_DISTANCE = Math.sqrt(3 * Math.pow(255, 2));

    private ColorSimilarUtils() {
        // 工具类不需要实例化
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    /**
     * 计算两个颜色的相似度（基于RGB欧氏距离）
     *
     * @param color1 第一个颜色
     * @param color2 第二个颜色
     * @return 相似度（0~1，1表示完全相同）
     */
    public static double calculateSimilarity(Color color1, Color color2) {
        if (color1 == null || color2 == null) {
            throw new IllegalArgumentException("颜色参数不能为空");
        }

        int dr = color1.getRed() - color2.getRed();
        int dg = color1.getGreen() - color2.getGreen();
        int db = color1.getBlue() - color2.getBlue();

        double distance = Math.sqrt(dr * dr + dg * dg + db * db);
        return 1.0 - (distance / MAX_DISTANCE);
    }

    /**
     * 计算两个十六进制颜色字符串的相似度（支持#或0x开头）
     *
     * @param hexColor1 第一个颜色（如 "#FF0000" 或 "0xFF0000"）
     * @param hexColor2 第二个颜色
     * @return 相似度（0~1，1表示完全相同）
     */
    public static double calculateSimilarity(String hexColor1, String hexColor2) {
        if (hexColor1 == null || hexColor2 == null) {
            throw new IllegalArgumentException("颜色字符串不能为空");
        }
        Color color1 = parseHexColor(hexColor1);
        Color color2 = parseHexColor(hexColor2);
        return calculateSimilarity(color1, color2);
    }

    /**
     * 将十六进制颜色字符串转换为 Color 对象
     */
    private static Color parseHexColor(String hex) {
        if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = "#" + hex.substring(2);
        }
        return Color.decode(hex);
    }

    public static void main(String[] args) {
        System.out.println("RGB颜色相似度：" +
                calculateSimilarity(new Color(255, 0, 0), new Color(254, 1, 1)));

        System.out.println("十六进制颜色相似度：" +
                calculateSimilarity("0xFF0000", "0xFE0101"));

        System.out.println("支持#前缀：" +
                calculateSimilarity("#FF0000", "#FE0101"));
    }
}
