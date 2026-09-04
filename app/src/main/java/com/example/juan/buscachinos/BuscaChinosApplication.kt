package com.example.juan.buscachinos

import android.app.Application
import android.content.Context
import android.location.LocationManager
import com.example.juan.buscachinos.data.local.AppDatabase
import com.example.juan.buscachinos.data.location.AndroidLocationTracker
import com.example.juan.buscachinos.data.repository.ChinoRepositoryImpl
import com.example.juan.buscachinos.domain.repository.ChinoRepository
import com.example.juan.buscachinos.domain.repository.LocationRepository
import com.example.juan.buscachinos.domain.usecase.AddChinoUseCase
import com.example.juan.buscachinos.domain.usecase.DeleteChinoUseCase
import com.example.juan.buscachinos.domain.usecase.GetLastKnownLocationUseCase
import com.example.juan.buscachinos.domain.usecase.ObserveChinosUseCase
import com.example.juan.buscachinos.domain.usecase.SearchChinosUseCase

/**
 * Contenedor de dependencias manual (sin framework de DI). Cablea `data` -> `domain`
 * respetando la inversion de dependencias: las capas superiores solo conocen las
 * interfaces de [ChinoRepository]/[LocationRepository].
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val chinoRepository: ChinoRepository = ChinoRepositoryImpl(database.chinoDao())

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    private val locationRepository: LocationRepository =
        AndroidLocationTracker(context, locationManager)

    val observeChinosUseCase = ObserveChinosUseCase(chinoRepository)
    val addChinoUseCase = AddChinoUseCase(chinoRepository)
    val deleteChinoUseCase = DeleteChinoUseCase(chinoRepository)
    val getLastKnownLocationUseCase = GetLastKnownLocationUseCase(locationRepository)
    val searchChinosUseCase = SearchChinosUseCase()
}

class BuscaChinosApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
