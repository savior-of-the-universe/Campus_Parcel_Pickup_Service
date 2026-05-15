package com.team.utils;

public final class DataMaskingUtils {

    private DataMaskingUtils() {
    }

    /**
     * 手机号脱敏：保留前3位和后4位，中间用**代替。
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        String trimmed = phone.trim();
        if (trimmed.length() <= 7) {
            // 长度不足标准位数，使用首位+**+末位兜底
            if (trimmed.length() <= 2) {
                return "*".repeat(trimmed.length());
            }
            return trimmed.charAt(0) + "**" + trimmed.charAt(trimmed.length() - 1);
        }
        String prefix = trimmed.substring(0, 3);
        String suffix = trimmed.substring(trimmed.length() - 4);
        return prefix + "**" + suffix;
    }

    /**
     * 学号脱敏：保留前2位和后2位，中间用**代替。
     */
    public static String maskStudentId(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return studentId;
        }
        String trimmed = studentId.trim();
        if (trimmed.length() <= 4) {
            // 长度不足时，保留首位尾位
            if (trimmed.length() <= 2) {
                return "*".repeat(trimmed.length());
            }
            return trimmed.charAt(0) + "**" + trimmed.charAt(trimmed.length() - 1);
        }
        String prefix = trimmed.substring(0, 2);
        String suffix = trimmed.substring(trimmed.length() - 2);
        return prefix + "**" + suffix;
    }
}
