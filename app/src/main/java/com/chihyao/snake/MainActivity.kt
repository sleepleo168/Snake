 package com.chihyao.snake

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chihyao.snake.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.content_main.*

 class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val viewModel = ViewModelProvider(this).get(SnakeViewModel::class.java)

        viewModel.body.observe(this, Observer {
            game_view.snakeBody = it
            game_view.invalidate()
        })
        viewModel.score.observe(this, Observer {
            score.setText(it.toString())
        })
        viewModel.gameState.observe(this, Observer { gameState ->
            when (gameState) {
                GameState.GAME_OVER -> {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Game")
                        .setMessage("Game Over")
                        .setPositiveButton("OK", null)
                        .show()
                }
                GameState.ONGOING -> {
                    viewModel.refresh()
                }
                GameState.PAUSE -> {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Pause")
                        .setMessage("Game Paused")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        })
        viewModel.apple.observe(this, Observer {
            game_view.apple = it
            game_view.invalidate()
        })

        binding.fab.setOnClickListener { view ->
            if (viewModel.gameState.value == GameState.ONGOING) {
                viewModel.pause()
            }
            AlertDialog.Builder(this)
                .setTitle("Replay")
                .setMessage("Are you Sure?")
                .setPositiveButton("OK",{dialog, which ->
                    viewModel.reset()
                    viewModel.start()
                })
                .setNeutralButton("Cancel", { dialog, which ->
                    if (viewModel.gameState.value == GameState.PAUSE) {
                        viewModel.start()
                    }
                })
                .show()
        }
        viewModel.reset()
        viewModel.start()
        top.setOnClickListener { viewModel.move(Direction.TOP) }
        down.setOnClickListener { viewModel.move(Direction.DOWN) }
        left.setOnClickListener { viewModel.move(Direction.LEFT) }
        right.setOnClickListener { viewModel.move(Direction.RIGHT) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}