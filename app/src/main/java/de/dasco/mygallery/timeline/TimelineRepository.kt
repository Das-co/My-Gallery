package de.dasco.mygallery.timeline

import android.app.Application
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.dasco.mygallery.models.HeaderItem
import de.dasco.mygallery.models.MediaItem
import de.dasco.mygallery.utils.smartDate
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class TimelineRepository() {

    private var job: CompletableJob? = null

/*
    fun getImages(application: Application): LiveData<List<DataItem>> {

        job = Job()
        return object : LiveData<List<DataItem>>() {
            override fun onActive() {
                super.onActive()
                job?.let { singleJob ->
                    CoroutineScope(Dispatchers.IO + singleJob).launch {

                        val imageList = mutableListOf<DataItem>()

                        val projection =
                            arrayOf(
                                MediaStore.Images.Media._ID,
                                MediaStore.Images.Media.DISPLAY_NAME,
                                MediaStore.Images.Media.SIZE,
                                MediaStore.Images.Media.DATE_MODIFIED
                            )

                        val selection: String? =
                            "${MediaStore.Images.Media.SIZE} >= ?"     //Selection criteria
                        val selectionArgs = arrayOf<String>("20000")  //Selection criteria
                        val sortOrder: String? = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"



                        application.contentResolver.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder
                        )?.use { cursor ->
                            // Cache column indices.
                            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                            println("ID: $idColumn")
                            //            val nameColumn =
                            //                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                            val sizeColumn =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                            val dateColumn =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                            println("SIZE: $sizeColumn")

                            var lastDate = ""
                            while (cursor.moveToNext()) {

                                val id = cursor.getLong(idColumn)
                                val size = cursor.getLong(sizeColumn)
                                val date = cursor.getLong(dateColumn)

                                val contentUri: Uri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                                val item = MediaItem(id, contentUri.toString(), size, date)

                                val formattedDateString = formatter.format(date * 1000)

                                if (lastDate != formattedDateString) {
                                    lastDate = formattedDateString
//                                    println("DATE different: ${formattedDateString}")
                                    imageList.add(
                                        DataItem.Header(
                                            HeaderItem(
                                                date.hashCode().toLong(), date
                                            )
                                        )
                                    )
                                }

//                                println("DATE: ${formattedDateString}")
                                imageList.add(DataItem.ImageItem(item))
*/
/*
                                withContext(Dispatchers.Main) {
                                    value = imageList
                                }
*//*

                            }
//                            println("Array: ${images.value}")
                        }
                        application.contentResolver.registerContentObserver(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            true,
                            object : ContentObserver(
                                Handler(Looper.getMainLooper())
                            ) {
                                override fun onChange(selfChange: Boolean) {
                                    println("Content Changed")
                                    getImages(application)
                                }
                            })

                        withContext(Dispatchers.Main) {
                            value = imageList
                            singleJob.complete()
                        }

                    }
                }
            }
        }
    }
*/


    private var imagesLiveData: MutableLiveData<List<DataItem>> = MutableLiveData()
    fun getImageList(): MutableLiveData<List<DataItem>> {
        return imagesLiveData
    }

    /**
     * Getting All Images Path.
     *
     * Required Storage Permission
     *
     * @return ArrayList with images Path
     */
    private fun loadImagesFromSDCard(application: Application): MutableList<DataItem> {
        val imageList = mutableListOf<DataItem>()

        val projection =
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_MODIFIED
            )

        val selection: String? =
            "${MediaStore.Images.Media.SIZE} >= ?"     //Selection criteria
        val selectionArgs = arrayOf("20000")  //Selection criteria
        val sortOrder: String? = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        application.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            val sizeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            val dateColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            var lastDate = ""

            var header: HeaderItem? = null
//            val headerChildren = ArrayList<Long>()

//            val headerList = ArrayList<HeaderItem>()

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val size = cursor.getLong(sizeColumn)
                val date = cursor.getLong(dateColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val formattedDateString = smartDate(date)

                if (lastDate != formattedDateString) {

                    header = HeaderItem(
                        date.hashCode().toLong(), date, ArrayList()
                    )

                    lastDate = formattedDateString

                    imageList.add(
                        DataItem.Header(
                            header
                        )
                    )
                }
                val item = MediaItem(id, contentUri.toString(), size, date, header!!.id)
                header.children.add(id)

                imageList.add(DataItem.ImageItem(item))
            }

        }

        return imageList
    }

    suspend fun getAllImages(application: Application) {
        withContext(Dispatchers.Main) {
            println("GETALLIMAGES")
//            imagesLiveData.value = getImages(application).value
            imagesLiveData.value = withContext(Dispatchers.IO) {
                loadImagesFromSDCard(application)
            }
        }
    }
}