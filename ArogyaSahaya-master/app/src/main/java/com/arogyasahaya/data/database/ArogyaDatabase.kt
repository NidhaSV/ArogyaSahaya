package com.arogyasahaya.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arogyasahaya.data.dao.*
import com.arogyasahaya.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        MedicineEntity::class,
        MedicineDoseLogEntity::class,
        VitalLogEntity::class,
        UserProfileEntity::class,
        AshaEventEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ArogyaDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun medicineDoseLogDao(): MedicineDoseLogDao
    abstract fun vitalLogDao(): VitalLogDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun ashaEventDao(): AshaEventDao

    companion object {
        @Volatile
        private var INSTANCE: ArogyaDatabase? = null

        fun getDatabase(context: Context): ArogyaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArogyaDatabase::class.java,
                    "arogya_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed simulated ASHA events on first creation
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedAshaEvents(database.ashaEventDao())
                }
            }
        }

        private suspend fun seedAshaEvents(dao: AshaEventDao) {
            val events = buildList {
                val cal = Calendar.getInstance()

                // Next 3 months of simulated events
                repeat(12) { weekOffset ->
                    cal.timeInMillis = System.currentTimeMillis()
                    cal.add(Calendar.WEEK_OF_YEAR, weekOffset + 1)

                    when (weekOffset % 4) {
                        0 -> add(
                            AshaEventEntity(
                                title = "Primary Health Camp",
                                description = "Free checkup by PHC doctor. Bring your health card.",
                                eventDate = cal.timeInMillis,
                                location = "Village Panchayat Hall",
                                eventType = AshaEventType.HEALTH_CAMP
                            )
                        )
                        1 -> add(
                            AshaEventEntity(
                                title = "ASHA Worker Visit",
                                description = "Kavitha ASHA worker will visit for wellness check.",
                                eventDate = cal.timeInMillis,
                                location = "Door-to-door visit",
                                eventType = AshaEventType.ASHA_VISIT
                            )
                        )
                        2 -> add(
                            AshaEventEntity(
                                title = "Blood Pressure Screening",
                                description = "Free BP monitoring at the sub-health centre.",
                                eventDate = cal.timeInMillis,
                                location = "Sub Health Centre",
                                eventType = AshaEventType.WELLNESS_CHECK
                            )
                        )
                        3 -> add(
                            AshaEventEntity(
                                title = "Vaccination Drive",
                                description = "Influenza and booster vaccines available.",
                                eventDate = cal.timeInMillis,
                                location = "Primary Health Centre",
                                eventType = AshaEventType.VACCINATION
                            )
                        )
                    }
                }
            }
            dao.insertEvents(events)
        }
    }
}
