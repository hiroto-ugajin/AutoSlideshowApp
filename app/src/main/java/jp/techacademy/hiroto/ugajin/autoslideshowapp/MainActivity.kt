package jp.techacademy.hiroto.ugajin.autoslideshowapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import jp.techacademy.hiroto.ugajin.autoslideshowapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cursor: Cursor

    var num: Int = 0

    private var isTimerRunning = false
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private val PERMISSIONS_REQUEST_CODE = 100

    // APIレベルによって許可が必要なパーミッションを切り替える
    private val readImagesPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
        else android.Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        binding.buttonGo.setOnClickListener (this)
        binding.buttonGo.setOnClickListener(buttonGoClickListener)
        binding.buttonBack.setOnClickListener(buttonBackClickListener)
        binding.buttonSlideShow.setOnClickListener(buttonSlideShowClickListener)

        // パーミッションの許可状態を確認する
        if (checkSelfPermission(readImagesPermission) == PackageManager.PERMISSION_GRANTED) {
            // 許可されている

            displayNextImage()

        } else {
            // 許可されていないので許可ダイアログを表示する
            requestPermissions(
                arrayOf(readImagesPermission),
                PERMISSIONS_REQUEST_CODE
            )
        }

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                // ここに定期的に実行したい処理を記述する
                // 例: Log.d("Timer", "Timer is running")
                displayNextImage()

                if (isTimerRunning) {
                    handler.postDelayed(this, 2000) // 1秒ごとに実行する場合
                }
            }
        }
    }

    private var currentPosition: Int = 0

    private fun displayNextImage() {

        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if (cursor != null) {
            this.num = cursor.count
        }

        if (cursor != null && currentPosition < this.num) {
            if (cursor.moveToPosition(currentPosition) != null) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                binding.imageView.setImageURI(imageUri)

                if (currentPosition < this.num - 1) { currentPosition ++
                } else if (currentPosition == this.num - 1) { currentPosition = 0
                }
           }
        }

        if (cursor != null) {
            cursor.close()
        }
    }

    private fun displayPreviousImage() {

        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if (cursor != null) {
            this.num = cursor.count
        }

        if (cursor != null && currentPosition > 0) {
            if (cursor.moveToPosition(currentPosition - 1) != null) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                binding.imageView.setImageURI(imageUri)
                currentPosition --}

                } else if (currentPosition == 0) {
            if (cursor != null) {
                if (cursor.moveToPosition(0) != null) {
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    binding.imageView.setImageURI(imageUri)
                    currentPosition = num
                }
            }
            }

        if (cursor != null) {
            cursor.close()
        }
    }

    override fun onClick(v: View) {
        displayNextImage()
    }

    private val buttonGoClickListener = View.OnClickListener {
        displayNextImage()
    }

    private val buttonBackClickListener = View.OnClickListener {
        displayPreviousImage()
    }

    private val buttonSlideShowClickListener = View.OnClickListener {
        if (isTimerRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        isTimerRunning = true
        handler.post(runnable)
    }

    private fun stopTimer() {
        isTimerRunning = false
        handler.removeCallbacks(runnable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayNextImage()

                }
        }
    }
}









