package common.cn.kafei.simukraft.util;

public class MathUtil {
    public static double clamp(double value, double minValue, double maxValue) {
        return Math.max(minValue, Math.min(value, maxValue));
    }

    public static float clamp(float value, float minValue, float maxValue) {
        return Math.max(minValue, Math.min(value, maxValue));
    }

    public static int clamp(int value, int minValue, int maxValue) {
        return Math.max(minValue, Math.min(value, maxValue));
    }
}
