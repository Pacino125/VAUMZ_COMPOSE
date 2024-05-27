package com.example.myapplication.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.myapplication.daos.IAreaDao
import com.example.myapplication.daos.IFishTypeDao
import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.Area
import com.example.myapplication.entities.FishType
import com.example.myapplication.screens.LicenseScreen
import com.example.myapplication.screens.SelectAreaManuallyScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private lateinit var db: FishingLicense
    private lateinit var fishTypeDao: IFishTypeDao
    private lateinit var areaDao: IAreaDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = LicenseObject
                    ) {
                        composable<LicenseObject> {
                            LicenseScreen(navigateToSelectArea = { navController.navigate(SelectAreaManuallyObject) },
                                    navigateToFish = { navController.navigate(FishObject) })
                        }
                        composable<FishObject> {
                            //FishScreen()
                        }
                        composable<SelectAreaManuallyObject> {
                            SelectAreaManuallyScreen()
                        }
                    }
                }
            }
        }

        db = Room.databaseBuilder(
            applicationContext,
            FishingLicense::class.java, "fishing_license"
        ).build()

        fishTypeDao = db.fishTypeDao()
        insertFishType()

        areaDao = db.areaDao()
        insertInitialAreas()
    }

    private fun insertFishType() {
        val fishTypes = listOf(
            FishType(type = "Kapor", minCatchLength = 40, maxCatchLength = 100),
            FishType(type = "Zubáč", minCatchLength = 50, maxCatchLength = 100),
            FishType(type = "Šťuka", minCatchLength = 60, maxCatchLength = 120),
            FishType(type = "Sumec", minCatchLength = 70, maxCatchLength = 160),
            FishType(type = "Amur", minCatchLength = 70, maxCatchLength = 100),
            FishType(type = "Pleskáč vysoký", minCatchLength = 20, maxCatchLength = 66),
            FishType(type = "Lipeň", minCatchLength = 30, maxCatchLength = 50),
            FishType(type = "Pstruh potočný", minCatchLength = 27, maxCatchLength = 50),
            FishType(type = "Pstruh dúhový", minCatchLength = 27, maxCatchLength = 50),
            FishType(type = "Pstruh jazerný", minCatchLength = 45, maxCatchLength = null),
            FishType(type = "Boleň", minCatchLength = 40, maxCatchLength = null),
            FishType(type = "Hlavátka", minCatchLength = 70, maxCatchLength = null),
            FishType(type = "Jalec hlavatý", minCatchLength = 20, maxCatchLength = null),
            FishType(type = "Jalec iné druhy", minCatchLength = 20, maxCatchLength = null),
            FishType(type = "Jeseter", minCatchLength = 45, maxCatchLength = null),
            FishType(type = "Lieň", minCatchLength = 25, maxCatchLength = null),
            FishType(type = "Mieň", minCatchLength = 35, maxCatchLength = null),
            FishType(type = "Mrena", minCatchLength = 40, maxCatchLength = null),
            FishType(type = "Nosáľ sťahovavý", minCatchLength = 25, maxCatchLength = null),
            FishType(type = "Pleskáč siný", minCatchLength = 20, maxCatchLength = null),
            FishType(type = "Pleskáč tuponosý", minCatchLength = 20, maxCatchLength = null),
            FishType(type = "Pleskáč zelenkavý", minCatchLength = 15, maxCatchLength = null),
            FishType(type = "Podustva", minCatchLength = 30, maxCatchLength = null),
            FishType(type = "Sivoň", minCatchLength = 25, maxCatchLength = null),
            FishType(type = "Tolstolobik", minCatchLength = 45, maxCatchLength = null),
            FishType(type = "Úhor", minCatchLength = 45, maxCatchLength = null),
            FishType(type = "Belička", minCatchLength = null, maxCatchLength = null),
            FishType(type = "Ostatné", minCatchLength = null, maxCatchLength = null)
        )

        lifecycleScope.launch {
            fishTypeDao.insertAll(fishTypes)
        }
    }

    private fun insertInitialAreas() {
        val areas = listOf(
            Area(name = "Hron č. 9a (H)", areaId = "3-1110-6", chap = false),
            Area(name = "Hron č. 9b (H)", areaId = "3-1111-6", chap = false),
            Area(name = "Hron č. 11a (H)", areaId = "3-1130-6", chap = false),
            Area(name = "Hron č. 11b (H)", areaId = "3-1131-6", chap = false),
            Area(name = "Rohozná", areaId = "3-3350-5", chap = false),
            Area(name = "Bystrica č. 1a", areaId = "3-0380-5", chap = true),
            Area(name = "Bystrica č. 1b", areaId = "3-0381-5", chap = false),
            Area(name = "Čierňanka č. 1", areaId = "3-0540-5", chap = false),
            Area(name = "Orava č. 1a (H)", areaId = "3-2710-6", chap = false),
            Area(name = "Udava č. 1", areaId = "4-3020-5", chap = false),
            Area(name = "Poprad č. 4 (H)", areaId = "4-1970-6", chap = false),
            Area(name = "Turiec č. 1b (H)", areaId = "3-4431-6", chap = false),
            Area(name = "Váh č. 16 (H)", areaId = "3-4650-6", chap = false),
            Area(name = "Biela Orava č. 2", areaId = "3-0080-5", chap = false),
            Area(name = "Poprad č. 2a (H)", areaId = "4-1950-6", chap = false),
            Area(name = "Poprad č. 2c (H)", areaId = "4-1952-6", chap = false),
            Area(name = "Čierny Hron č. 1 (H)", areaId = "3-0560-6", chap = false),
            Area(name = "Hron č. 10a (H)", areaId = "3-1120-6", chap = false),
            Area(name = "Hron č. 10b (H)", areaId = "3-1121-6", chap = false),
            Area(name = "Orava č. 2a (H)", areaId = "3-2720-6", chap = true),
            Area(name = "Orava č. 2b (H)", areaId = "3-2721-6", chap = false),
            Area(name = "Rimava č. 3", areaId = "3-3270-5", chap = false),
            Area(name = "Váh č. 18a (H)", areaId = "3-4680-6", chap = false),
            Area(name = "Poprad č. 5 (H)", areaId = "4-1980-6", chap = false),
            Area(name = "Dunajec č. 2 (H)", areaId = "4-0430-6", chap = false),
            Area(name = "Poprad č. 3b (H)", areaId = "4-1961-6", chap = true),
            Area(name = "Poprad č. 3a (H)", areaId = "4-1960-6", chap = false),
            Area(name = "Poprad č. 3c (H)", areaId = "4-1962-6", chap = false),
            Area(name = "Poprad č. 1 (H)", areaId = "4-1940-6", chap = false),
            Area(name = "Orava č. 3 (H)", areaId = "3-2730-6", chap = false),
            Area(name = "Turiec č. 2 (H)", areaId = "3-4480-6", chap = false),
            Area(name = "Ondava č. 3", areaId = "4-1670-5", chap = false)
        )

        lifecycleScope.launch {
            areaDao.insertAll(areas)
        }
    }
}

@Serializable
object LicenseObject

@Serializable
object FishObject

@Serializable
object SelectAreaManuallyObject