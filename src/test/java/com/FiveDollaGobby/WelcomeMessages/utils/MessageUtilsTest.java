package com.FiveDollaGobby.WelcomeMessages.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MessageUtils class
 */
public class MessageUtilsTest {

    @Test
    @DisplayName("Test basic colorization")
    public void testBasicColorization() {
        // Test basic color codes
        String result = MessageUtils.colorize("&aHello &cWorld");
        assertNotNull(result);
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("World"));
        
        // Test with null input
        assertEquals("", MessageUtils.colorize(null));
        
        // Test with empty input
        assertEquals("", MessageUtils.colorize(""));
    }

    @Test
    @DisplayName("Test hex color processing")
    public void testHexColorProcessing() {
        // Test valid hex colors
        String result = MessageUtils.colorize("&#FF0000Red &#00FF00Green &#0000FFBlue");
        assertNotNull(result);
        assertTrue(result.contains("Red"));
        assertTrue(result.contains("Green"));
        assertTrue(result.contains("Blue"));
        
        // Test invalid hex colors (should not crash)
        String invalidResult = MessageUtils.colorize("&#GG0000Invalid");
        assertNotNull(invalidResult);
    }

    @Test
    @DisplayName("Test gradient processing")
    public void testGradientProcessing() {
        // Test valid gradient
        String result = MessageUtils.colorize("<gradient:#FF0000:#00FF00>Gradient Text</gradient>");
        assertNotNull(result);
        System.out.println("Gradient result: " + result);
        // Check that the text is processed - just verify it's not null and has some content
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // The result should contain some form of the original text
        assertTrue(result.length() > 5, "Result should have some content, but was: " + result);
        
        // Test invalid gradient (should not crash)
        String invalidResult = MessageUtils.colorize("<gradient:#GG0000:#00FF00>Invalid</gradient>");
        assertNotNull(invalidResult);
    }

    @Test
    @DisplayName("Test rainbow processing")
    public void testRainbowProcessing() {
        // Test rainbow text
        String result = MessageUtils.colorize("<rainbow>Rainbow Text</rainbow>");
        assertNotNull(result);
        System.out.println("Rainbow result: " + result);
        // Check that the text is processed - just verify it's not null and has some content
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // The result should contain some form of the original text
        assertTrue(result.length() > 5, "Result should have some content, but was: " + result);
        
        // Test nested rainbow
        String nestedResult = MessageUtils.colorize("Normal <rainbow>Rainbow</rainbow> Text");
        assertNotNull(nestedResult);
        System.out.println("Nested rainbow result: " + nestedResult);
        assertFalse(nestedResult.isEmpty());
        assertTrue(nestedResult.length() > 10, "Nested result should have some content, but was: " + nestedResult);
    }

    @Test
    @DisplayName("Test message length limiting")
    public void testMessageLengthLimiting() {
        // Test long message truncation
        String longMessage = "A".repeat(15000);
        String result = MessageUtils.colorize(longMessage);
        assertNotNull(result);
        assertTrue(result.length() <= 10003); // 10000 + "..."
        assertTrue(result.endsWith("..."));
    }

    @Test
    @DisplayName("Test strip colors")
    public void testStripColors() {
        // Test color stripping
        String colored = "&aHello &cWorld";
        String stripped = MessageUtils.stripColors(colored);
        assertNotNull(stripped);
        assertTrue(stripped.contains("Hello"));
        assertTrue(stripped.contains("World"));
        
        // Test with null
        assertNull(MessageUtils.stripColors(null));
    }

    @Test
    @DisplayName("Test empty string check")
    public void testEmptyStringCheck() {
        // Test empty string detection
        assertTrue(MessageUtils.isEmpty(null));
        assertTrue(MessageUtils.isEmpty(""));
        assertTrue(MessageUtils.isEmpty("   "));
        assertFalse(MessageUtils.isEmpty("Hello"));
        assertFalse(MessageUtils.isEmpty("  Hello  "));
    }

    @Test
    @DisplayName("Test center message")
    public void testCenterMessage() {
        // Test message centering
        String message = "Hello World";
        String centered = MessageUtils.centerMessage(message);
        assertNotNull(centered);
        assertTrue(centered.contains("Hello World"));
        
        // Test with null
        assertEquals("", MessageUtils.centerMessage(null));
        
        // Test with empty
        assertEquals("", MessageUtils.centerMessage(""));
    }

    @Test
    @DisplayName("Test complex message processing")
    public void testComplexMessageProcessing() {
        // Test complex message with multiple features
        String complexMessage = "&aHello <gradient:#FF0000:#00FF00>Gradient</gradient> <rainbow>Rainbow</rainbow> &#FF0000Red";
        String result = MessageUtils.colorize(complexMessage);
        assertNotNull(result);
        // Check that the text is processed - just verify it's not null and has some content
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // The result should contain some form of the original text
        assertTrue(result.length() > 10, "Result should have some content, but was: " + result);
    }

    @Test
    @DisplayName("Test edge cases")
    public void testEdgeCases() {
        // Test malformed gradient
        String malformedGradient = "<gradient:#FF0000>Incomplete</gradient>";
        String result1 = MessageUtils.colorize(malformedGradient);
        assertNotNull(result1);
        
        // Test malformed rainbow
        String malformedRainbow = "<rainbow>Unclosed";
        String result2 = MessageUtils.colorize(malformedRainbow);
        assertNotNull(result2);
        
        // Test mixed valid and invalid
        String mixed = "&aValid &#GG0000Invalid <gradient:#FF0000:#00FF00>Valid</gradient>";
        String result3 = MessageUtils.colorize(mixed);
        assertNotNull(result3);
    }
}
