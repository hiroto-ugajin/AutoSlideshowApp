package jp.techacademy.hiroto.ugajin.autoslideshowapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import jp.techacademy.hiroto.ugajin.autoslideshowapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cursor: Cursor

//    private var currentPosition: Int = -1

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

        binding.button1.setOnClickListener(this)

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
            if (
    //            cursor != null &&
                cursor.moveToPosition(currentPosition)
//                cursor.moveToNext()
                != null
            )
            {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                binding.imageView.setImageURI(imageUri)
                currentPosition ++
//                = cursor.position
            } else {

                // カーソルの最後に達した場合、最初に戻る
                if (cursor.moveToPosition(0) != null) {
                    cursor.moveToPosition(0)
                }
                currentPosition = 1
            }
        }

        if (cursor != null) {
            cursor.close()
        }
    }

    override fun onClick(v: View) {
        displayNextImage()
        Log.d("UI_PARTS", "進むボタンがタップされました。")
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









