package com.example.asynctask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.asynctask.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button.setOnClickListener{
            loadData()
        }
    }

    /** Что выполняется последовательно, то является синхронным, иначе асинхронным*/

    private fun loadData() {
        // -------------------------------------
        binding.progressBar.isVisible = true   //
        binding.button.isEnabled = false       //
        //--------------------------------------
        // это и есть callback, который идет на вход функции с потоком
        loadCity{
            // код внутри скобок будет выполняться после отработки потока
            binding.tvCity.text = it
            loadTemp(it){
                binding.tvTempreture.text = it.toString()
                binding.progressBar.isVisible = false
                binding.button.isEnabled = true
            }
        }
    }

    /**
     * Kotlin thread исполняется сразу без команды
     *
     * callback - лямбда функция, которая в данном случае принимает Int и ничего не возвращает
     * для передачи данных из потока используется метод invoke()
     * */

    private fun loadTemp(city: String, callback: (Int) -> Unit) {
        thread {
            Toast.makeText(
                this,
                "Loading temperature for city : ${city}",
                Toast.LENGTH_SHORT
            ).show()
            Thread.sleep(5000)
            callback.invoke(17)
        }
    }

    private fun loadCity(callback: (String) -> Unit) {
        thread {
            Thread.sleep(5000)
            callback.invoke("Moscow")
        }
    }
}