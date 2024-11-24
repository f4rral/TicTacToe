package com.hfad.tictactoe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hfad.tictactoe.viewmodels.GameViewModel
import com.hfad.tictactoe.databinding.ActivityMainBinding
import com.hfad.tictactoe.viewmodels.GameViewModelFactory

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val pictureTic = ResourcesCompat.getDrawable(resources, R.drawable.tic, null)
        val pictureTac = ResourcesCompat.getDrawable(resources, R.drawable.tac, null)

        viewModel = ViewModelProvider(this, GameViewModelFactory(pictureTic, pictureTac))[GameViewModel::class.java]

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.isEndGame.observe(this, Observer { isEndGame ->
            if (isEndGame == true) {
                Toast.makeText(this, "End Game! ${viewModel.winPlayer.value} WIN!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}