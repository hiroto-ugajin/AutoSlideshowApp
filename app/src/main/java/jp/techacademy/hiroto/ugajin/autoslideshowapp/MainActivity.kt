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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import jp.techacademy.hiroto.ugajin.autoslideshowapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cursor: Cursor

    var num: Int = 0

    private var isTimerRunning = false
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("ANDROID", "許可された")
                displayNextImage()
            } else {
                Log.d("ANDROID", "許可されなかった")
                Toast.makeText(this, "パーミッションが拒否されました", Toast.LENGTH_SHORT).show()
            }
        }

    // APIレベルによって許可が必要なパーミッションを切り替える
    private val readImagesPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
        else android.Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonPermission.setOnClickListener {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(readImagesPermission) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                displayNextImage()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissionLauncher.launch(readImagesPermission)
            }
        }

        binding.buttonGo.setOnClickListener(buttonGoClickListener)
        binding.buttonBack.setOnClickListener(buttonBackClickListener)
        binding.buttonSlideShow.setOnClickListener(buttonSlideShowClickListener)

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                // ここに定期的に実行したい処理を記述する
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
        binding.buttonSlideShow.text = "停止"
        binding.buttonGo.isEnabled = false
        binding.buttonBack.isEnabled = false
        handler.post(runnable)
    }

    private fun stopTimer() {
        isTimerRunning = false
        binding.buttonSlideShow.text = "再生"
        binding.buttonGo.isEnabled = true
        binding.buttonBack.isEnabled = true
        handler.removeCallbacks(runnable)
    }
}









