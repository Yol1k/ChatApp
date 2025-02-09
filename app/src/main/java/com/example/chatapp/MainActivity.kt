package com.example.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.ui.fragments.FragmentChatsBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, FragmentChatsBinding()).commit()
        }
    }
}

