package com.FiveDollaGobby.WelcomeMessages.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SecurityUtils class
 */
public class SecurityUtilsTest {

    @Test
    @DisplayName("Test valid player name sanitization")
    public void testValidPlayerNameSanitization() {
        // Valid player names
        assertNotNull(SecurityUtils.sanitizePlayerName("Player123"));
        assertNotNull(SecurityUtils.sanitizePlayerName("Test_Player"));
        assertNotNull(SecurityUtils.sanitizePlayerName("Test-Player"));
        assertNotNull(SecurityUtils.sanitizePlayerName("A"));
        assertNotNull(SecurityUtils.sanitizePlayerName("1234567890123456")); // Max length
        
        // Test exact values
        assertEquals("Player123", SecurityUtils.sanitizePlayerName("Player123"));
        assertEquals("Test_Player", SecurityUtils.sanitizePlayerName("Test_Player"));
        assertEquals("Test-Player", SecurityUtils.sanitizePlayerName("Test-Player"));
    }

    @Test
    @DisplayName("Test invalid player name sanitization")
    public void testInvalidPlayerNameSanitization() {
        // Invalid player names
        assertNull(SecurityUtils.sanitizePlayerName(""));
        assertNull(SecurityUtils.sanitizePlayerName("   "));
        assertNull(SecurityUtils.sanitizePlayerName(null));
        assertNull(SecurityUtils.sanitizePlayerName("Player@123")); // Invalid character
        assertNull(SecurityUtils.sanitizePlayerName("Player 123")); // Space
        assertNull(SecurityUtils.sanitizePlayerName("Player#123")); // Invalid character
        assertNull(SecurityUtils.sanitizePlayerName("12345678901234567")); // Too long
        assertNull(SecurityUtils.sanitizePlayerName("")); // Empty
    }

    @Test
    @DisplayName("Test text sanitization")
    public void testTextSanitization() {
        // Valid text
        assertEquals("Hello World", SecurityUtils.sanitizeText("Hello World"));
        assertEquals("Test123", SecurityUtils.sanitizeText("Test123"));
        assertEquals("", SecurityUtils.sanitizeText(""));
        assertEquals("", SecurityUtils.sanitizeText(null));
        
        // Text with control characters
        assertEquals("HelloWorld", SecurityUtils.sanitizeText("Hello\u0000World"));
        assertEquals("Test", SecurityUtils.sanitizeText("Test\u0001\u0002\u0003"));
        
        // Long text truncation
        String longText = "A".repeat(2000);
        String result = SecurityUtils.sanitizeText(longText);
        assertEquals(1000, result.length());
        assertTrue(result.endsWith("..."));
    }

    @Test
    @DisplayName("Test theme name sanitization")
    public void testThemeNameSanitization() {
        // Valid theme names
        assertNotNull(SecurityUtils.sanitizeThemeName("halloween"));
        assertNotNull(SecurityUtils.sanitizeThemeName("christmas"));
        assertNotNull(SecurityUtils.sanitizeThemeName("test-theme"));
        assertNotNull(SecurityUtils.sanitizeThemeName("test_theme"));
        assertNotNull(SecurityUtils.sanitizeThemeName("theme123"));
        
        // Test case conversion
        assertEquals("halloween", SecurityUtils.sanitizeThemeName("HALLOWEEN"));
        assertEquals("christmas", SecurityUtils.sanitizeThemeName("Christmas"));
        assertEquals("test-theme", SecurityUtils.sanitizeThemeName("Test-Theme"));
    }

    @Test
    @DisplayName("Test invalid theme name sanitization")
    public void testInvalidThemeNameSanitization() {
        // Invalid theme names
        assertNull(SecurityUtils.sanitizeThemeName(""));
        assertNull(SecurityUtils.sanitizeThemeName("   "));
        assertNull(SecurityUtils.sanitizeThemeName(null));
        assertNull(SecurityUtils.sanitizeThemeName("theme@123")); // Invalid character
        assertNull(SecurityUtils.sanitizeThemeName("theme 123")); // Space
        assertNull(SecurityUtils.sanitizeThemeName("theme#123")); // Invalid character
    }

    @Test
    @DisplayName("Test animation type sanitization")
    public void testAnimationTypeSanitization() {
        // Valid animation types
        assertNotNull(SecurityUtils.sanitizeAnimationType("typing"));
        assertNotNull(SecurityUtils.sanitizeAnimationType("rainbow"));
        assertNotNull(SecurityUtils.sanitizeAnimationType("epic-welcome"));
        assertNotNull(SecurityUtils.sanitizeAnimationType("epic_welcome"));
        assertNotNull(SecurityUtils.sanitizeAnimationType("test123"));
        
        // Test case conversion
        assertEquals("typing", SecurityUtils.sanitizeAnimationType("TYPING"));
        assertEquals("rainbow", SecurityUtils.sanitizeAnimationType("Rainbow"));
        assertEquals("epic-welcome", SecurityUtils.sanitizeAnimationType("Epic-Welcome"));
    }

    @Test
    @DisplayName("Test invalid animation type sanitization")
    public void testInvalidAnimationTypeSanitization() {
        // Invalid animation types
        assertNull(SecurityUtils.sanitizeAnimationType(""));
        assertNull(SecurityUtils.sanitizeAnimationType("   "));
        assertNull(SecurityUtils.sanitizeAnimationType(null));
        assertNull(SecurityUtils.sanitizeAnimationType("typing@123")); // Invalid character
        assertNull(SecurityUtils.sanitizeAnimationType("typing 123")); // Space
        assertNull(SecurityUtils.sanitizeAnimationType("typing#123")); // Invalid character
    }

    @Test
    @DisplayName("Test hex color validation")
    public void testHexColorValidation() {
        // Valid hex colors
        assertTrue(SecurityUtils.isValidHexColor("#FF0000"));
        assertTrue(SecurityUtils.isValidHexColor("#00FF00"));
        assertTrue(SecurityUtils.isValidHexColor("#0000FF"));
        assertTrue(SecurityUtils.isValidHexColor("#ffffff"));
        assertTrue(SecurityUtils.isValidHexColor("#000000"));
        assertTrue(SecurityUtils.isValidHexColor("#123ABC"));
        
        // Invalid hex colors
        assertFalse(SecurityUtils.isValidHexColor(""));
        assertFalse(SecurityUtils.isValidHexColor(null));
        assertFalse(SecurityUtils.isValidHexColor("#FF00")); // Too short
        assertFalse(SecurityUtils.isValidHexColor("#FF00000")); // Too long
        assertFalse(SecurityUtils.isValidHexColor("FF0000")); // Missing #
        assertFalse(SecurityUtils.isValidHexColor("#GG0000")); // Invalid character
        assertFalse(SecurityUtils.isValidHexColor("#FF000")); // Too short
    }

    @Test
    @DisplayName("Test HTML escaping")
    public void testHtmlEscaping() {
        // Test HTML escaping
        assertEquals("&amp;", SecurityUtils.escapeHtml("&"));
        assertEquals("&lt;", SecurityUtils.escapeHtml("<"));
        assertEquals("&gt;", SecurityUtils.escapeHtml(">"));
        assertEquals("&quot;", SecurityUtils.escapeHtml("\""));
        assertEquals("&#x27;", SecurityUtils.escapeHtml("'"));
        
        // Test complex HTML
        assertEquals("&lt;script&gt;alert(&#x27;test&#x27;)&lt;/script&gt;", 
                    SecurityUtils.escapeHtml("<script>alert('test')</script>"));
        
        // Test null and empty
        assertEquals("", SecurityUtils.escapeHtml(null));
        assertEquals("", SecurityUtils.escapeHtml(""));
    }

    @Test
    @DisplayName("Test safe display validation")
    public void testSafeDisplayValidation() {
        // Safe content
        assertTrue(SecurityUtils.isSafeForDisplay("Hello World"));
        assertTrue(SecurityUtils.isSafeForDisplay("Test123"));
        assertTrue(SecurityUtils.isSafeForDisplay(""));
        assertTrue(SecurityUtils.isSafeForDisplay(null));
        assertTrue(SecurityUtils.isSafeForDisplay("Normal text with numbers 123"));
        
        // Unsafe content
        assertFalse(SecurityUtils.isSafeForDisplay("<script>alert('test')</script>"));
        assertFalse(SecurityUtils.isSafeForDisplay("javascript:alert('test')"));
        assertFalse(SecurityUtils.isSafeForDisplay("data:text/html,<script>alert('test')</script>"));
        assertFalse(SecurityUtils.isSafeForDisplay("vbscript:msgbox('test')"));
        assertFalse(SecurityUtils.isSafeForDisplay("onload=alert('test')"));
        assertFalse(SecurityUtils.isSafeForDisplay("onerror=alert('test')"));
        assertFalse(SecurityUtils.isSafeForDisplay("onclick=alert('test')"));
        assertFalse(SecurityUtils.isSafeForDisplay("onmouseover=alert('test')"));
        
        // Case insensitive
        assertFalse(SecurityUtils.isSafeForDisplay("<SCRIPT>alert('test')</SCRIPT>"));
        assertFalse(SecurityUtils.isSafeForDisplay("JAVASCRIPT:alert('test')"));
        assertFalse(SecurityUtils.isSafeForDisplay("ONLOAD=alert('test')"));
    }
}
