# Clear Desktop Room Cache and Enable Destructive Migration

The user wants to clear the Room database cache for the Desktop (JVM) application to avoid migration issues and start with a fresh state.

## Proposed Changes

### 1. Manual Cleanup (Immediate Action)
- Delete the database files in the home directory: `~/materials.db`, `~/materials.db-shm`, `~/materials.db-wal`, and `~/materials.db.lck`.

### 2. Persistence & Automatic Management
#### [MODIFY] [DatabaseBuilder.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/data/local/DatabaseBuilder.kt)
- Add `.fallbackToDestructiveMigration(true)` to the Room database configuration. This will cause Room to automatically recreate the database if a schema version mismatch is detected and no migration path is found, preventing the need for manual file deletion in the future during development.

## Verification Plan

### Manual Verification
- After applying changes and deleting the files, I will confirm the files are gone.
- The user can then run the `desktopApp` to verify it starts correctly with an empty database and recreates the files as needed.
