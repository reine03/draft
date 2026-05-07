package byod.util;

import java.util.regex.Pattern;

/**
 * Input validation helpers used across controllers.
 */
public class Validator {

    private static final Pattern STUDENT_ID  = Pattern.compile("\\d{4}-\\d{5}");
    private static final Pattern MAC_ADDRESS = Pattern.compile(
        "^([0-9A-Fa-f]{2}[:\\-]){5}[0-9A-Fa-f]{2}$"
    );

    /** e.g. 2024-00192 */
    public static boolean isValidStudentId(String id) {
        return id != null && STUDENT_ID.matcher(id.trim()).matches();
    }

    /** Optional field — valid if blank OR matches MAC pattern. */
    public static boolean isValidMac(String mac) {
        if (mac == null || mac.isBlank()) return true;
        return MAC_ADDRESS.matcher(mac.trim()).matches();
    }

    /** Reject blank or whitespace-only strings. */
    public static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    /** Build a user-facing error string from multiple checks. Returns null if all pass. */
    public static String validateRegistrationForm(
            String studentId, String fullName, String course,
            String serialNo, String brand, String model, String mac) {

        StringBuilder errors = new StringBuilder();

        if (!notBlank(fullName))          errors.append("• Full name is required.\n");
        if (!isValidStudentId(studentId)) errors.append("• Student ID must be in format YYYY-NNNNN (e.g. 2024-00192).\n");
        if (!notBlank(course))            errors.append("• Course / program is required.\n");
        if (!notBlank(serialNo))          errors.append("• Serial number is required.\n");
        if (!notBlank(brand))             errors.append("• Brand is required.\n");
        if (!notBlank(model))             errors.append("• Model is required.\n");
        if (!isValidMac(mac))             errors.append("• MAC address format is invalid (e.g. A4:C3:F0:11:22:33).\n");

        return errors.length() > 0 ? errors.toString().trim() : null;
    }
}
