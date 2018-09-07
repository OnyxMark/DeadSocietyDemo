package me.nosmastew.deadsociety.utilities;


import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class DSUtils {

    private static final String SEQUENCE_HEADER = "" + ChatColor.RESET + ChatColor.UNDERLINE + ChatColor.RESET;
    private static final String SEQUENCE_FOOTER = "" + ChatColor.RESET + ChatColor.ITALIC + ChatColor.RESET;

    public static final long ONE_SECOND = 1;
    public static final long ONE_MINUTE = ONE_SECOND * 60;

    public static final String /* Texts & Symbols */
            LINE = ChatColor.translateAlternateColorCodes('&', "&7&m-----------------------------------------------------"),
            ARROW = ChatColor.RED + "» ",
            DOT = ChatColor.DARK_GRAY + "• ",
            EXCLAMATION_MARK = ChatColor.RED + "(!) ",
            DS = ChatColor.GRAY + "[" + ChatColor.RED + "DeadSociety" + ChatColor.GRAY + "] ",
            CLICK_HERE_SIGN = ChatColor.DARK_RED + "Click Here";

    public static final String /* Github Wiki */
            LOOT_WIKI = "https://github.com/NosmaSteww/DeadSociety/wiki/About-Lootable-Chests",
            WIKI = ChatColor.GRAY + "https://www.spigotmc.org/resources/48823/";

    public static final String /* Warnings & Information */
            FILES_RELOADED = ChatColor.GREEN + "Files were successfully reloaded!";

    public static final String /* Signs */
            RANDOM_SPAWN_SIGN = "[RandomSpawn]",
            ZONE_SIGN = "[Zone]";

    public static String encodeString(String hiddenString) {
        return quote(stringToColors(hiddenString));
    }

    public static boolean hasHiddenString(String input) {
        return input != null && input.indexOf(SEQUENCE_HEADER) > -1 && input.indexOf(SEQUENCE_FOOTER) > -1;

    }

    public static String extractHiddenString(String input) {
        return colorsToString(extract(input));
    }

    public static String replaceHiddenString(String input, String hiddenString) {
        if (input == null) return null;

        int start = input.indexOf(SEQUENCE_HEADER);
        int end = input.indexOf(SEQUENCE_FOOTER);

        if (start < 0 || end < 0) {
            return null;
        }

        return input.substring(0, start + SEQUENCE_HEADER.length()) + stringToColors(hiddenString) + input.substring(end, input.length());
    }

    public static String colour(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private static String quote(String input) {
        if (input == null) return null;
        return SEQUENCE_HEADER + input + SEQUENCE_FOOTER;
    }

    private static String extract(String input) {
        if (input == null) return null;

        int start = input.indexOf(SEQUENCE_HEADER);
        int end = input.indexOf(SEQUENCE_FOOTER);

        if (start < 0 || end < 0) {
            return null;
        }

        return input.substring(start + SEQUENCE_HEADER.length(), end);
    }

    private static String stringToColors(String normal) {
        if (normal == null) return null;

        byte[] bytes = normal.getBytes(Charset.forName("UTF-8"));
        char[] chars = new char[bytes.length * 4];

        for (int i = 0; i < bytes.length; i++) {
            char[] hex = byteToHex(bytes[i]);
            chars[i * 4] = ChatColor.COLOR_CHAR;
            chars[i * 4 + 1] = hex[0];
            chars[i * 4 + 2] = ChatColor.COLOR_CHAR;
            chars[i * 4 + 3] = hex[1];
        }

        return new String(chars);
    }

    private static String colorsToString(String colors) {
        if (colors == null) return null;

        colors = colors.toLowerCase().replace("" + ChatColor.COLOR_CHAR, "");

        if (colors.length() % 2 != 0) {
            colors = colors.substring(0, (colors.length() / 2) * 2);
        }

        char[] chars = colors.toCharArray();
        byte[] bytes = new byte[chars.length / 2];

        for (int i = 0; i < chars.length; i += 2) {
            bytes[i / 2] = hexToByte(chars[i], chars[i + 1]);
        }

        return new String(bytes, Charset.forName("UTF-8"));
    }

    private static int hexToUnsignedInt(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'a' && c <= 'f') {
            return c - 87;
        } else {
            throw new IllegalArgumentException("Invalid hex char: out of range");
        }
    }

    private static char unsignedIntToHex(int i) {
        if (i >= 0 && i <= 9) {
            return (char) (i + 48);
        } else if (i >= 10 && i <= 15) {
            return (char) (i + 87);
        } else {
            throw new IllegalArgumentException("Invalid hex int: out of range");
        }
    }

    private static byte hexToByte(char hex1, char hex0) {
        return (byte) (((hexToUnsignedInt(hex1) << 4) | hexToUnsignedInt(hex0)) + Byte.MIN_VALUE);
    }

    private static char[] byteToHex(byte b) {
        int unsignedByte = (int) b - Byte.MIN_VALUE;
        return new char[]{unsignedIntToHex((unsignedByte >> 4) & 0xf), unsignedIntToHex(unsignedByte & 0xf)};
    }

    public static String s(int amount) {
        return (amount > 1) ? "s" : "";
    }

    public static String s(String s, int amount) {
        return (!s.endsWith("s")) && (!s.equals("c4")) && (amount > 1) ? "s" : "";
    }

    public static String currentTime(long duration) {
        StringBuilder res = new StringBuilder();
        long temp;
        if (duration >= ONE_SECOND) {

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                String s = temp > 1 ? " " + Lang.TIME_MINUTES.get() : " " + Lang.TIME_MINUTE.get();
                res.append(temp).append(s);
            }

            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" ").append(Lang.TIME_AND.get()).append(" ");
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                String s = temp > 1 ? " " + Lang.TIME_SECONDS.get() : " " + Lang.TIME_SECOND.get();
                res.append(temp).append(s);
            }
            return res.toString();
        } else {
            return "0 " + Lang.TIME_SECOND.get();
        }
    }

    private static DecimalFormat formatter;

    public static String format(double arg0) {
        if (formatter == null) formatter = new DecimalFormat("###,###,###.#");

        return formatter.format(arg0);
    }

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static String randomAlphanumeric(int count) {
        return RandomStringUtils.randomAlphanumeric(count).toUpperCase();
    }

    public static String randomHiddenAlphanumeric(int count) {
        String rs = randomAlphanumeric(count);
        return DSUtils.encodeString(rs.toUpperCase());
    }

    public static String randomHiddenString(String string, int count) {
        String rs = randomAlphanumeric(count);
        return DSUtils.encodeString(string) + " " + DSUtils.encodeString(rs.toUpperCase());
    }
}