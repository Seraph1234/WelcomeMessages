package com.FiveDollaGobby.WelcomeMessages.commands;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.managers.DataManager;
import com.FiveDollaGobby.WelcomeMessages.managers.MessageManager;
import com.FiveDollaGobby.WelcomeMessages.managers.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for WelcomeCommand class
 */
@ExtendWith(MockitoExtension.class)
public class WelcomeCommandTest {

    @Mock
    private WelcomePlugin plugin;
    
    @Mock
    private CommandSender sender;
    
    @Mock
    private Player player;
    
    @Mock
    private Command command;
    
    @Mock
    private DataManager dataManager;
    
    @Mock
    private MessageManager messageManager;
    
    @Mock
    private EffectManager effectManager;
    
    private WelcomeCommand welcomeCommand;

    @BeforeEach
    public void setUp() {
        // Use lenient stubbing to avoid unnecessary stubbing exceptions
        lenient().when(plugin.getDataManager()).thenReturn(dataManager);
        lenient().when(plugin.getMessageManager()).thenReturn(messageManager);
        lenient().when(plugin.getEffectManager()).thenReturn(effectManager);
        
        // Mock plugin configuration
        lenient().when(plugin.getConfig()).thenReturn(mock(org.bukkit.configuration.file.FileConfiguration.class));
        lenient().when(plugin.getMessagesConfig()).thenReturn(mock(org.bukkit.configuration.file.FileConfiguration.class));
        lenient().when(plugin.getMessagesConfig().getString(anyString())).thenReturn("Test message");
        lenient().when(plugin.getMessagesConfig().getStringList(anyString())).thenReturn(Arrays.asList("Test message"));
        
        welcomeCommand = new WelcomeCommand(plugin);
    }

    @Test
    @DisplayName("Test help command")
    public void testHelpCommand() {
        // Test help command
        String[] args = {"help"};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test version command")
    public void testVersionCommand() {
        // Mock plugin description
        org.bukkit.plugin.PluginDescriptionFile description = mock(org.bukkit.plugin.PluginDescriptionFile.class);
        when(description.getVersion()).thenReturn("1.0.0");
        when(plugin.getDescription()).thenReturn(description);
        
        // Test version command
        String[] args = {"version"};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test reload command without permission")
    public void testReloadCommandWithoutPermission() {
        // Mock sender without permission
        when(sender.hasPermission("welcome.reload")).thenReturn(false);
        
        String[] args = {"reload"};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
        
        assertTrue(result);
        // Should show no permission message
    }

    @Test
    @DisplayName("Test reload command with permission")
    public void testReloadCommandWithPermission() {
        // Mock sender with permission
        when(sender.hasPermission("welcome.reload")).thenReturn(true);
        
        String[] args = {"reload"};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
        
        assertTrue(result);
        // Should attempt reload
    }

    @Test
    @DisplayName("Test test command without permission")
    public void testTestCommandWithoutPermission() {
        // Mock sender without permission
        when(sender.hasPermission("welcome.test")).thenReturn(false);
        
        String[] args = {"test"};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
        
        assertTrue(result);
        // Should show no permission message
    }

    @Test
    @DisplayName("Test test command with permission")
    public void testTestCommandWithPermission() {
        // Mock sender as player with permission
        when(player.hasPermission("welcome.test")).thenReturn(true);
        when(player.getName()).thenReturn("TestPlayer");
        when(dataManager.isFirstJoin(player)).thenReturn(false);
        when(messageManager.getJoinMessage(player, false)).thenReturn("Test join message");
        when(messageManager.getQuitMessage(player)).thenReturn("Test quit message");
        
        // Mock Bukkit.getScheduler()
        try (MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class)) {
            org.bukkit.scheduler.BukkitScheduler scheduler = mock(org.bukkit.scheduler.BukkitScheduler.class);
            bukkitMock.when(Bukkit::getScheduler).thenReturn(scheduler);
            
            String[] args = {"test"};
            boolean result = welcomeCommand.onCommand(player, command, "welcome", args);
            
            assertTrue(result);
        }
    }

    @Test
    @DisplayName("Test test command with player argument")
    public void testTestCommandWithPlayerArgument() {
        // Mock sender with permission
        when(sender.hasPermission("welcome.test")).thenReturn(true);
        when(player.getName()).thenReturn("TestPlayer");
        when(dataManager.isFirstJoin(player)).thenReturn(false);
        when(messageManager.getJoinMessage(player, false)).thenReturn("Test join message");
        when(messageManager.getQuitMessage(player)).thenReturn("Test quit message");
        
        // Mock Bukkit.getPlayer()
        try (MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class)) {
            bukkitMock.when(() -> Bukkit.getPlayer("TestPlayer")).thenReturn(player);
            
            String[] args = {"test", "TestPlayer"};
            boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
            
            assertTrue(result);
        }
    }

    @Test
    @DisplayName("Test testall command")
    public void testTestAllCommand() {
        // Mock sender with permission
        when(player.hasPermission("welcome.test")).thenReturn(true);
        when(player.getName()).thenReturn("TestPlayer");
        when(dataManager.isFirstJoin(player)).thenReturn(false);
        when(messageManager.getJoinMessage(player, false)).thenReturn("Test join message");
        when(messageManager.getQuitMessage(player)).thenReturn("Test quit message");
        
        // Mock server and scheduler
        org.bukkit.Server server = mock(org.bukkit.Server.class);
        org.bukkit.scheduler.BukkitScheduler scheduler = mock(org.bukkit.scheduler.BukkitScheduler.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        when(scheduler.runTaskLater(any(org.bukkit.plugin.Plugin.class), any(Runnable.class), anyLong())).thenReturn(mock(org.bukkit.scheduler.BukkitTask.class));
        
        String[] args = {"testall"};
        boolean result = welcomeCommand.onCommand(player, command, "welcome", args);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test testanim command")
    public void testTestAnimCommand() {
        // Mock sender as player with permission
        when(player.hasPermission("welcome.test")).thenReturn(true);
        when(player.getName()).thenReturn("TestPlayer");
        
        // Mock Bukkit.getScheduler()
        try (MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class)) {
            org.bukkit.scheduler.BukkitScheduler scheduler = mock(org.bukkit.scheduler.BukkitScheduler.class);
            bukkitMock.when(Bukkit::getScheduler).thenReturn(scheduler);
            
            String[] args = {"testanim", "typing"};
            boolean result = welcomeCommand.onCommand(player, command, "welcome", args);
            
            assertTrue(result);
        }
    }

    @Test
    @DisplayName("Test testtheme command")
    public void testTestThemeCommand() {
        // Mock sender as player with permission
        when(player.hasPermission("welcome.test")).thenReturn(true);
        when(player.getName()).thenReturn("TestPlayer");
        when(player.getWorld()).thenReturn(mock(org.bukkit.World.class));
        when(player.getWorld().getName()).thenReturn("world");
        // Mock server and scheduler
        org.bukkit.Server server = mock(org.bukkit.Server.class);
        org.bukkit.scheduler.BukkitScheduler scheduler = mock(org.bukkit.scheduler.BukkitScheduler.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        // Mock online players - using a simple approach
        lenient().when(server.getOnlinePlayers()).thenReturn(Collections.emptyList());
        lenient().when(server.getMaxPlayers()).thenReturn(20);
        when(dataManager.getJoinCount(player)).thenReturn(5);
        when(dataManager.isFirstJoin(player)).thenReturn(false);
        
        // Mock theme manager
        com.FiveDollaGobby.WelcomeMessages.managers.ThemeManager themeManager = mock(com.FiveDollaGobby.WelcomeMessages.managers.ThemeManager.class);
        when(messageManager.getThemeManager()).thenReturn(themeManager);
        when(themeManager.getThemeMessages(anyString(), anyBoolean())).thenReturn(Arrays.asList("Test theme message"));
        
        // Mock scheduler runTaskLater method
        when(scheduler.runTaskLater(any(org.bukkit.plugin.Plugin.class), any(Runnable.class), anyLong())).thenReturn(mock(org.bukkit.scheduler.BukkitTask.class));
        
        String[] args = {"testtheme", "halloween"};
        boolean result = welcomeCommand.onCommand(player, command, "welcome", args);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test testmilestone command")
    public void testTestMilestoneCommand() {
        // Mock sender as player with permission
        when(player.hasPermission("welcome.test")).thenReturn(true);
        when(player.getName()).thenReturn("TestPlayer");
        
        // Mock smart recognition manager
        when(messageManager.getSmartRecognitionManager()).thenReturn(mock(com.FiveDollaGobby.WelcomeMessages.managers.SmartRecognitionManager.class));
        when(messageManager.getSmartRecognitionManager().getMilestoneInfo(player)).thenReturn("Test milestone info");
        when(messageManager.getSmartRecognitionManager().checkMilestones(player, false)).thenReturn("Test milestone message");
        when(messageManager.getSmartRecognitionManager().getReturningPlayerMessage(player)).thenReturn("Test returning message");
        
        String[] args = {"testmilestone"};
        boolean result = welcomeCommand.onCommand(player, command, "welcome", args);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test testplaytime command")
    public void testTestPlaytimeCommand() {
        // Mock sender as player with permission
        when(player.hasPermission("welcome.test")).thenReturn(true);
        when(player.getName()).thenReturn("TestPlayer");
        when(dataManager.getTotalPlaytime(player)).thenReturn(10L);
        when(dataManager.getCurrentSessionTime(player)).thenReturn(5L);
        when(dataManager.getJoinCount(player)).thenReturn(5);
        when(plugin.getConfig().getIntegerList("smart-recognition.milestones.playtime-milestones")).thenReturn(Arrays.asList(1, 5, 10, 25));
        
        String[] args = {"testplaytime"};
        boolean result = welcomeCommand.onCommand(player, command, "welcome", args);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test stats command")
    public void testStatsCommand() {
        // Mock sender as player with permission
        lenient().when(sender.hasPermission("welcome.stats")).thenReturn(true);
        lenient().when(player.getName()).thenReturn("TestPlayer");
        lenient().when(dataManager.getJoinCount(player)).thenReturn(5);
        lenient().when(dataManager.isFirstJoin(player)).thenReturn(false);
        lenient().when(dataManager.hasMessagesDisabled(player)).thenReturn(false);
        lenient().when(dataManager.getTotalPlaytime(player)).thenReturn(10L);
        lenient().when(dataManager.getCurrentSessionTime(player)).thenReturn(5L);
        
        String[] args = {"stats"};
        boolean result = welcomeCommand.onCommand(player, command, "welcome", args);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test reset command without permission")
    public void testResetCommandWithoutPermission() {
        // Mock sender without permission
        when(sender.hasPermission("welcome.reset")).thenReturn(false);
        
        String[] args = {"reset", "TestPlayer"};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
        
        assertTrue(result);
        // Should show no permission message
    }

    @Test
    @DisplayName("Test reset command with permission")
    public void testResetCommandWithPermission() {
        // Mock sender with permission
        when(sender.hasPermission("welcome.reset")).thenReturn(true);
        
        String[] args = {"reset", "TestPlayer"};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
        
        assertTrue(result);
        // Should attempt reset
    }

    @Test
    @DisplayName("Test toggle command")
    public void testToggleCommand() {
        // Mock sender as player with permission
        when(player.hasPermission("welcome.toggle")).thenReturn(true);
        when(dataManager.toggleMessages(player)).thenReturn(true);
        
        String[] args = {"toggle"};
        boolean result = welcomeCommand.onCommand(player, command, "welcome", args);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test unknown command")
    public void testUnknownCommand() {
        String[] args = {"unknown"};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", args);
        
        assertTrue(result);
        // Should show unknown command message
    }

    @Test
    @DisplayName("Test tab completion")
    public void testTabCompletion() {
        // Test first argument completion
        String[] args = {"t"};
        List<String> completions = welcomeCommand.onTabComplete(sender, command, "welcome", args);
        
        assertNotNull(completions);
        assertTrue(completions.contains("test"));
        assertTrue(completions.contains("testall"));
        assertTrue(completions.contains("testanim"));
        assertTrue(completions.contains("testtheme"));
        assertTrue(completions.contains("testmilestone"));
        assertTrue(completions.contains("testplaytime"));
    }

    @Test
    @DisplayName("Test tab completion for testanim")
    public void testTabCompletionForTestAnim() {
        // Test second argument completion for testanim
        String[] args = {"testanim", "t"};
        List<String> completions = welcomeCommand.onTabComplete(sender, command, "welcome", args);
        
        assertNotNull(completions);
        assertTrue(completions.contains("typing"));
        assertTrue(completions.contains("typewriter"));
    }

    @Test
    @DisplayName("Test tab completion for player names")
    public void testTabCompletionForPlayerNames() {
        // Mock online players
        when(plugin.getServer()).thenReturn(mock(org.bukkit.Server.class));
        // Mock online players - using a simple approach
        lenient().when(plugin.getServer().getOnlinePlayers()).thenReturn(Collections.emptyList());
        lenient().when(player.getName()).thenReturn("TestPlayer");
        
        // Mock Bukkit.getOnlinePlayers()
        try (MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class)) {
            bukkitMock.when(Bukkit::getOnlinePlayers).thenReturn(Arrays.asList(player));
            
            // Test second argument completion for test command
            String[] args = {"test", "T"};
            List<String> completions = welcomeCommand.onTabComplete(sender, command, "welcome", args);
            
            assertNotNull(completions);
            assertTrue(completions.contains("TestPlayer"));
        }
    }

    @Test
    @DisplayName("Test edge cases")
    public void testEdgeCases() {
        // Test with empty arguments (null args should be handled gracefully)
        String[] emptyArgs = {};
        boolean result = welcomeCommand.onCommand(sender, command, "welcome", emptyArgs);
        assertTrue(result);
        
        // Test with null arguments - should not throw exception
        assertDoesNotThrow(() -> {
            welcomeCommand.onCommand(sender, command, "welcome", null);
        });
        
        // Test tab complete with null arguments
        assertDoesNotThrow(() -> {
            welcomeCommand.onTabComplete(sender, command, "welcome", null);
        });
    }
}
