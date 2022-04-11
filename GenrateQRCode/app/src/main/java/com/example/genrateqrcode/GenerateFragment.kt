package com.example.genrateqrcode

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.print.PrintHelper
import com.example.genrateqrcode.databinding.FragmentGenrateBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class GenerateFragment : Fragment() {
    private var _binding: FragmentGenrateBinding?=null
    private val binding get() = _binding!!
    private val TAG="GenerateFragment"
    private val args:GenerateFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentGenrateBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qrcode=args.id
        val bitmap=generateCode(qrcode)
        binding.qrcode.setImageBitmap(bitmap)
        binding.download.setOnClickListener {
            printQR(bitmap)
        }
    }
    private fun generateCode(data:String):Bitmap{
        val width=300
        val height=300
        val bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val codeWriter= MultiFormatWriter()
        try {
            val bitMatrix =
                codeWriter.encode(data, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }catch (e: WriterException){
            Log.e(TAG,"generateQRCode: ${e.message}")
        }
        return bitmap
    }
    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context?.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(requireContext(),"Qr Code saved to gallery",Toast.LENGTH_SHORT).show()
        }
    }
    private fun printQR(bitmap: Bitmap){
        requireActivity().also { context->
            PrintHelper(context).apply {
                scaleMode=PrintHelper.SCALE_MODE_FIT
            }.also { printHelper ->
                printHelper.printBitmap("QRCode",bitmap)
            }
        }
    }


}