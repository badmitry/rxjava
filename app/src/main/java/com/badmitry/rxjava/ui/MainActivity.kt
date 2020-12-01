package com.badmitry.rxjava.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.badmitry.rxjava.R
import com.badmitry.rxjava.databinding.ActivityMainBinding
import com.badmitry.rxjava.mvp.presenter.MainPresenter
import com.badmitry.rxjava.mvp.view.IMainView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter


class MainActivity : MvpAppCompatActivity(), IMainView {

    private val PICKFILE_RESULT_CODE = 1100
    private val PERMISSIONS_RQUEST_CODE = 1234
    private var binding: ActivityMainBinding? = null
    private var mainScheduler = AndroidSchedulers.mainThread()
    private val presenter by moxyPresenter { MainPresenter(mainScheduler, AndroidFileJpgChecker()) }
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        checkPermissions()
        initView()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            presenter.requestPerm()
        }
    }

    override fun requestPerm() {
        val listPermissionsNeeded = arrayListOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(
            this, listPermissionsNeeded.toTypedArray(),
            PERMISSIONS_RQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_RQUEST_CODE) {
            presenter.checkPermission(grantResults, PackageManager.PERMISSION_DENIED)
        }
    }

    private fun initView() {
        binding?.btnSelectFile?.setOnClickListener {
            var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.setType("*/*")
            chooseFile = Intent.createChooser(chooseFile, "Choose a file")
            startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)
        }
        binding?.btnConvert?.setOnClickListener {
            val bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri)
            bmp?.let{
                val androidConverter = AndroidConverter(bmp)
                presenter.convertImg(androidConverter)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICKFILE_RESULT_CODE -> {
                fileUri = data?.data
                presenter.checkFile(resultCode, fileUri?.path)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showConvertBtn(b: Boolean) {
        if (b) {
            binding?.btnConvert?.visibility = View.VISIBLE
        } else {
            binding?.btnConvert?.visibility = View.INVISIBLE
        }
    }

    override fun setText(text: String) {
        binding?.tvFileUri?.text = text
    }

    override fun setProgressBar(i: Int) {
        binding?.progressBar?.progress = i
    }

    override fun showProgressBar(b: Boolean) {
        if (b) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}