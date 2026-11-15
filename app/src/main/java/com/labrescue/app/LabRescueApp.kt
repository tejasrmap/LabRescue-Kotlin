package com.labrescue.app

import android.app.Application
import com.labrescue.app.data.db.SampleData
import com.labrescue.app.data.repository.LabRescueRepository

class LabRescueApp : Application() {

    lateinit var repository: LabRescueRepository
        private set

    override fun onCreate() {
        super.onCreate()
        SampleData.loadFromAssets(this)
        repository = LabRescueRepository(this)
    }
}
