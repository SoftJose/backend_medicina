package saitel.medicina.util;

import java.util.HashMap;
import java.util.Map;

public final class ExtensionUtil {

    private ExtensionUtil() {}

    private static final Map<String, String> MIME_TO_EXT = new HashMap<>();

    static {
        MIME_TO_EXT.put("application/pdf", "pdf");
        MIME_TO_EXT.put("image/jpeg", "jpg");
        MIME_TO_EXT.put("image/jpg", "jpg");
        MIME_TO_EXT.put("image/png", "png");
        MIME_TO_EXT.put("application/msword", "doc");
        MIME_TO_EXT.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        MIME_TO_EXT.put("application/vnd.ms-excel", "xls");
        MIME_TO_EXT.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
    }

    public static String extension(String value) {
        if (value == null || value.isBlank()) return "pdf";

        String v = value.trim().toLowerCase();

        if (MIME_TO_EXT.containsKey(v)) {
            return MIME_TO_EXT.get(v);
        }

        if (v.startsWith(".")) {
            v = v.substring(1);
        }

        if ("jpeg".equals(v)) return "jpg";

        if (v.isBlank()) return "pdf";

        return v;
    }
}
