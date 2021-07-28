package ru.myitschool.nasa_bootcamp.ui.asteroid_radar

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import ru.myitschool.nasa_bootcamp.data.model.AsteroidModel
import ru.myitschool.nasa_bootcamp.data.repository.NasaRepository
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class AsteroidRadarViewModelImpl @Inject constructor(
    private val repository: NasaRepository
) : ViewModel(), AsteroidRadarViewModel {

    var list: ArrayList<AsteroidModel> = arrayListOf()

    var listOfAsteroids: MutableLiveData<ArrayList<AsteroidModel>> =
        MutableLiveData<ArrayList<AsteroidModel>>()


    override suspend fun getAsteroidList() {
        val response = repository.getAsteroids(getTodayDateFormatted(), getPlusSevenDaysDateFormatted())
        list = parseAsteroidsJsonResult(response.body()!!.near_earth_objects)

        listOfAsteroids.value = list
    }

    override fun getAsteroidListViewModel(): MutableLiveData<ArrayList<AsteroidModel>> {
        return listOfAsteroids
    }

    override fun getViewModelScope() = viewModelScope

}