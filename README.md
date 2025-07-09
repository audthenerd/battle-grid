# BattleGrid 🎯

A tactical Android application for military operations, featuring 9-liner request management, interactive mapping, user management, and cross-device database synchronization.

## Features 🚀

### 📋 9-Liner Request System
- **CAS (Close Air Support)** requests
- **MEDEVAC** requests  
- **Fire Support** requests
- Draft saving and transmission tracking
- Complete request history and status management

### 🗺️ Interactive Mapping
- **Google Maps integration** with real-time interaction
- **Polygon drawing** for area marking and tactical zones
- **Persistent map data** saved to local database
- Click-to-delete polygon functionality

### 👥 User Management
- User creation with validation
- Experience points and leveling system
- User activation/deactivation
- Unique username constraints

### 🔄 Cross-Device Synchronization
- **Automated database sync** across multiple Android devices
- **Master database** aggregation system
- **Backup and restore** functionality
- **Periodic sync** with configurable intervals

### 📱 Battle Sessions
- Session-based tactical operations
- Grid position tracking for entities (players, NPCs, objects)
- Health and status management
- Session completion tracking

## Tech Stack 🛠️

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Database**: Room (SQLite)
- **Maps**: Google Maps API for Android
- **Architecture**: MVVM with Repository pattern
- **Navigation**: Navigation Compose
- **Async**: Kotlin Coroutines & Flow
- **Build**: Gradle with Version Catalogs

## Prerequisites 📋

- **Android Studio** (latest stable version)
- **Android SDK** (API level 26+)
- **Google Maps API Key**
- **Python 3** (for database sync tools)
- **ADB** (Android Debug Bridge)

## Setup Instructions ⚙️

### 1. Clone the Repository
```bash
git clone <repository-url>
cd BattleGrid
```

### 2. Configure Google Maps API
Create a `local.properties` file in the root directory:
```properties
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
```

**To get a Google Maps API key:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create or select a project
3. Enable the Maps SDK for Android
4. Create credentials (API key)
5. Add the key to your `local.properties` file

### 3. Build and Run
```bash
./gradlew build
```

Open in Android Studio and run on device/emulator.

## Database Schema 📊

The app uses Room database with the following entities:

- **Users**: User accounts with experience and levels
- **Nine Liners**: Military communication requests
- **Battle Sessions**: Tactical operation sessions
- **Grid Positions**: Entity positions within sessions
- **Polygons**: Map polygon data for tactical zones

## Database Synchronization 🔄

### Automated Sync System

The project includes a complete database synchronization system for multi-device deployments:

#### Setup Periodic Sync
```bash
# Make scripts executable and setup cron job
chmod +x setup_periodic_sync.sh
./setup_periodic_sync.sh
```

#### Manual Sync
```bash
# Sync databases from all connected devices
./sync_databases.sh

# Merge all device databases into master database
python3 merge_databases.py

# View aggregated data
python3 view_data.py --summary
```

#### Sync Features
- **Automatic detection** of connected Android devices
- **Conflict resolution** for duplicate data
- **Device source tracking** for data lineage
- **Backup system** with timestamped snapshots
- **Configurable sync intervals** (default: 30 minutes)

### Database Tools

```bash
# View summary statistics
python3 view_data.py --summary

# Show all 9-liner requests
python3 view_data.py --requests

# Show users from specific device
python3 view_data.py --users --device emulator-5554

# Export data to JSON
python3 view_data.py --export
```

## Architecture Overview 🏗️

```
app/
├── database/
│   ├── entities/          # Room entities
│   ├── dao/              # Data Access Objects  
│   └── BattleGridDatabase.kt
├── repository/           # Repository pattern
├── screens/             # Compose UI screens
├── navigation/          # Navigation setup
└── ui/theme/           # Material Design theme

Scripts/
├── sync_databases.sh    # Device sync script
├── merge_databases.py   # Database merger
├── auto_sync.sh        # Automated sync
├── setup_periodic_sync.sh # Cron setup
└── view_data.py        # Data viewer
```

## Key Components 🔧

### Navigation
- **Bottom Navigation** with 3 main screens
- **Home**: Interactive map view
- **Controls**: 9-liner request management  
- **User**: User account management

### Database Layer
- **Room Database** with foreign key constraints
- **Repository Pattern** for data abstraction
- **Flow-based** reactive data streams
- **Automatic migrations** for schema updates

### Sync System
- **Shell scripts** for Android device interaction
- **Python tools** for data processing and merging
- **SQLite operations** for database manipulation
- **Cron integration** for automated scheduling

## Development Guidelines 📝

### Adding New Features
1. Create entity in `database/entities/`
2. Add DAO in `database/dao/`
3. Update database version in `BattleGridDatabase.kt`
4. Create repository in `repository/`
5. Build UI in `screens/`

### Database Migrations
- Increment version number in `@Database` annotation
- Room handles migrations automatically with `fallbackToDestructiveMigration()`
- For production, implement proper migration strategies

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Troubleshooting 🔧

### Common Issues

**Maps not loading:**
- Verify Google Maps API key is correct
- Check if Maps SDK for Android is enabled
- Ensure internet connectivity

**Database sync failing:**
- Verify ADB is installed and in PATH
- Check device connection with `adb devices`
- Ensure app is installed on target devices

**Build errors:**
- Clean and rebuild: `./gradlew clean build`
- Invalidate caches in Android Studio
- Check Gradle and dependency versions

### Logs and Debugging

```bash
# View app logs
adb logcat -s BattleGrid

# View sync logs
tail -f logs/sync.log

# Check database content
sqlite3 master_battlegrid.db ".tables"
```

## Contributing 🤝

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## Security Considerations 🔒

- **API Keys**: Never commit API keys to version control
- **Database**: Contains tactical data - ensure proper device security
- **Network**: Consider VPN for sync operations in sensitive environments
- **Permissions**: App requires location and network permissions

## License 📄

[Add your license information here]

## Support 📞

For technical support or questions:
- Create an issue in the repository
- Check existing documentation
- Review troubleshooting section

---

**Built for tactical operations with ❤️ and ⚡** 