package de.dasco.mygallery.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.ExifInterfaceImageHeaderParser
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class GlideAppModule : AppGlideModule(){
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)

        //Hack to fix Glide outputting tons of log spam with ExifInterface errors
        glide.registry.imageHeaderParsers.removeAll { it is ExifInterfaceImageHeaderParser }
    }


}// Intentionally empty.