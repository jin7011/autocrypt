package com.di.autocrypt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di.autocrypt.data.Center
import com.di.autocrypt.model.Repository
import com.di.autocrypt.model.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SplashViewModel  @Inject constructor(
    private val repository: Repository,
    private val roomRepository: RoomRepository
): ViewModel(){

    private val _timerCount = MutableLiveData<Int>()
    private lateinit var a : Job

    val centers: MutableLiveData<ArrayList<Center>> by lazy {
        MutableLiveData<ArrayList<Center>>()
    }

    val timerCount : LiveData<Int>
        get() = _timerCount

    init{
        _timerCount.value = 0
    }

    fun getCenters(){
        viewModelScope.launch {
            val list = ArrayList<Center>()
            for(page in 1..10){
                val res = repository.getCenters(page,10)
                list.addAll(res.centerList)
            }
            centers.value = list
        }
    }

    fun timerStart(){
        if(::a.isInitialized) a.cancel()

//        _timerCount.value = 1
        a = viewModelScope.launch {
            while(_timerCount.value!! <= 100) {
                _timerCount.value = _timerCount.value!!.plus(1)
                delay(2)
            }
        }
    }

    fun timerStop(){
        if(::a.isInitialized) a.cancel()
    }

    fun insert(centerList:List<Center>) = CoroutineScope(Dispatchers.IO).launch{
        roomRepository.insertCenters(centerList)
    }

    suspend fun roomSize():Int = withContext(Dispatchers.IO){
        return@withContext roomRepository.getCenters().size
    }

}