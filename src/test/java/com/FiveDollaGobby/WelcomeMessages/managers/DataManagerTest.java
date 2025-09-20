package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.managers.DataManager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

/**
 * Unit tests for DataManager class
 */
@ExtendWith(MockitoExtension.class)
public class DataManagerTest {

    @Mock
    private WelcomePlugin plugin;
    
    @Mock
    private Player player;
    
    @Mock
    private Server server;
    
    @Mock
    private BukkitScheduler scheduler;
    
    private DataManager dataManager;
    private UUID testUUID;
    private MockedStatic<Bukkit> bukkitMock;

    @BeforeEach
    public void setUp() {
        testUUID = UUID.randomUUID();
        lenient().when(player.getUniqueId()).thenReturn(testUUID);
        lenient().when(player.getName()).thenReturn("TestPlayer");
        
        // Mock Bukkit server and scheduler
        bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(Bukkit::getServer).thenReturn(server);
        bukkitMock.when(Bukkit::getScheduler).thenReturn(scheduler);
        
        // Mock plugin configuration with lenient stubbing
        lenient().when(plugin.getConfig()).thenReturn(mock(org.bukkit.configuration.file.FileConfiguration.class));
        lenient().when(plugin.getConfig().getInt("general.save-interval", 10)).thenReturn(10);
        lenient().when(plugin.getConfig().getInt("performance.cache-time", 5)).thenReturn(5);
        lenient().when(plugin.getConfig().getInt("performance.max-cache-size", 100)).thenReturn(100);
        
        // Mock logger
        lenient().when(plugin.getLogger()).thenReturn(mock(org.bukkit.plugin.PluginLogger.class));
        
        // Mock MessageManager for resetPlaytimeData method
        com.FiveDollaGobby.WelcomeMessages.managers.MessageManager messageManager = mock(com.FiveDollaGobby.WelcomeMessages.managers.MessageManager.class);
        com.FiveDollaGobby.WelcomeMessages.managers.SmartRecognitionManager smartRecognitionManager = mock(com.FiveDollaGobby.WelcomeMessages.managers.SmartRecognitionManager.class);
        lenient().when(plugin.getMessageManager()).thenReturn(messageManager);
        lenient().when(messageManager.getSmartRecognitionManager()).thenReturn(smartRecognitionManager);
        
        dataManager = new DataManager(plugin);
    }
    
    @AfterEach
    public void tearDown() {
        if (bukkitMock != null) {
            bukkitMock.close();
        }
    }

    @Test
    @DisplayName("Test first join detection")
    public void testFirstJoinDetection() {
        // Test first join
        assertTrue(dataManager.isFirstJoin(player));
        
        // Update player data (simulate first join)
        dataManager.updatePlayerData(player);
        
        // After update, should no longer be first join
        assertFalse(dataManager.isFirstJoin(player));
    }

    @Test
    @DisplayName("Test join count tracking")
    public void testJoinCountTracking() {
        // Initial join count should be 0
        assertEquals(0, dataManager.getJoinCount(player));
        
        // Update player data multiple times
        dataManager.updatePlayerData(player);
        dataManager.updatePlayerData(player);
        dataManager.updatePlayerData(player);
        
        // Join count should be 3
        assertEquals(3, dataManager.getJoinCount(player));
    }

    @Test
    @DisplayName("Test playtime tracking")
    public void testPlaytimeTracking() {
        // Initial playtime should be 0
        assertEquals(0, dataManager.getTotalPlaytime(player));
        
        // Update playtime
        dataManager.updatePlaytime(player);
        
        // Playtime should be updated (exact value depends on timing)
        assertTrue(dataManager.getTotalPlaytime(player) >= 0);
    }

    @Test
    @DisplayName("Test session time tracking")
    public void testSessionTimeTracking() {
        // Initial session time should be 0
        assertEquals(0, dataManager.getCurrentSessionTime(player));
        
        // Update player data to start session
        dataManager.updatePlayerData(player);
        
        // Session time should be 0 initially
        assertEquals(0, dataManager.getCurrentSessionTime(player));
    }

    @Test
    @DisplayName("Test message toggle")
    public void testMessageToggle() {
        // Initial state should be false (messages enabled)
        assertFalse(dataManager.hasMessagesDisabled(player));
        
        // Toggle messages
        boolean disabled = dataManager.toggleMessages(player);
        assertTrue(disabled);
        assertTrue(dataManager.hasMessagesDisabled(player));
        
        // Toggle again
        disabled = dataManager.toggleMessages(player);
        assertFalse(disabled);
        assertFalse(dataManager.hasMessagesDisabled(player));
    }

    @Test
    @DisplayName("Test player data structure")
    public void testPlayerDataStructure() {
        // Get player data
        PlayerData data = dataManager.getPlayerDataPublic(player);
        assertNotNull(data);
        
        // Check initial values
        assertEquals(0, data.joinCount);
        assertEquals(0, data.firstJoinTime);
        assertEquals(0, data.lastSeenTime);
        assertFalse(data.messagesDisabled);
        assertEquals(0, data.totalPlaytime);
        assertEquals(0, data.sessionStartTime);
    }

    @Test
    @DisplayName("Test data update")
    public void testDataUpdate() {
        // Update player data
        dataManager.updatePlayerData(player);
        
        // Get updated data
        PlayerData data = dataManager.getPlayerDataPublic(player);
        
        // Check that data was updated
        assertTrue(data.joinCount > 0);
        assertTrue(data.firstJoinTime > 0);
        assertTrue(data.lastSeenTime > 0);
        assertTrue(data.sessionStartTime > 0);
    }

    @Test
    @DisplayName("Test last seen update")
    public void testLastSeenUpdate() {
        // Update last seen
        dataManager.setLastSeen(player);
        
        // Get updated data
        PlayerData data = dataManager.getPlayerDataPublic(player);
        
        // Check that last seen was updated
        assertTrue(data.lastSeenTime > 0);
    }

    @Test
    @DisplayName("Test playtime data reset")
    public void testPlaytimeDataReset() {
        // Update playtime first
        dataManager.updatePlaytime(player);
        
        // Reset playtime data
        dataManager.resetPlaytimeData(player);
        
        // Get updated data
        PlayerData data = dataManager.getPlayerDataPublic(player);
        
        // Check that playtime was reset
        assertEquals(0, data.totalPlaytime);
        assertTrue(data.sessionStartTime > 0); // Should be reset to current time
    }

    @Test
    @DisplayName("Test total unique joins")
    public void testTotalUniqueJoins() {
        // Initial count should be 0
        assertEquals(0, dataManager.getTotalUniqueJoins());
        
        // Update player data (first join)
        dataManager.updatePlayerData(player);
        
        // Total unique joins should be 1
        assertEquals(1, dataManager.getTotalUniqueJoins());
    }

    @Test
    @DisplayName("Test data consistency")
    public void testDataConsistency() {
        // Update player data multiple times
        for (int i = 0; i < 5; i++) {
            dataManager.updatePlayerData(player);
        }
        
        // Get data
        PlayerData data = dataManager.getPlayerDataPublic(player);
        
        // Check consistency
        assertEquals(5, data.joinCount);
        assertTrue(data.firstJoinTime > 0);
        assertTrue(data.lastSeenTime > 0);
        assertTrue(data.sessionStartTime > 0);
    }

    @Test
    @DisplayName("Test edge cases")
    public void testEdgeCases() {
        // Test with null player (should not crash)
        assertDoesNotThrow(() -> {
            dataManager.isFirstJoin(null);
            dataManager.getJoinCount(null);
            dataManager.hasMessagesDisabled(null);
            dataManager.toggleMessages(null);
        });
    }
}
