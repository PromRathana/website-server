package com.cambofreelance.websiteservice.logger.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtil {

    private static final NumberFormat COMPACT_FORMAT =
        NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);

    // Private constructor to prevent instantiation
    private NumberUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String abbrNum(double number) {
        COMPACT_FORMAT.setMaximumFractionDigits(1);
        return COMPACT_FORMAT.format(number);
    }

}
