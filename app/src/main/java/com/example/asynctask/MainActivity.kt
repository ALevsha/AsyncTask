package com.example.asynctask

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.asynctask.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button.setOnClickListener {

            /**
             * создается сфера действия в рамках жизненного цикла приложения, каманды в которой
             * выполняются отдельно от главного потока, если умрет activity, запрос также будет
             * остановлен
             */
            lifecycleScope.launch {
                loadData()
            }
        }
    }


    /**
     * для использования корутины функция помечается модификатором suspend
     * */
    private suspend fun loadData() {
        Log.d("MainActivity", "Load started")
        binding.progressBar.isVisible = true
        binding.button.isEnabled = false
        val city = loadCity()
        binding.tvCity.text = city
        val temp = loadTemp(city)
        binding.tvTempreture.text = temp.toString()
        binding.progressBar.isVisible = false
        binding.button.isEnabled = true
        Log.d("MainActivity", "Load finished")
    }


    private suspend fun loadTemp(city: String): Int {
        Toast.makeText(
            this,
            "Loading temperature for city : ${city}",
            Toast.LENGTH_SHORT
        ).show()
        // метод delay делает отложенное выполнение, при этом не блокируя основной поток
        delay(5000)
        return 17
    }

    private suspend fun loadCity(): String {
        delay(5000)
        return "Moscow"
    }

}
