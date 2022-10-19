package com.example.asynctask

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.asynctask.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // приложение крашилось потому, что работать с view можно только в главном потоке
    // передавать вызов метода из главного потока можно посредством класса Handler,
    // которому на вход идет объект класса Runnable

    // также для передачи и обработки сообщений между потоками используется наследник Handler
    // с переопределенным методом

    private val handler = object : Handler() {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            println("HANDLE_MESSAGE $msg")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            loadData()
        }

        handler.sendMessage(Message.obtain(handler, 0, 11))
    }

    /** Что выполняется последовательно, то является синхронным, иначе асинхронным*/

    private fun loadData() {
        // -------------------------------------
        binding.progressBar.isVisible = true   //
        binding.button.isEnabled = false       //
        //--------------------------------------
        // это и есть callback, который идет на вход функции с потоком
        loadCity {
            // код внутри скобок будет выполняться после отработки потока
            binding.tvCity.text = it
            loadTemp(it) {
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
        /* объект класса Handler без параметров создается только в главном потоке,
         для создания в другом потоке необходимо объект Runnable в очередь сообщений  из класса
         Looper, в котором надо исполнить команды:
         */
        /*Handler(Looper.getMainLooper()*//*ссылка на главный поток*//*).post {
            TODO()
        }*/
        // для передачи управления в другой поток:
       /* Handler(Looper.myLooper()!!){
            TODO()
        }*/
        thread {
            // также можно использовать метод runOnUiThread{}
            runOnUiThread{
                TODO()
            }
            handler.post {
                Toast.makeText(
                    this,
                    "Loading temperature for city : ${city}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Thread.sleep(5000)
            handler.post {
                callback.invoke(17)
            }
        }
    }

    private fun loadCity(callback: (String) -> Unit) {
        thread {
            Thread.sleep(5000)
            handler.post {
                callback.invoke("Moscow")
            }
        }
    }
}