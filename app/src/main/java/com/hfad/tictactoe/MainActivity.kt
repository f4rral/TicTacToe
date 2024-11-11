package com.hfad.tictactoe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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

        viewModel.isEndGame.observe(this, Observer { isEndGame ->
            if (isEndGame == true) {
                Toast.makeText(this, "End Game! ${viewModel.winPlayer.value} WIN!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}