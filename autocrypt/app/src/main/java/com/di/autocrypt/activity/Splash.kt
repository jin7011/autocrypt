package com.di.autocrypt.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.di.autocrypt.R
import com.di.autocrypt.databinding.ActivitySplashBinding
import com.di.autocrypt.model.Repository
import com.di.autocrypt.model.RoomRepository
import com.di.autocrypt.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class Splash : AppCompatActivity() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var roomRepository: RoomRepository
    lateinit var binding: ActivitySplashBinding
    lateinit var viewModel: SplashViewModel
    private val TAG = "Splash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        setViewmodel(this)

        binding.button.setOnClickListener {
            viewModel.getCenters()
        }

        binding.button2.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                log("${roomRepository.getCenters().size}")
                roomRepository.deleteAll()
                log("${roomRepository.getCenters().size}")
            }
        }
    }

    fun setViewmodel(lifecycleOwner: LifecycleOwner) {
        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]

        viewModel.run {
            timerStart()
//            getCenters()

            timerCount.observe(lifecycleOwner) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (it == 80) {
                        if (roomSize() != 100) {
                            viewModel.timerStop()
                            log("${roomSize()}")
                        }
                    } else {
                        binding.progressBar.progress = it
                    }

                    if (it == 100 && roomSize() == 100) {
                        GoMain()
                    }
                }
            }

            centers.observe(lifecycleOwner) {
                if (it.size == 100) {
                    log("size = 100")
                    viewModel.insert(it)
                    viewModel.timerStart()
                }
            }
        }
    }

    fun GoMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }

    fun log(str: String) {
        Log.e(TAG, str)
    }
}