package com.yemektariflerikitabi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import java.io.ByteArrayOutputStream

class DescriptionFragment : Fragment() {
    var secilenGorsel : Uri? = null
    var secilenBitMap : Bitmap? =null
    lateinit var btnKaydet:Button
    lateinit var imageView: ImageView
    lateinit var txtYemekIsmi: EditText
    lateinit var txtYemekDetail:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtYemekIsmi=view.findViewById(R.id.txtYemekIsmi)
        txtYemekDetail=view.findViewById(R.id.txtYemekDetail)
        btnKaydet=view.findViewById(R.id.btnKaydet)
        btnKaydet.setOnClickListener {
            kaydet(it)
        }
        imageView=view.findViewById(R.id.imageView)
        imageView.setOnClickListener {
            gorselSec(it)
        }

        arguments?.let {
            var gelenBilgi = DescriptionFragmentArgs.fromBundle(it).info

            if (gelenBilgi.equals("menudengeldim")){
                //yeni bir ürün eklemeye geldi
                txtYemekIsmi.setText("")
                txtYemekDetail.setText("")
                btnKaydet.visibility= View.VISIBLE

                val gorselSecmeArkaPlani = BitmapFactory.decodeResource(context?.resources,R.drawable.gorselsecimi)
                imageView.setImageBitmap(gorselSecmeArkaPlani) // fragmentdetail in aynı görünümünü elde ettik.
            }else{
                //daha önce oluşturulan yemeği görmeye geldi.
                btnKaydet.visibility = View.INVISIBLE

                val secilenId = DescriptionFragmentArgs.fromBundle(it).id

                context?.let {
                    try {
                        val db = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM yemekler WHERE id =?", arrayOf(secilenId.toString())) //sadece seçtiğimiz yemeğin id si!alınır

                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex = cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorseli = cursor.getColumnIndex("gorsel")

                        while (cursor.moveToNext()){
                            txtYemekIsmi.setText(cursor.getString(yemekIsmiIndex))
                            txtYemekDetail.setText(cursor.getString(yemekIsmiIndex))

                            val byteDizisi = cursor.getBlob(yemekGorseli)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun kaydet(view: View){
        Log.d("Test1","Kaydet e tıklandı")//SQLite Kaydetme

        val yemekIsmi = txtYemekIsmi.text.toString()
        val yemekMalzemeleri = txtYemekDetail.toString()

        if (secilenBitMap!=null){
            val kucukBitmap = kucukBitmapOlustur(secilenBitMap!!,300)

            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler(id INTEGER PRIMARY KEY, yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)")

                    val sqlString = "INSERT INTO yemekler(yemekismi,yemekmalzemesi,gorsel) VALUES(?,?,?)"
                    val statement = database.compileStatement(sqlString) // string sql gibi çalıştırır ve binding yapar!!
                    statement.bindString(1,yemekIsmi)
                    statement.bindString(2,yemekMalzemeleri)
                    statement.bindBlob(3,byteDizisi)

                    statement.execute() // çalıştırır

                }
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }

            val action = DescriptionFragmentDirections.actionDescriptionFragmentToListFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun gorselSec(view:View){
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it.applicationContext, // android versiyonlar arası uyumsuzluğu engellemek için applicationContext kullandık!
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {//izin verilmedi,izin istememiz gerekiyor.
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                //izin zaten verilmiş,tekrar istemeden galeriye git
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2) // galeriye göütren kod!
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==1){
            if (grantResults.size >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                // izin aldık!
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }

            }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode==2 && resultCode == Activity.RESULT_OK && data != null){

            secilenGorsel=data.data

            try {
                context?.let {
                    if (secilenGorsel != null) {
                        if (Build.VERSION.SDK_INT>=28) {
                            val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitMap=ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitMap)
                        }else{
                            secilenBitMap=MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageView.setImageBitmap(secilenBitMap)
                        }
                    }

                }
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }
        }



        super.onActivityResult(requestCode, resultCode, data)
    }

    fun kucukBitmapOlustur(kullanicininSectigiBitmap:Bitmap,maximumBoyut:Int):Bitmap{
        var width =kullanicininSectigiBitmap.width
        var height = kullanicininSectigiBitmap.height

        val bitmapOrani:Double = width.toDouble() /height.toDouble() // bu oran sabit kalır ve tekrar yakalamak hedeftir.
        //yatay ya da dikey gorsel pozisyonuna gore oransal küçültmek için kontrol sağlarım.-> width/height ile
        if (bitmapOrani>1){
            //gorsel yatay demektir!
            width=maximumBoyut
            val kisaltilmisHeight = width / bitmapOrani
            height= kisaltilmisHeight.toInt()
        }else{
            //gorsel dikey demektir!
            height = maximumBoyut
            val kisaltimisWidth = height * bitmapOrani
            width = kisaltimisWidth.toInt()
        }
        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width/2,height/2,true)
    }





}