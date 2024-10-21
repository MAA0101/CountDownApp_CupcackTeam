package com.example.countdownapp_cupcackteam

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import kotlinx.coroutines.launch

class Repository(private val eventDao: DAO) {

    fun getAllEvents(): LiveData<List<Event>> = eventDao.getAllEvents()

    suspend fun insert(event: Event) = eventDao.insert(event)

    suspend fun getEvent(eventId: Int): Event? = eventDao.getEvent(eventId)
    suspend fun getTitle(eventTitle:String):Event?=eventDao.getTitle(eventTitle)

    suspend fun deletEvent(event:Event){
        eventDao.deletEvent(event)}
}

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository
    val allEvents: LiveData<List<Event>>
    private val appContext: Context = application

    init {
        val eventDao = DataBase.getDatabase(application).eventDao()
        repository = Repository(eventDao)
        allEvents = repository.getAllEvents()
    }

    fun insert(event: Event) = viewModelScope.launch {
        repository.insert(event)

    }
    fun delete(event: Event,context: Context)  {
        /*val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            event.id,
            Intent(appContext, NotificationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()*/
        WorkManager.getInstance(appContext).cancelUniqueWork(event.title)


        // Show a toast message to confirm the deletion
        Toast.makeText(context, "Event deleted!", Toast.LENGTH_SHORT).show()

        //val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //notificationManager.cancel(event.id)



        viewModelScope.launch {
            repository.deletEvent(event)
        }
    }
    fun getEvent(eventId: Int) = viewModelScope.launch {
        repository.getEvent(eventId)

    }
    fun getTitle(eventTitle:String) = viewModelScope.launch {
        repository.getTitle(eventTitle)

    }

}
