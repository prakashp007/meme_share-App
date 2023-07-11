    package com.example.memeshare

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.memeshare.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private  lateinit var binding : ActivityMainBinding
    private lateinit var imgUrl : String
    private val currnetTime = Calendar.getInstance().timeInMillis
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
         loadMeme()
        binding.btnNext.setOnClickListener {
            loadMeme()
        }
        binding.btnShare.setOnClickListener {
            val bitmapDrawables = binding.imvMemeImages.drawable as BitmapDrawable
            val bitmap = bitmapDrawables.bitmap
            shareMeme(bitmap)
        }
    }

    private fun loadMeme() {
        binding.pbLoadMeme.visibility = View.VISIBLE
        //instantiate  the  request queue
        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.com/gimme"

        // request a string from the provided url
        val stringRequest =
            JsonObjectRequest(Request.Method.GET, url, null, {response ->
                imgUrl  = response.getString("url")
                Glide.with(this).load(imgUrl).listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbLoadMeme.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbLoadMeme.visibility = View.GONE
                        return false
                    }
                })
                    .into(binding.imvMemeImages)
            }, {

            })

        //add the request to the RequestQueue
        queue.add(stringRequest)
    }
    private fun shareMeme(bitmap : Bitmap){
        val uri = getImageToShare(bitmap)
        val intent  = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM,uri)
            putExtra(Intent.EXTRA_TEXT,"Sharing Image")
            putExtra(Intent.EXTRA_SUBJECT,"Subject Here")
            type = "image/png"
        }
        startActivity(Intent.createChooser(intent,"Share via"))
    }

    private fun getImageToShare(bitmap: Bitmap) : Uri? {
        val  imageFolder = File(cacheDir,"images")
        var uri : Uri? = null
        try {
            imageFolder.mkdirs()
            val file = File(imageFolder,"$currnetTime.png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG,90,fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            uri = FileProvider.getUriForFile(this,"com.example.memeshare",file)
        }catch (e : Exception){
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }
        return uri
    }

}
