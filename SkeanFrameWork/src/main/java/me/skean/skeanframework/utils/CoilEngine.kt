package me.skean.skeanframework.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.load
import com.zhihu.matisse.engine.ImageEngine

/**
 * Created by Skean on 2022/4/22.
 */
class CoilEngine : ImageEngine {
    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable?, imageView: ImageView?, uri: Uri?) {
        imageView?.let {
            it.load(uri, imageLoader(context)) {
                placeholder(placeholder)
                size(resize)
            }
        }
    }

    override fun loadGifThumbnail(context: Context, resize: Int, placeholder: Drawable?, imageView: ImageView?, uri: Uri?) {
        imageView?.let {
            it.load(uri, imageLoader(context)) {
                placeholder(placeholder)
                size(resize)
            }
        }
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView?, uri: Uri?) {
        imageView?.let {
            it.load(uri, imageLoader(context)) {
                size(resizeX, resizeY)
            }
        }
    }

    override fun loadGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView?, uri: Uri?) {
        imageView?.let {
            it.load(uri, imageLoader(context)) {
                size(resizeX, resizeY)
            }
        }
    }

    override fun supportAnimatedGif(): Boolean = true

    private fun imageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(VideoFrameDecoder.Factory())
            }.build()
    }
}