package com.velocity.core.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations.
 * Provides helper methods for formatting, parsing, and calculating time differences.
 * 
 * <p>All methods are static and the class cannot be instantiated.</p>
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
public final class DateTimeUtil {

    /**
     * ISO 8601 date-time formatter (e.g., "2026-02-01T15:30:00").
     */
    public static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Date formatter (e.g., "2026-02-01").
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Time formatter (e.g., "15:30:00").
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    /**
     * Human-readable date-time formatter (e.g., "01 Feb 2026, 03:30 PM").
     */
    public static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    /**
     * Human-readable date formatter (e.g., "01 Feb 2026").
     */
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Private constructor to prevent instantiation.
     * 
     * @throws UnsupportedOperationException if instantiation is attempted
     */
    private DateTimeUtil() {
        throw new UnsupportedOperationException("DateTimeUtil is a utility class and cannot be instantiated");
    }

    /**
     * Gets the current UTC timestamp.
     * 
     * @return current instant in UTC
     */
    public static Instant now() {
        return Instant.now();
    }

    /**
     * Gets the current date-time in UTC.
     * 
     * @return current LocalDateTime in UTC
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Gets the current date-time in the specified timezone.
     * 
     * @param zoneId the timezone
     * @return current LocalDateTime in the specified timezone
     */
    public static LocalDateTime nowInZone(ZoneId zoneId) {
        return LocalDateTime.now(zoneId);
    }

    /**
     * Gets the current date-time in Indian Standard Time (IST).
     * 
     * @return current LocalDateTime in IST
     */
    public static LocalDateTime nowIst() {
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    /**
     * Converts LocalDateTime to Instant (assumes UTC).
     * 
     * @param dateTime the LocalDateTime to convert
     * @return the corresponding Instant
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC);
    }

    /**
     * Converts Instant to LocalDateTime (in UTC).
     * 
     * @param instant the Instant to convert
     * @return the corresponding LocalDateTime in UTC
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Formats a LocalDateTime using the ISO 8601 format.
     * 
     * @param dateTime the LocalDateTime to format
     * @return formatted string (e.g., "2026-02-01T15:30:00")
     */
    public static String formatIso(LocalDateTime dateTime) {
        return dateTime.format(ISO_DATE_TIME_FORMATTER);
    }

    /**
     * Formats a LocalDateTime for display purposes.
     * 
     * @param dateTime the LocalDateTime to format
     * @return formatted string (e.g., "01 Feb 2026, 03:30 PM")
     */
    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_DATE_TIME_FORMATTER);
    }

    /**
     * Formats a LocalDate for display purposes.
     * 
     * @param date the LocalDate to format
     * @return formatted string (e.g., "01 Feb 2026")
     */
    public static String formatDisplay(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Parses an ISO 8601 date-time string.
     * 
     * @param dateTimeString the string to parse (e.g., "2026-02-01T15:30:00")
     * @return the parsed LocalDateTime
     * @throws java.time.format.DateTimeParseException if the string cannot be parsed
     */
    public static LocalDateTime parseIso(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, ISO_DATE_TIME_FORMATTER);
    }

    /**
     * Calculates the difference in minutes between two LocalDateTime instances.
     * 
     * @param start the start date-time
     * @param end the end date-time
     * @return the difference in minutes (can be negative if end is before start)
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Calculates the difference in seconds between two LocalDateTime instances.
     * 
     * @param start the start date-time
     * @param end the end date-time
     * @return the difference in seconds (can be negative if end is before start)
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * Calculates the difference in hours between two LocalDateTime instances.
     * 
     * @param start the start date-time
     * @param end the end date-time
     * @return the difference in hours (can be negative if end is before start)
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calculates the difference in days between two LocalDateTime instances.
     * 
     * @param start the start date-time
     * @param end the end date-time
     * @return the difference in days (can be negative if end is before start)
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Checks if a date-time is in the past.
     * 
     * @param dateTime the date-time to check
     * @return true if the date-time is before now (UTC), false otherwise
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime.isBefore(nowUtc());
    }

    /**
     * Checks if a date-time is in the future.
     * 
     * @param dateTime the date-time to check
     * @return true if the date-time is after now (UTC), false otherwise
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(nowUtc());
    }

    /**
     * Checks if a date-time is within a specified number of minutes from now.
     * 
     * @param dateTime the date-time to check
     * @param minutes the number of minutes
     * @return true if the date-time is within the specified minutes from now
     */
    public static boolean isWithinMinutes(LocalDateTime dateTime, long minutes) {
        LocalDateTime now = nowUtc();
        long diff = Math.abs(minutesBetween(now, dateTime));
        return diff <= minutes;
    }

    /**
     * Adds minutes to a date-time.
     * 
     * @param dateTime the base date-time
     * @param minutes the number of minutes to add (can be negative)
     * @return the resulting date-time
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.plusMinutes(minutes);
    }

    /**
     * Adds hours to a date-time.
     * 
     * @param dateTime the base date-time
     * @param hours the number of hours to add (can be negative)
     * @return the resulting date-time
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }

    /**
     * Adds days to a date-time.
     * 
     * @param dateTime the base date-time
     * @param days the number of days to add (can be negative)
     * @return the resulting date-time
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }

    /**
     * Gets the start of the day (00:00:00) for a given date-time.
     * 
     * @param dateTime the date-time
     * @return the start of the day
     */
    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * Gets the end of the day (23:59:59.999999999) for a given date-time.
     * 
     * @param dateTime the date-time
     * @return the end of the day
     */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * Converts a timestamp in milliseconds to LocalDateTime (UTC).
     * 
     * @param epochMilli the timestamp in milliseconds since epoch
     * @return the corresponding LocalDateTime in UTC
     */
    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC);
    }

    /**
     * Converts a LocalDateTime to timestamp in milliseconds (assumes UTC).
     * 
     * @param dateTime the LocalDateTime to convert
     * @return the timestamp in milliseconds since epoch
     */
    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * Formats a duration in minutes to a human-readable string.
     * 
     * @param minutes the duration in minutes
     * @return formatted string (e.g., "1h 30m", "45m", "2h")
     */
    public static String formatDuration(long minutes) {
        if (minutes < 60) {
            return minutes + "m";
        }
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        if (remainingMinutes == 0) {
            return hours + "h";
        }
        return hours + "h " + remainingMinutes + "m";
    }

    /**
     * Gets a relative time description (e.g., "2 minutes ago", "in 5 minutes").
     * 
     * @param dateTime the date-time to compare with now
     * @return human-readable relative time string
     */
    public static String getRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = nowUtc();
        long minutes = minutesBetween(now, dateTime);
        
        if (minutes == 0) {
            return "just now";
        } else if (minutes > 0) {
            // Future
            if (minutes < 60) {
                return "in " + minutes + " minute" + (minutes == 1 ? "" : "s");
            } else if (minutes < 1440) { // Less than 24 hours
                long hours = minutes / 60;
                return "in " + hours + " hour" + (hours == 1 ? "" : "s");
            } else {
                long days = minutes / 1440;
                return "in " + days + " day" + (days == 1 ? "" : "s");
            }
        } else {
            // Past
            minutes = Math.abs(minutes);
            if (minutes < 60) {
                return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
            } else if (minutes < 1440) { // Less than 24 hours
                long hours = minutes / 60;
                return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
            } else {
                long days = minutes / 1440;
                return days + " day" + (days == 1 ? "" : "s") + " ago";
            }
        }
    }
}
