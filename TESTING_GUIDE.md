# WelcomeMessages - Testing Guide

## Table of Contents
- [Unit Testing](#unit-testing)
- [Integration Testing](#integration-testing)
- [Performance Testing](#performance-testing)
- [Manual Testing](#manual-testing)
- [Test Scenarios](#test-scenarios)
- [Troubleshooting](#troubleshooting)

## Unit Testing

### Running Unit Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests SecurityUtilsTest

# Run tests with verbose output
./gradlew test --info

# Generate test report
./gradlew test
# Report will be in build/reports/tests/test/index.html
```

### Test Coverage
The plugin includes comprehensive unit tests for:
- **SecurityUtils**: Input validation and sanitization
- **MessageUtils**: Message processing and formatting
- **DataManager**: Player data management
- **SmartRecognitionManager**: Milestone detection and smart features
- **WelcomeCommand**: Command handling and validation

### Test Structure
```
src/test/java/com/FiveDollaGobby/WelcomeMessages/
├── utils/
│   ├── SecurityUtilsTest.java
│   └── MessageUtilsTest.java
├── managers/
│   ├── DataManagerTest.java
│   └── SmartRecognitionManagerTest.java
└── commands/
    └── WelcomeCommandTest.java
```

## Integration Testing

### Test Server Setup
1. Create a test server with Paper/Spigot
2. Install the plugin JAR
3. Configure test scenarios
4. Run integration tests

### Test Commands
```bash
# Basic functionality
/welcome help
/welcome version
/welcome reload

# Testing commands
/welcome test
/welcome testall
/welcome testanim typing
/welcome testtheme halloween
/welcome testmilestone
/welcome testplaytime

# Performance monitoring
/welcome performance
/welcome metrics

# Player management
/welcome stats
/welcome toggle
/welcome reset PlayerName
```

### Test Scenarios

#### 1. Basic Message Testing
```bash
# Test join messages
/welcome test

# Test with specific player
/welcome test PlayerName

# Test all features
/welcome testall
```

#### 2. Animation Testing
```bash
# Test different animation types
/welcome testanim typing
/welcome testanim rainbow
/welcome testanim epic_welcome
/welcome testanim mysterious_join
/welcome testanim party_time
```

#### 3. Theme Testing
```bash
# Test different themes
/welcome testtheme default
/welcome testtheme halloween
/welcome testtheme christmas
/welcome testtheme morning
/welcome testtheme evening
```

#### 4. Milestone Testing
```bash
# Test milestone detection
/welcome testmilestone

# Test playtime tracking
/welcome testplaytime
```

#### 5. Performance Testing
```bash
# View performance summary
/welcome performance

# View detailed metrics
/welcome metrics
```

## Performance Testing

### Load Testing
1. **High Player Count**: Test with 100+ players joining/leaving
2. **Rapid Joins**: Multiple players joining simultaneously
3. **Long Sessions**: Extended playtime tracking
4. **Memory Usage**: Monitor memory consumption over time

### Performance Metrics
- **Message Processing Time**: Should be < 50ms
- **Animation Time**: Should be < 100ms
- **Effect Time**: Should be < 200ms
- **Milestone Time**: Should be < 30ms
- **Theme Time**: Should be < 20ms
- **Placeholder Time**: Should be < 10ms
- **Memory Usage**: Should be < 80%

### Performance Commands
```bash
# View current performance
/welcome performance

# View detailed metrics
/welcome metrics

# Monitor in real-time
# Check console logs for performance reports
```

## Manual Testing

### Test Checklist

#### Basic Functionality
- [ ] Plugin loads without errors
- [ ] Commands work correctly
- [ ] Messages display properly
- [ ] Animations work
- [ ] Effects trigger
- [ ] Themes change
- [ ] Milestones detect
- [ ] Placeholders resolve

#### Permission Testing
- [ ] Admin commands require proper permissions
- [ ] Player commands work with correct permissions
- [ ] Permission denied messages display
- [ ] Rank-based messages work

#### Configuration Testing
- [ ] Config reload works
- [ ] Invalid config shows errors
- [ ] Default values load correctly
- [ ] Custom settings apply

#### Edge Cases
- [ ] Null player handling
- [ ] Invalid input handling
- [ ] Network disconnections
- [ ] Server restarts
- [ ] Plugin reloads

### Test Data Setup

#### Test Players
```yaml
# Create test players with different scenarios
test-players:
  - name: "NewPlayer"
    first-join: true
    join-count: 1
    playtime: 0
  
  - name: "RegularPlayer"
    first-join: false
    join-count: 50
    playtime: 100
  
  - name: "VIPPlayer"
    first-join: false
    join-count: 200
    playtime: 500
    rank: "vip"
  
  - name: "AdminPlayer"
    first-join: false
    join-count: 1000
    playtime: 2000
    rank: "admin"
```

#### Test Messages
```yaml
# Test different message types
test-messages:
  join:
    - "&aWelcome {player}!"
    - "&6&lWelcome &e{player} &6&lto the server!"
    - "<gradient:#FF0000:#00FF00>Welcome {player}!</gradient>"
    - "<rainbow>Welcome {player}!</rainbow>"
  
  quit:
    - "&cGoodbye {player}!"
    - "&7{player} &cleft the server"
  
  milestones:
    - "&6&lMilestone! &e{player} &7has joined &e{count} &7times!"
    - "&b&lPlaytime! &e{player} &7has played for &e{hours} &7hours!"
```

## Test Scenarios

### Scenario 1: New Player Join
**Objective**: Test first-time player experience
**Steps**:
1. Create new player
2. Join server
3. Verify first-join message
4. Check milestone detection
5. Verify effects trigger

**Expected Results**:
- First-join message displays
- Milestone detected
- Effects trigger
- Player data created

### Scenario 2: Returning Player
**Objective**: Test returning player experience
**Steps**:
1. Use existing player
2. Join server
3. Verify returning message
4. Check milestone detection
5. Verify effects trigger

**Expected Results**:
- Returning message displays
- Milestone detected if applicable
- Effects trigger
- Player data updated

### Scenario 3: Milestone Achievement
**Objective**: Test milestone detection
**Steps**:
1. Set player join count to milestone value
2. Join server
3. Verify milestone message
4. Check milestone tracking

**Expected Results**:
- Milestone message displays
- Milestone marked as reached
- No duplicate notifications

### Scenario 4: Theme Change
**Objective**: Test theme system
**Steps**:
1. Set current theme
2. Join server
3. Verify theme message
4. Change theme
5. Join again

**Expected Results**:
- Theme message displays
- Theme changes apply
- No conflicts

### Scenario 5: Animation System
**Objective**: Test animation functionality
**Steps**:
1. Enable animations
2. Join server
3. Verify animation plays
4. Test different animation types

**Expected Results**:
- Animation plays correctly
- Different types work
- No performance issues

### Scenario 6: Performance Monitoring
**Objective**: Test performance tracking
**Steps**:
1. Enable performance monitoring
2. Perform various actions
3. Check performance metrics
4. Verify thresholds

**Expected Results**:
- Metrics tracked correctly
- Thresholds work
- Reports generated

## Troubleshooting

### Common Issues

#### Tests Failing
```bash
# Check test output
./gradlew test --info

# Check specific test
./gradlew test --tests SecurityUtilsTest --info

# Clean and rebuild
./gradlew clean test
```

#### Performance Issues
```bash
# Check performance metrics
/welcome performance

# Check detailed metrics
/welcome metrics

# Check console logs
# Look for performance warnings
```

#### Configuration Issues
```bash
# Check config validation
/welcome reload

# Check console logs
# Look for configuration errors
```

#### Memory Issues
```bash
# Check memory usage
/welcome metrics

# Check console logs
# Look for memory warnings
```

### Debug Mode
```yaml
# Enable debug mode in config.yml
general:
  debug: true
```

### Log Analysis
```bash
# Check plugin logs
tail -f logs/latest.log | grep WelcomeMessages

# Check for errors
grep "ERROR" logs/latest.log | grep WelcomeMessages

# Check for warnings
grep "WARN" logs/latest.log | grep WelcomeMessages
```

### Performance Analysis
```bash
# Monitor performance in real-time
watch -n 1 "grep 'Performance Report' logs/latest.log | tail -1"

# Check memory usage
jstat -gc <server-pid>

# Check CPU usage
top -p <server-pid>
```

## Test Automation

### Automated Test Script
```bash
#!/bin/bash
# test-welcome-messages.sh

echo "Starting WelcomeMessages tests..."

# Run unit tests
echo "Running unit tests..."
./gradlew test

# Check test results
if [ $? -eq 0 ]; then
    echo "Unit tests passed!"
else
    echo "Unit tests failed!"
    exit 1
fi

# Run integration tests
echo "Running integration tests..."
# Add integration test commands here

echo "All tests completed!"
```

### Continuous Integration
```yaml
# .github/workflows/test.yml
name: Test WelcomeMessages

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 21
      uses: actions/setup-java@v2
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Upload test results
      uses: actions/upload-artifact@v2
      if: always()
      with:
        name: test-results
        path: build/reports/tests/
```

This comprehensive testing guide ensures that the WelcomeMessages plugin is thoroughly tested and performs reliably in all scenarios.
