package com.FiveDollaGobby.WelcomeMessages.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:(#[A-Fa-f0-9]{6}):(#[A-Fa-f0-9]{6})>(.*?)</gradient>");
    private static final Pattern RAINBOW_PATTERN = Pattern.compile("<rainbow>(.*?)</rainbow>");

    // colorize with hex, gradient, and rainbow support
    public static String colorize(String message) {
        if (message == null) return "";
        
        // Enhanced security check using SecurityUtils
        message = SecurityUtils.sanitizeMessageContent(message);
        
        // Additional length check for color processing
        if (message.length() > 10000) {
            message = message.substring(0, 10000) + "...";
        }

        // gradients first
        message = processGradients(message);

        // then rainbow
        message = processRainbow(message);

        // hex colors
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);
            try {
                matcher.appendReplacement(buffer, ChatColor.of("#" + group).toString());
            } catch (IllegalArgumentException e) {
                // Invalid hex color, skip it
                matcher.appendReplacement(buffer, matcher.group(0));
            }
        }

        message = matcher.appendTail(buffer).toString();

        // legacy colors
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // process gradient patterns
    private static String processGradients(String message) {
        Matcher matcher = GRADIENT_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String startColor = matcher.group(1);
            String endColor = matcher.group(2);
            String text = matcher.group(3);

            // strip existing colors
            text = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text));

            String gradientText = applyGradient(text, startColor, endColor);
            matcher.appendReplacement(buffer, gradientText);
        }

        return matcher.appendTail(buffer).toString();
    }

    // process rainbow patterns
    private static String processRainbow(String message) {
        Matcher matcher = RAINBOW_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String text = matcher.group(1);

            // strip existing colors
            text = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text));

            String rainbowText = applyRainbow(text);
            matcher.appendReplacement(buffer, rainbowText);
        }

        return matcher.appendTail(buffer).toString();
    }

    // apply gradient between two colors
    private static String applyGradient(String text, String startHex, String endHex) {
        if (text.isEmpty()) return text;

        try {
            // parse colors with validation
            if (startHex.length() != 7 || endHex.length() != 7) {
                return text; // Invalid hex format
            }
            
            int startR = Integer.parseInt(startHex.substring(1, 3), 16);
            int startG = Integer.parseInt(startHex.substring(3, 5), 16);
            int startB = Integer.parseInt(startHex.substring(5, 7), 16);

            int endR = Integer.parseInt(endHex.substring(1, 3), 16);
            int endG = Integer.parseInt(endHex.substring(3, 5), 16);
            int endB = Integer.parseInt(endHex.substring(5, 7), 16);

            StringBuilder result = new StringBuilder();
            int length = text.length();

            for (int i = 0; i < length; i++) {
                char c = text.charAt(i);
                if (c == ' ') {
                    result.append(' ');
                    continue;
                }

                float ratio = (float) i / Math.max(1, length - 1);

                int r = Math.round(startR + (endR - startR) * ratio);
                int g = Math.round(startG + (endG - startG) * ratio);
                int b = Math.round(startB + (endB - startB) * ratio);

                String hex = String.format("#%02x%02x%02x", r, g, b);
                result.append(ChatColor.of(hex)).append(c);
            }

            return result.toString();
        } catch (IllegalArgumentException e) {
            // Invalid hex color, return original text
            return text;
        }
    }

    // apply rainbow colors
    private static String applyRainbow(String text) {
        if (text.isEmpty()) return text;

        // rainbow colors
        String[] rainbowHex = {
                "#ff0000", // red
                "#ff7f00", // orange
                "#ffff00", // yellow
                "#00ff00", // green
                "#0000ff", // blue
                "#4b0082", // indigo
                "#9400d3"  // violet
        };

        StringBuilder result = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                result.append(' ');
                continue;
            }

            // calculate position
            float position = (float) i / Math.max(1, length - 1) * (rainbowHex.length - 1);
            int colorIndex = (int) position;
            float colorRatio = position - colorIndex;

            String color;
            if (colorIndex >= rainbowHex.length - 1) {
                color = rainbowHex[rainbowHex.length - 1];
            } else {
                // interpolate between colors
                String startColor = rainbowHex[colorIndex];
                String endColor = rainbowHex[colorIndex + 1];

                int startR = Integer.parseInt(startColor.substring(1, 3), 16);
                int startG = Integer.parseInt(startColor.substring(3, 5), 16);
                int startB = Integer.parseInt(startColor.substring(5, 7), 16);

                int endR = Integer.parseInt(endColor.substring(1, 3), 16);
                int endG = Integer.parseInt(endColor.substring(3, 5), 16);
                int endB = Integer.parseInt(endColor.substring(5, 7), 16);

                int r = Math.round(startR + (endR - startR) * colorRatio);
                int g = Math.round(startG + (endG - startG) * colorRatio);
                int b = Math.round(startB + (endB - startB) * colorRatio);

                color = String.format("#%02x%02x%02x", r, g, b);
            }

            result.append(ChatColor.of(color)).append(c);
        }

        return result.toString();
    }

    // send colored message to sender
    public static void sendMessage(CommandSender sender, String message) {
        if (sender != null && message != null && !message.isEmpty()) {
            sender.sendMessage(colorize(message));
        }
    }

    // send to console with prefix
    public static void sendConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(colorize("[WelcomeMessages] " + message));
    }

    // broadcast to players with permission
    public static void broadcast(String message, String permission) {
        String colored = colorize(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (permission == null || player.hasPermission(permission)) {
                player.sendMessage(colored);
            }
        }
    }

    // center message in chat (approximately)
    public static String centerMessage(String message) {
        if (message == null || message.isEmpty()) return "";

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + message;
    }

    // strip all colors
    public static String stripColors(String message) {
        if (message == null) return null;
        return ChatColor.stripColor(colorize(message));
    }

    // check if empty
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // font widths for centering
    private enum DefaultFontInfo {
        A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5),
        D('D', 5), d('d', 5), E('E', 5), e('e', 5), F('F', 5), f('f', 4),
        G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1),
        J('J', 5), j('j', 5), K('K', 5), k('k', 4), L('L', 5), l('l', 1),
        M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5),
        P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5),
        S('S', 5), s('s', 5), T('T', 5), t('t', 4), U('U', 5), u('u', 5),
        V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5),
        Y('Y', 5), y('y', 5), Z('Z', 5), z('z', 5),
        NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5), NUM_5('5', 5),
        NUM_6('6', 5), NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0('0', 5),
        EXCLAMATION('!', 1), AT('@', 6), HASHTAG('#', 5), DOLLAR('$', 5),
                PERCENT('%', 5), AMPERSAND('&', 5), ASTERISK('*', 5), LEFT_PAREN('(', 4),
                RIGHT_PAREN(')', 4), MINUS('-', 5), UNDERSCORE('_', 5), PLUS('+', 5),
                EQUALS('=', 5), LEFT_BRACKET('[', 3), RIGHT_BRACKET(']', 3),
                LEFT_BRACE('{', 4), RIGHT_BRACE('}', 4), COLON(':', 1), SEMICOLON(';', 1),
                APOSTROPHE('\'', 1), QUOTE('"', 3), LEFT_ARROW('<', 4), RIGHT_ARROW('>', 4),
                QUESTION('?', 5), SLASH('/', 5), BACKSLASH('\\', 5), PIPE('|', 1),
                TILDE('~', 5), TICK('`', 2), PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3),
                DEFAULT('█', 5);

        private final char character;
        private final int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public int getLength() {
            return length;
        }

        public int getBoldLength() {
            return length + (this == SPACE ? 0 : 1);
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : values()) {
                if (dFI.character == c) return dFI;
            }
            return DEFAULT;
        }
    }
}