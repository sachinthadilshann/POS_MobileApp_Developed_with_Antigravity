package com.sachintha.posapp.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for formatting numbers, dates, and currencies
 */
public class FormatUtils {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat("#,##0");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat INVOICE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    /**
     * Format amount as currency
     */
    public static String formatCurrency(double amount) {
        return "Rs. " + CURRENCY_FORMAT.format(amount);
    }

    /**
     * Format amount without currency symbol
     */
    public static String formatAmount(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    /**
     * Format quantity
     */
    public static String formatQuantity(int quantity) {
        return QUANTITY_FORMAT.format(quantity);
    }

    /**
     * Format date only
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }

    /**
     * Format time only
     */
    public static String formatTime(Date date) {
        if (date == null) return "";
        return TIME_FORMAT.format(date);
    }

    /**
     * Format date and time
     */
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return DATETIME_FORMAT.format(date);
    }

    /**
     * Generate invoice number
     */
    public static String generateInvoiceNumber() {
        return "INV" + INVOICE_DATE_FORMAT.format(new Date());
    }

    /**
     * Format percentage
     */
    public static String formatPercentage(double value) {
        return String.format(Locale.getDefault(), "%.1f%%", value);
    }
}
