# Room Cache Cleared and Destructive Migration Enabled

I have cleared the local Room database cache for the desktop application and updated the configuration to handle future schema changes automatically.

## Changes Made

### 1. Database Cleanup
- Successfully deleted the following files from your home directory:
    - `~/materials.db`
    - `~/materials.db-shm`
    - `~/materials.db-wal`
    - `~/materials.db.lck`

### 2. Configuration Update
#### [DatabaseBuilder.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/data/local/DatabaseBuilder.kt)
Enabled destructive migration in the Room database builder.

```kotlin
fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(true) // Added this line
        .build()
}
```

## Verification Results

- **Cleanup:** Verified that `ls ~/materials.db*` returns no files.
- **Auto-management:** The `fallbackToDestructiveMigration(true)` ensures that if you change your entities in the future, Room will simply recreate the database instead of crashing due to missing migrations.

> [!TIP]
> Since the database was deleted, the application will perform a full initial sync from Supabase on the next launch.
