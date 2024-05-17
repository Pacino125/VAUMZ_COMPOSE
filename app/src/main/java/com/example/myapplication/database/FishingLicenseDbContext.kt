package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myapplication.data.Area
import com.example.myapplication.data.AreaType
import com.example.myapplication.data.Catch
import com.example.myapplication.data.FishType
import com.example.myapplication.data.FishingSession
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class FishingLicenseDbContext(context : Context) :   SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_FISH_TYPE_TABLE_QUERY)
        db.execSQL(CREATE_CATCH_TABLE_QUERY)
        db.execSQL(CREATE_FISHING_SESSION_TABLE_QUERY)
        db.execSQL(CREATE_AREA_TABLE_QUERY)
        db.execSQL(CREATE_AREA_TYPE_TABLE_QUERY)
        insertInitialFishTypes(db)
        insertInitialLipnoveAreas(db)
        insertInitialAreaTypes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FishingLicense"

        private const val CREATE_FISH_TYPE_TABLE_QUERY = """
            CREATE TABLE tbl_fish_type (
                guid TEXT PRIMARY KEY,
                type TEXT,
                min_catch_length INTEGER,
                max_catch_length INTEGER
            )
        """

        private const val CREATE_CATCH_TABLE_QUERY = """
            CREATE TABLE tbl_catch (
                guid TEXT PRIMARY KEY,
                fish_type_guid TEXT,
                fish_count INTEGER,
                length INTEGER,
                weight REAL
            )
        """

        private const val CREATE_FISHING_SESSION_TABLE_QUERY = """
            CREATE TABLE tbl_fishing_session (
                guid TEXT PRIMARY KEY,
                area_guid TEXT,
                date INTEGER,
                is_active INTEGER,
                catch_guid TEXT
            )
        """

        private const val CREATE_AREA_TABLE_QUERY = """
            CREATE TABLE tbl_area (
                guid TEXT PRIMARY KEY,
                name TEXT,
                area_id TEXT,
                area_type_guid TEXT,
                organization_guid TEXT,
                geo_coordinates_guid TEXT,
                chap INTEGER
            )
        """

        private const val CREATE_AREA_TYPE_TABLE_QUERY = """
            CREATE TABLE tbl_area_type (
                guid TEXT PRIMARY KEY,
                type TEXT
            )
        """
    }

    fun getAllAreas(): List<Area> {
        val areas = mutableListOf<Area>()
        val db = readableDatabase
        val query = "SELECT tbl_area.guid, tbl_area.name, tbl_area.area_id, tbl_area.area_type_guid, " +
                "tbl_area.organization_guid, tbl_area.geo_coordinates_guid, tbl_area.chap, " +
                "tbl_area_type.type AS area_type " +
                "FROM tbl_area " +
                "LEFT JOIN tbl_area_type ON tbl_area.area_type_guid = tbl_area_type.guid " +
                "WHERE tbl_area.organization_guid IS NULL"

        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val guidIndex = cursor.getColumnIndex("guid")
            val nameIndex = cursor.getColumnIndex("name")
            val areaIdIndex = cursor.getColumnIndex("area_id")
            val areaTypeIdIndex = cursor.getColumnIndex("area_type_guid")
            val organizationGuidIndex = cursor.getColumnIndex("organization_guid")
            val coordinatesIdIndex = cursor.getColumnIndex("geo_coordinates_guid")
            val chapIndex = cursor.getColumnIndex("chap")
            val areaTypeIndex = cursor.getColumnIndex("area_type")

            if (guidIndex != -1 && nameIndex != -1 && areaIdIndex != -1 && areaTypeIdIndex != -1 &&
                coordinatesIdIndex != -1 && chapIndex != -1 && areaTypeIndex != -1) {
                val guid = cursor.getString(guidIndex)
                val name = cursor.getString(nameIndex)
                val areaId = cursor.getString(areaIdIndex)
                val areaTypeId = cursor.getString(areaTypeIdIndex)
                val organizationId = cursor.getString(organizationGuidIndex)
                val coordinatesId = cursor.getString(coordinatesIdIndex)
                val chap = cursor.getInt(chapIndex) == 1
                val areaType = cursor.getString(areaTypeIndex)

                val area = Area(guid, name, areaId, AreaType(areaTypeId, areaType), organizationId, coordinatesId, chap)
                areas.add(area)
            }
        }

        cursor.close()
        db.close()
        return areas
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFishingSessions(): List<FishingSession> {
        val fishingSessions = mutableListOf<FishingSession>()
        val db = readableDatabase

        val query = "SELECT fishingSession.guid, fishingSession.area_guid, fishingSession.date, fishingSession.is_active, " +
                "catch.guid AS catch_guid, catch.fish_type_guid, catch.fish_count, catch.length, catch.weight, " +
                "area.guid AS area_area_guid, area.name AS area_name, area.area_id, area.chap, fishType.guid AS fish_type_guid, fishType.type AS fish_type_type " +
                "FROM tbl_fishing_session fishingSession " +
                "INNER JOIN tbl_area area ON fishingSession.area_guid = area.guid " +
                "LEFT JOIN tbl_catch catch ON fishingSession.catch_guid = catch.guid " +
                "LEFT JOIN tbl_fish_type fishType ON catch.fish_type_guid = fishType.guid "

        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val sessionGuidIndex = cursor.getColumnIndex("guid")
            val dateIndex = cursor.getColumnIndex("date")
            val isActiveIndex = cursor.getColumnIndex("is_active")
            val catchGuidIndex = cursor.getColumnIndex("catch_guid")
            val fishCountIndex = cursor.getColumnIndex("fish_count")
            val lengthIndex = cursor.getColumnIndex("length")
            val weightIndex = cursor.getColumnIndex("weight")
            val areaGuidIndex = cursor.getColumnIndex("area_area_guid")
            val areaNameIndex = cursor.getColumnIndex("area_name")
            val areaIdIndex = cursor.getColumnIndex("area_id")
            val areaChapIndex = cursor.getColumnIndex("chap")
            val fishTypeGuidIndex = cursor.getColumnIndex("fish_type_guid")
            val fishTypeTypeIndex = cursor.getColumnIndex("fish_type_type")

            if (sessionGuidIndex != -1  && dateIndex != -1 &&
                isActiveIndex != -1 && catchGuidIndex != -1 &&
                areaGuidIndex != -1 && areaNameIndex != -1 && areaIdIndex != -1 &&
                areaChapIndex != -1 && fishTypeGuidIndex != -1 && fishTypeTypeIndex != -1 &&
                weightIndex != -1 && fishCountIndex != -1 && lengthIndex != -1) {

                val sessionGuid = cursor.getString(sessionGuidIndex)
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(cursor.getString(dateIndex))
                val isActive = cursor.getInt(isActiveIndex) == 1
                val catchGuid = cursor.getString(catchGuidIndex)
                val fishTypeGuid = cursor.getString(fishTypeGuidIndex)
                val fishCount = cursor.getInt(fishCountIndex)
                val length = cursor.getInt(lengthIndex)
                val weight = cursor.getDouble(weightIndex)
                val areaGuid = cursor.getString(areaGuidIndex)
                val areaName = cursor.getString(areaNameIndex)
                val areaId = cursor.getString(areaIdIndex)
                val areaChap = cursor.getInt(areaChapIndex) == 1
                val fishTypeType = cursor.getString(fishTypeTypeIndex)

                val area = Area(areaGuid, areaName, areaId, null, null, null, areaChap)
                val fishType = if (fishTypeGuid != null) FishType(fishTypeGuid, fishTypeType, null, null) else null
                val catch = if (catchGuid != null) Catch(catchGuid, fishType, fishCount, length, weight) else null

                val calendar = Calendar.getInstance()
                calendar.time = date

                val localDateTime = LocalDateTime.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)
                )

                val fishingSession = FishingSession(sessionGuid, area, localDateTime, isActive, catch)
                fishingSessions.add(fishingSession)
            }
        }

        cursor.close()
        db.close()
        return fishingSessions
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertFishingSession(fishingSession: FishingSession, areaId: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("guid", fishingSession.guid)
            put("area_guid", areaId)
            put("date", fishingSession.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            put("is_active", fishingSession.isActive)
            put("catch_guid", fishingSession.catchId?.guid)
        }

        db.insert("tbl_fishing_session", null, values)
        db.close()
    }

    fun updateFishingSessionIsActiveToFalse(sessionGuid: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("is_active", 0)
        }

        val whereClause = "guid = ?"
        val whereArgs = arrayOf(sessionGuid)
        db.update("tbl_fishing_session", values, whereClause, whereArgs)
        db.close()
    }

    fun getAllFishTypes(): List<FishType> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tbl_fish_type", null)
        val fishTypes = mutableListOf<FishType>()

        while (cursor.moveToNext()) {
            val guidIndex = cursor.getColumnIndex("guid")
            val typeIndex = cursor.getColumnIndex("type")
            val minCatchLengthIndex = cursor.getColumnIndex("min_catch_length")
            val maxCatchLengthIndex = cursor.getColumnIndex("max_catch_length")

            if (guidIndex != -1 && typeIndex != -1 && minCatchLengthIndex != -1 && maxCatchLengthIndex != -1) {
                val guid = cursor.getString(guidIndex)
                val type = cursor.getString(typeIndex)
                val minCatchLength = cursor.getInt(minCatchLengthIndex)
                val maxCatchLength = cursor.getInt(maxCatchLengthIndex)

                val fishType = FishType(guid, type, minCatchLength, maxCatchLength)
                fishTypes.add(fishType)
            }
        }

        cursor.close()
        db.close()

        return fishTypes
    }

    fun addCatch(catch: Catch, fishingSessionGuid : String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("guid", catch.guid)
            put("fish_type_guid", catch.fishType!!.guid)
            put("fish_count", catch.fishCount)
            put("length", catch.length)
            put("weight", catch.weight)
        }

        db.insert("tbl_catch", null, values)

        val sessionValues = ContentValues().apply {
            put("catch_guid", catch.guid)
        }

        db.update("tbl_fishing_session", sessionValues, "guid = ?", arrayOf(fishingSessionGuid))
        updateFishingSessionIsActiveToFalse(fishingSessionGuid)
        db.close()
    }

    private fun executeInsertStatement(db: SQLiteDatabase, sqlStatement: String) {
        db.execSQL(sqlStatement)
    }

    private fun insertInitialFishTypes(db: SQLiteDatabase) {
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('90132E08-FBDE-4D69-9293-A18F4B9E6CCA', 'Kapor', 40, 100)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('FC13C210-8E54-4450-A81C-652260995BDE', 'Zubáč', 50, 100)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('61368878-FE45-4D47-848E-7A22E830B43F', 'Šťuka', 60, 120)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('468EFDCC-2898-4393-BA0D-3591AFAB702C', 'Sumec', 70, 160)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('9CA54DFE-CE34-4523-B583-B7DF7E1E7151', 'Amur', 70, 100)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('EC084B03-2637-44E4-A183-29CB02F07CC4', 'Pleskáč vysoký', 20, 66)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('E000C625-8301-4763-BF67-AF011BB63C6A', 'Lipeň', 30, 50)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('7437F233-E86F-4491-8FBE-EB09A08F2E47', 'Pstruh potočný', 27, 50)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length, max_catch_length) VALUES ('E1A0CD48-3449-4B65-8D21-C5094BA25323', 'Pstruh dúhový', 27, 50)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('5CDE71C8-B03A-450E-A939-C6BA575A3049', 'Pstruh jazerný', 45)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('05E2E22F-9628-4663-ADD7-FFB5402A533E', 'Boleň', 40)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('48099160-F5F3-4B72-9EA3-BD8F42DEF578', 'Hlavátka', 70)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('94D66AB6-BB17-4A75-B792-7F28F8F4E6F3', 'Jalec hlavatý', 20)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('600F2C6F-2DBC-40CA-A43D-83BA17AE0AE2', 'Jalec iné druhy', 20)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('A51A201D-E543-43D7-9593-B646B88B7EEE', 'Jeseter', 45)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('F4A2D5E3-A88F-41BE-AD6E-4A2E9CB8EBA6', 'Lieň', 25)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('1ED47EE6-D023-4E8A-8C6A-4901259854AA', 'Mieň', 35)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('9489D28D-92FD-4F1C-87CD-FD833ACFB2A1', 'Mrena', 40)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('FA9F9225-5E70-458A-BBAE-04315CF0D440', 'Nosáľ sťahovavý', 25)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('DFDF833C-CC6F-4AB7-804D-8088C20E4803', 'Pleskáč siný', 20)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('377D9134-7539-4EAC-8043-D1A9FA6F6422', 'Pleskáč tuponosý', 20)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('7DDB575F-1F0F-4CCD-83C2-6A88E939D592', 'Pleskáč zelenkavý', 15)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('DA523083-CB90-4D96-BBAF-DC294E69FF1A', 'Podustva', 30)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('AFFB00CA-C4A3-4E36-A340-17741D4F2CBA', 'Sivoň', 25)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('885B312A-56A9-443F-8248-DF0D8040D938', 'Tolstolobik', 45)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type, min_catch_length) VALUES ('E47B30F5-D40B-4971-B983-5F43895E5FC8', 'Úhor', 45)")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type) VALUES ('59818A6F-0A29-45B3-9BC7-E3FA078432F2', 'Belička')")
        executeInsertStatement(db, "INSERT INTO tbl_fish_type (guid, type) VALUES ('D6DAA81C-0863-49B7-89FD-AF9BCFC83AD4', 'Ostatné')")
    }

    private fun insertInitialAreaTypes(db: SQLiteDatabase) {
        executeInsertStatement(db, "INSERT INTO tbl_area_type(guid, type) VALUES ('35C1D4F5-B6D8-4245-9ED2-F9BF3ECD330D', 'Kaprová voda miestna')")
        executeInsertStatement(db, "INSERT INTO tbl_area_type(guid, type) VALUES ('C49E7A0F-95CE-4130-97C3-23BA3102698E', 'Pstruhová voda')")
        executeInsertStatement(db, "INSERT INTO tbl_area_type(guid, type) VALUES ('D780545C-BBB4-4A73-8BF6-97400B2797C8', 'Lipňová voda')")
        executeInsertStatement(db, "INSERT INTO tbl_area_type(guid, type) VALUES ('7C1BB848-0DA0-4805-8D62-C3A545F727EC', 'Kaprová voda celozväzová')")
    }

    private fun insertInitialLipnoveAreas(db: SQLiteDatabase) {
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('F197C5E3-EAFB-4D4A-9C77-B4B4051F0F37', 'Hron č. 9a (H)', '3-1110-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('37A34EE5-4DAD-4B0E-8A8B-427DAD5CE371', 'Hron č. 9b (H)', '3-1111-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('5E51447D-290B-4074-8148-29DDC4470D28', 'Hron č. 11a (H)', '3-1130-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('D8AA6BE6-BF83-49A9-AED7-B9C78F43C877', 'Hron č. 11b (H)', '3-1131-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('9C3CB2EE-6944-42A7-BE76-CAC6F067ED39', 'Rohozná', '3-3350-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, chap) VALUES ('B2BD8EA7-525F-4D36-B5C2-A446AE2AC57D', 'Bystrica č. 1a', '3-0380-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 1")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('32AC227A-6AD5-432F-BE01-092AE4C20EB6', 'Bystrica č. 1b', '3-0381-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('F8971821-1C2C-4626-B68E-22BD4B8A62A5', 'Čierňanka č. 1', '3-0540-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('C73C5B4F-B54D-4BF3-93BE-2D04EAB003A7', 'Orava č. 1a (H)', '3-2710-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('870B24DD-E02F-4588-9530-3E8CF6F72874', 'Udava č. 1', '4-3020-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('B6CBF321-5FC5-40B7-AB0C-45CCAE9AE286', 'Poprad č. 4 (H)', '4-1970-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('463032E1-0207-419B-BB68-C548A6F8BCF8', 'Turiec č. 1b (H)', '3-4431-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('B6854EC8-8ECE-4185-A5DD-D023EDD6D66F', 'Váh č. 16 (H)', '3-4650-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('F20732AC-10D5-4158-B273-8B58D99429D7', 'Biela Orava č. 2', '3-0080-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('16838F43-5498-4C7C-8A99-93FE9A37A9C0', 'Poprad č. 2a (H)', '4-1950-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('ACEF3C71-7087-48FA-96CF-DA96D554806C', 'Poprad č. 2c (H)', '4-1952-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('995BC028-C8C8-4B88-82FD-D480CE2F4767', 'Čierny Hron č. 1 (H)', '3-0560-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('6F9D879A-E3AD-4A7E-A172-9312C3D903DB', 'Hron č. 10a (H)', '3-1120-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('1960F764-C47C-4A76-961C-7192FB84F9F9', 'Hron č. 10b (H)', '3-1121-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, chap) VALUES ('FBD358CC-B5D8-494B-8E5A-B2FFF951D094', 'Orava č. 2a (H)', '3-2720-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 1")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('1330A168-6F37-408A-A90D-0A511FC413E3', 'Orava č. 2b (H)', '3-2721-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('63BB2B7A-B49B-4585-B703-AE6203949E2B', 'Rimava č. 3', '3-3270-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('535A7EDB-1134-410D-B6F5-BB00A641D452', 'Váh č. 18a (H)', '3-4680-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('681F689E-AB4D-4969-93A2-871051D21F27', 'Poprad č. 5 (H)', '4-1980-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('87855189-97AD-4405-93B3-1239CE843A18', 'Dunajec č. 2 (H)', '4-0430-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, chap) VALUES ('6F7D2B47-11B6-43B4-825B-5E6F1415F1CD', 'Poprad č. 3b (H)', '4-1961-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 1")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('F2175BEA-D92D-4A5A-82E4-CC8CAF97289D', 'Poprad č. 3a (H)', '4-1960-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('C2570FD7-5491-4FF0-AB2A-F6A649054630', 'Poprad č. 3c (H)', '4-1962-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('49BBF884-9896-4A8C-B847-A21567922076', 'Poprad č. 1 (H)', '4-1940-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('230F41CB-8ECC-4442-8D55-73F83E14F4CB', 'Orava č. 3 (H)', '3-2730-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('9D470533-0A5C-44E5-A477-ECFDAA094D12', 'Turiec č. 2 (H)', '3-4480-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('6EB3D99B-F42B-40D3-9E26-7E222088E7D5', 'Ondava č. 3', '4-1670-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('4AEC3BD9-E457-4517-9FB0-31E29518CC61', 'Hron č. 7b (H)', '3-1081-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('FBEE85C0-48ED-40C6-A9FF-123CE1461372', 'Rajčanka č. 1', '3-3130-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('7A9AFACF-1E8D-4A81-B9C7-0C41938334C4', 'Váh č. 15 (H)', '3-4640-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, chap) VALUES ('FF35F45A-2969-41AA-8CDC-F1C0E6D89B20', 'Váh č.20', '3-4710-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 1)")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('8D104B35-E57A-452F-BFD2-EEF970D7EA5D', 'Poprad č. 2b (H)', '4-1951-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid) VALUES ('2039CA09-B2CC-4128-A638-9CF7E126BB87', 'Turiec č. 1 (H)', '3-4430-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8'")
    }
}