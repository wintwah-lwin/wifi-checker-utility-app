package au.edu.jcu.assessment.utilityapp

import android.app.Application
import au.edu.jcu.assessment.utilityapp.di.AppContainer

class UtilityApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
