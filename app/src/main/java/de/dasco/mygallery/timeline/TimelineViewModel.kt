package de.dasco.mygallery.timeline

import android.app.Application
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.*
import de.dasco.mygallery.models.MediaItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TimelineRepository()

    /*
        private val _images = MutableLiveData<List<MediaItem>>()
        val images: LiveData<List<MediaItem>>
            get() = _images
    */
//    val images = repository.getImnages(application)
    val images = repository.getImageList()


    init {
        viewModelScope.launch {
            repository.getAllImages(application)
        }
        application.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            object : ContentObserver(
                Handler(Looper.getMainLooper())
            ){
                override fun onChange(selfChange: Boolean) {

                    viewModelScope.launch {
                        repository.getAllImages(application)
                    }

                }
            })
    }


    /**
     * Factory for constructing BooruViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TimelineViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}