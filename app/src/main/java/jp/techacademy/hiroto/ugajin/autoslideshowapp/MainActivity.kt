package jp.techacademy.hiroto.ugajin.autoslideshowapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import jp.techacademy.hiroto.ugajin.autoslideshowapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener  {

    private lateinit var binding: ActivityMainBinding

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
            getContentsInfo()
        } else {
            // 許可されていないので許可ダイアログを表示する
            requestPermissions(
                arrayOf(readImagesPermission),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onClick(v: View) {
        getNextInfo()
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
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            binding.imageView.setImageURI(imageUri)
        }

//        if (cursor!!.moveToFirst()) {
//            do {
//                // indexからIDを取得し、そのIDから画像のURIを取得する
//                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
//                val id = cursor.getLong(fieldIndex)
//                val imageUri =
//                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//
//                Log.d("UI_PARTS", "URI : $imageUri")
//            } while (cursor.moveToNext())
//        }

        cursor.close()
    }

    private fun getNextInfo() {

        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if (cursor != null && (cursor.moveToFirst() || cursor.moveToNext())) {
            if (cursor != null && cursor.moveToNext()) {

                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Log.d("UI_PARTS", "URI : $imageUri")
                binding.imageView.setImageURI(imageUri)

            } else {
                if (cursor!!.moveToFirst()) {
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    binding.imageView.setImageURI(imageUri)
                }
            }
            cursor.close()
        }
    }
}