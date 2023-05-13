package com.yemektariflerikitabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //supportFragmentManager.beginTransaction().replace(R.id.fragmentNavHost,ListFragment()).commit()

        }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.yemek_ekle,menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        if (item.itemId== R.id.yemek_ekleme_item) {

            //Navigation.findNavController(this,R.id.fragmentNavHost).navigate(R.id.action_listFragment_to_descriptionFragment)
            val action = ListFragmentDirections.actionListFragmentToDescriptionFragment("menudengeldim",0)
            Navigation.findNavController(this,R.id.fragmentNavHost).navigate(action)



        }
        return super.onOptionsItemSelected(item)
    }
}