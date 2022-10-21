package com.example.asynctask

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.asynctask.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button.setOnClickListener {
/*
            */
            /**
             * создается сфера действия в рамках жизненного цикла приложения, каманды в которой
             * выполняются отдельно от главного потока, если умрет activity, запрос также будет
             * остановлен
             *//*
            lifecycleScope.launch {
                loadData()
            }*/
            /**
             * Корутины в общем случае являются оберткой над программированием с использованием
             * callback'ов, только используется не thread, а Handler(Looper)
             * */
            loadDataWithoutCoroutine()
        }
    }

    /**
     * реализация аналогичная корутинам (грубо говоря, смысл один и тот же)
     * */

    private fun loadDataWithoutCoroutine(step: Int = 0, obj: Any? = null) {
        when (step) {
            /**
             * код в данном случае делится на 3 блока:
             * сперва синхронно идет код до использования первого callback'а,
             * далее до второго,
             * а после продолжается до конца программы
             * Примечательно, что за поток не приостанавливается (нет применения Thread.sleep())
             */
            0 -> {
                Log.d("MainActivity", "Load started")
                binding.progressBar.isVisible = true
                binding.button.isEnabled = false
                loadCityWithoutCoroutine {
                    loadDataWithoutCoroutine(1, it)
                }
            }
            1 -> {
                val city = obj as String
                binding.tvCity.text = city
                loadTempWithoutCoroutine(city) {
                    loadDataWithoutCoroutine(2, it)
                }
            }
            2 -> {
                val temp = obj as Int
                binding.tvTempreture.text = temp.toString()
                binding.progressBar.isVisible = false
                binding.button.isEnabled = true
                Log.d("MainActivity", "Load finished")
            }
        }
    }

    private fun loadTempWithoutCoroutine(city: String, callback: (Int) -> Unit) {
        runOnUiThread {
            Toast.makeText(
                this,
                "Loading temperature for city : ${city}",
                Toast.LENGTH_SHORT
            ).show()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke(17)
        }, 5000)
    }

    private fun loadCityWithoutCoroutine(callback: (String) -> Unit) {
        thread {
            Handler(Looper.getMainLooper()).postDelayed({
                runOnUiThread {
                    callback.invoke("Moscow")
                }
            }, 5000)
        }
    }

/*
    */
    /**
     * для использования корутины функция помечается модификатором suspend
     * *//*

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
*/

}
