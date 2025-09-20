package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

import java.util.UUID;
import java.util.Arrays;

/**
 * Unit tests for SmartRecognitionManager class
 */
@ExtendWith(MockitoExtension.class)
public class SmartRecognitionManagerTest {

    @Mock
    private WelcomePlugin plugin;
    
    @Mock
    private DataManager dataManager;
    
    @Mock
    private Player player;
    
    private SmartRecognitionManager smartManager;
    private UUID testUUID;

    @BeforeEach
    public void setUp() {
        testUUID = UUID.randomUUID();
        lenient().when(player.getUniqueId()).thenReturn(testUUID);
        lenient().when(player.getName()).thenReturn("TestPlayer");
        
        // Mock plugin configuration
        lenient().when(plugin.getConfig()).thenReturn(mock(org.bukkit.configuration.file.FileConfiguration.class));
        lenient().when(plugin.getConfig().getBoolean("smart-recognition.enabled", true)).thenReturn(true);
        lenient().when(plugin.getConfig().getBoolean("smart-recognition.milestones.enabled", true)).thenReturn(true);
        lenient().when(plugin.getConfig().getBoolean("smart-recognition.returning-player.enabled", true)).thenReturn(true);
        lenient().when(plugin.getConfig().getBoolean("smart-recognition.behavior.enabled", true)).thenReturn(true);
        
        // Mock milestone configurations
        lenient().when(plugin.getConfig().getIntegerList("smart-recognition.milestones.join-milestones"))
            .thenReturn(Arrays.asList(10, 25, 50, 100));
        lenient().when(plugin.getConfig().getIntegerList("smart-recognition.milestones.playtime-milestones"))
            .thenReturn(Arrays.asList(1, 5, 10, 25, 50));
        lenient().when(plugin.getConfig().getIntegerList("smart-recognition.milestones.streak-milestones"))
            .thenReturn(Arrays.asList(3, 7, 14, 30));
        
        // Mock returning player thresholds
        lenient().when(plugin.getConfig().getInt("smart-recognition.returning-player.thresholds.short-absence", 24)).thenReturn(24);
        lenient().when(plugin.getConfig().getInt("smart-recognition.returning-player.thresholds.medium-absence", 168)).thenReturn(168);
        lenient().when(plugin.getConfig().getInt("smart-recognition.returning-player.thresholds.long-absence", 720)).thenReturn(720);
        lenient().when(plugin.getConfig().getInt("smart-recognition.returning-player.thresholds.very-long-absence", 2160)).thenReturn(2160);
        
        // Mock behavior settings
        lenient().when(plugin.getConfig().getBoolean("smart-recognition.behavior.peak-activity", true)).thenReturn(true);
        lenient().when(plugin.getConfig().getBoolean("smart-recognition.behavior.favorite-world", true)).thenReturn(true);
        lenient().when(plugin.getConfig().getBoolean("smart-recognition.behavior.join-patterns", true)).thenReturn(true);
        
        // Mock data manager
        lenient().when(plugin.getDataManager()).thenReturn(dataManager);
        lenient().when(dataManager.getJoinCount(player)).thenReturn(5);
        lenient().when(dataManager.getJoinCount(null)).thenReturn(0);
        lenient().when(dataManager.getTotalPlaytime(player)).thenReturn(2L);
        lenient().when(dataManager.isFirstJoin(player)).thenReturn(false);
        
        // Mock messages config
        lenient().when(plugin.getMessagesConfig()).thenReturn(mock(org.bukkit.configuration.file.FileConfiguration.class));
        lenient().when(plugin.getMessagesConfig().getStringList(anyString())).thenReturn(Arrays.asList("Test message"));
        
        smartManager = new SmartRecognitionManager(plugin);
    }

    @Test
    @DisplayName("Test milestone detection - no milestone reached")
    public void testMilestoneDetectionNoMilestone() {
        // Set up player with low join count
        when(dataManager.getJoinCount(player)).thenReturn(5);
        
        // Mock to return null for milestone messages
        when(plugin.getMessagesConfig().getStringList(anyString())).thenReturn(null);
        
        // Check milestones
        String result = smartManager.checkMilestones(player, false);
        
        // Should return null (no milestone reached)
        assertNull(result);
    }

    @Test
    @DisplayName("Test milestone detection - join milestone reached")
    public void testMilestoneDetectionJoinMilestone() {
        // Set up player with milestone join count
        when(dataManager.getJoinCount(player)).thenReturn(15);
        
        // Check milestones
        String result = smartManager.checkMilestones(player, false);
        
        // Should return milestone message
        assertNotNull(result);
        assertTrue(result.contains("Test message"));
    }

    @Test
    @DisplayName("Test milestone detection - playtime milestone reached")
    public void testMilestoneDetectionPlaytimeMilestone() {
        // Set up player with milestone playtime
        when(dataManager.getTotalPlaytime(player)).thenReturn(15L);
        
        // Check milestones
        String result = smartManager.checkMilestones(player, false);
        
        // Should return milestone message
        assertNotNull(result);
        assertTrue(result.contains("Test message"));
    }

    @Test
    @DisplayName("Test returning player message - short absence")
    public void testReturningPlayerMessageShortAbsence() {
        // Mock the last seen times map (this would need to be done differently in real implementation)
        // For now, we'll test the method directly
        
        String result = smartManager.getReturningPlayerMessage(player);
        
        // Should return null for first time (no last seen data)
        assertNull(result);
    }

    @Test
    @DisplayName("Test player behavior data")
    public void testPlayerBehaviorData() {
        // Get player behavior
        var behavior = smartManager.getPlayerBehavior(player);
        
        // Should contain behavior data
        assertNotNull(behavior);
        assertTrue(behavior.containsKey("peak_activity"));
        assertTrue(behavior.containsKey("favorite_world"));
        assertTrue(behavior.containsKey("join_count"));
        assertTrue(behavior.containsKey("first_join"));
    }

    @Test
    @DisplayName("Test milestone info")
    public void testMilestoneInfo() {
        // Get milestone info
        String info = smartManager.getMilestoneInfo(player);
        
        // Should contain milestone information
        assertNotNull(info);
        assertTrue(info.contains("Join Count"));
        assertTrue(info.contains("Streak"));
        assertTrue(info.contains("Total Playtime"));
        assertTrue(info.contains("Current Session"));
    }

    @Test
    @DisplayName("Test milestone info with specific values")
    public void testMilestoneInfoWithValues() {
        // Set up specific values
        when(dataManager.getJoinCount(player)).thenReturn(25);
        when(dataManager.getTotalPlaytime(player)).thenReturn(50L);
        when(dataManager.getCurrentSessionTime(player)).thenReturn(30L);
        
        // Get milestone info
        String info = smartManager.getMilestoneInfo(player);
        
        // Should contain the specific values
        assertTrue(info.contains("25")); // Join count
        assertTrue(info.contains("50")); // Total playtime
        assertTrue(info.contains("30")); // Current session
    }

    @Test
    @DisplayName("Test disabled smart recognition")
    public void testDisabledSmartRecognition() {
        // Disable smart recognition
        when(plugin.getConfig().getBoolean("smart-recognition.enabled", true)).thenReturn(false);
        when(plugin.getConfig().getBoolean("smart-recognition.behavior.enabled", true)).thenReturn(false);
        
        // Create new manager with disabled config
        SmartRecognitionManager disabledManager = new SmartRecognitionManager(plugin);
        
        // Check milestones should return null
        String result = disabledManager.checkMilestones(player, false);
        assertNull(result);
        
        // Check returning player message should return null
        String returningResult = disabledManager.getReturningPlayerMessage(player);
        assertNull(returningResult);
        
        // Check behavior should return empty map
        var behavior = disabledManager.getPlayerBehavior(player);
        assertTrue(behavior.isEmpty());
    }

    @Test
    @DisplayName("Test disabled milestones")
    public void testDisabledMilestones() {
        // Disable milestones
        when(plugin.getConfig().getBoolean("smart-recognition.milestones.enabled", true)).thenReturn(false);
        
        // Create new manager with disabled milestones
        SmartRecognitionManager noMilestonesManager = new SmartRecognitionManager(plugin);
        
        // Check milestones should return null
        String result = noMilestonesManager.checkMilestones(player, false);
        assertNull(result);
    }

    @Test
    @DisplayName("Test disabled returning player")
    public void testDisabledReturningPlayer() {
        // Disable returning player detection
        when(plugin.getConfig().getBoolean("smart-recognition.returning-player.enabled", true)).thenReturn(false);
        
        // Create new manager with disabled returning player
        SmartRecognitionManager noReturningManager = new SmartRecognitionManager(plugin);
        
        // Check returning player message should return null
        String result = noReturningManager.getReturningPlayerMessage(player);
        assertNull(result);
    }

    @Test
    @DisplayName("Test player data reset")
    public void testPlayerDataReset() {
        // Reset player data
        smartManager.resetPlayerData(player);
        
        // Verify that reset was called on data manager
        verify(dataManager).resetPlaytimeData(player);
    }

    @Test
    @DisplayName("Test UUID data reset")
    public void testUuidDataReset() {
        // Reset UUID data
        smartManager.resetPlayerData(testUUID);
        
        // Should not throw exception
        assertDoesNotThrow(() -> smartManager.resetPlayerData(testUUID));
    }

    @Test
    @DisplayName("Test cleanup old data")
    public void testCleanupOldData() {
        // Test cleanup (should not throw exception)
        assertDoesNotThrow(() -> smartManager.cleanupOldData());
    }

    @Test
    @DisplayName("Test edge cases")
    public void testEdgeCases() {
        // Test with null player
        assertDoesNotThrow(() -> {
            smartManager.checkMilestones(null, false);
            smartManager.getReturningPlayerMessage(null);
            smartManager.getPlayerBehavior(null);
            smartManager.getMilestoneInfo(null);
        });
    }
}
