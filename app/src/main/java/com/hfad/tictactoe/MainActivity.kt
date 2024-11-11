package com.hfad.tictactoe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hfad.tictactoe.viewmodels.GameViewModel
import com.hfad.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = GameViewModel()

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
    }
}