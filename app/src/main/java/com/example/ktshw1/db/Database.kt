import android.content.Context
import androidx.room.Room
import com.example.ktshw1.db.ApplicationDatabase

object Database {

    lateinit var instance: ApplicationDatabase
        private set

    fun init(context: Context) {
        instance = Room.databaseBuilder(
            context,
            ApplicationDatabase::class.java,
            ApplicationDatabase.DB_NAME
        )
            .build()
    }
}
