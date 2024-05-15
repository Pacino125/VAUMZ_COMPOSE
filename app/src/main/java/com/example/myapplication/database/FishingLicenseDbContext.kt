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
import com.example.myapplication.data.User
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class FishingLicenseDbContext(context : Context) :   SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USER_TABLE_QUERY)
        db.execSQL(CREATE_FISH_TYPE_TABLE_QUERY)
        db.execSQL(CREATE_LICENSE_TABLE_QUERY)
        db.execSQL(CREATE_LICENSE_TYPE_TABLE_QUERY)
        db.execSQL(CREATE_CATCH_TABLE_QUERY)
        db.execSQL(CREATE_FISHING_SESSION_TABLE_QUERY)
        db.execSQL(CREATE_AREA_TABLE_QUERY)
        db.execSQL(CREATE_AREA_TYPE_TABLE_QUERY)
        db.execSQL(CREATE_COORDINATES_TABLE_QUERY)
        db.execSQL(CREATE_ORGANIZATIONS_TABLE_QUERY)
        insertInitialLicenseTypes(db)
        insertInitialFishTypes(db)
        insertInitialOrganizations(db)
        insertInitialLipnoveCoordinates(db)
        insertInitialLipnoveAreas(db)
        insertInitialAreaTypes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FishingLicense"

        private const val CREATE_USER_TABLE_QUERY = """
            CREATE TABLE tbl_user (
                guid TEXT PRIMARY KEY,
                email TEXT,
                name TEXT,
                fullname TEXT,
                organization_guid TEXT,
                child BOOLEAN,
                birth TEXT,
                address TEXT,
                memberYear INTEGER,
                number TEXT,
                password TEXT
            )
        """

        private const val CREATE_FISH_TYPE_TABLE_QUERY = """
            CREATE TABLE tbl_fish_type (
                guid TEXT PRIMARY KEY,
                type TEXT,
                min_catch_length INTEGER,
                max_catch_length INTEGER
            )
        """

        private const val CREATE_LICENSE_TABLE_QUERY = """
            CREATE TABLE tbl_license (
                guid TEXT PRIMARY KEY,
                license_type_guid TEXT,
                user_guid TEXT,
                year INTEGER
            )
        """

        private const val CREATE_LICENSE_TYPE_TABLE_QUERY = """
            CREATE TABLE tbl_license_type (
                guid TEXT PRIMARY KEY,
                type TEXT,
                description TEXT,
                price_for_adult INTEGER,
                price_for_child INTEGER
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
                license_guid TEXT,
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

        private const val CREATE_COORDINATES_TABLE_QUERY = """
            CREATE TABLE tbl_coordinates (
                guid TEXT PRIMARY KEY,
                latitude_first REAL,
                longitude_first REAL,
                latitude_second REAL,
                longitude_second REAL
            )
        """

        private const val CREATE_ORGANIZATIONS_TABLE_QUERY = """
            CREATE TABLE tbl_organizations (
                guid TEXT PRIMARY KEY,
                name TEXT
            )
        """
    }

    fun insertUser(user: User) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("guid", user.guid)
            put("email", user.email)
            put("name", user.name)
            put("fullname", user.fullname)
            putNull("organization_guid")
            put("child", user.child)
            put("birth", user.birth)
            put("address", user.address)
            putNull("memberYear")
            putNull("number")
            put("password", user.password)
        }
        db.insert("tbl_user", null, values)
        db.close()
    }

    fun getUserByEmail(email: String): User? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tbl_user WHERE email = ?", arrayOf(email))
        var user: User? = null

        if (cursor.moveToFirst()) {
            val guidIndex = cursor.getColumnIndex("guid")
            val emailIndex = cursor.getColumnIndex("email")
            val nameIndex = cursor.getColumnIndex("name")
            val fullnameIndex = cursor.getColumnIndex("fullname")
            val childIndex = cursor.getColumnIndex("child")
            val birthIndex = cursor.getColumnIndex("birth")
            val addressIndex = cursor.getColumnIndex("address")
            val passwordIndex = cursor.getColumnIndex("password")

            if (guidIndex != -1 && emailIndex != -1 && nameIndex != -1 && fullnameIndex != -1 &&
                childIndex != -1 && birthIndex != -1 &&
                addressIndex != -1 && passwordIndex != -1) {
                user = User(
                    cursor.getString(guidIndex),
                    cursor.getString(emailIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(fullnameIndex),
                    cursor.getString(passwordIndex),
                    null,
                    cursor.getInt(childIndex) != 0,
                    cursor.getString(birthIndex),
                    cursor.getString(addressIndex),
                    null,
                    null
                )
            }
        }

        cursor.close()
        db.close()

        return user
    }

    fun getUserByGuid(guid: String?): User? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tbl_user WHERE guid = ?", arrayOf(guid))
        var user: User? = null

        if (cursor.moveToFirst()) {
            val guidIndex = cursor.getColumnIndex("guid")
            val emailIndex = cursor.getColumnIndex("email")
            val nameIndex = cursor.getColumnIndex("name")
            val fullnameIndex = cursor.getColumnIndex("fullname")
            val organizationGuidIndex = cursor.getColumnIndex("organization_guid")
            val childIndex = cursor.getColumnIndex("child")
            val birthIndex = cursor.getColumnIndex("birth")
            val addressIndex = cursor.getColumnIndex("address")
            val memberYearIndex = cursor.getColumnIndex("memberYear")
            val numberIndex = cursor.getColumnIndex("number")
            val passwordIndex = cursor.getColumnIndex("password")

            if (guidIndex != -1 && emailIndex != -1 && nameIndex != -1 && fullnameIndex != -1 &&
                organizationGuidIndex != -1 && childIndex != -1 && birthIndex != -1 &&
                addressIndex != -1 && memberYearIndex != -1 && numberIndex != -1 && passwordIndex != -1) {
                user = User(
                    cursor.getString(guidIndex),
                    cursor.getString(emailIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(fullnameIndex),
                    cursor.getString(passwordIndex),
                    cursor.getString(organizationGuidIndex),
                    cursor.getInt(childIndex) != 0,
                    cursor.getString(birthIndex),
                    cursor.getString(addressIndex),
                    cursor.getString(memberYearIndex),
                    cursor.getString(numberIndex)
                )
            }
        }

        cursor.close()
        db.close()

        return user
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

    fun getAreasInUserOrganization(organizationId: String): List<Area>? {
        val areas = mutableListOf<Area>()
        val db = readableDatabase

        if (organizationId.isEmpty())
            return null

        val query = "SELECT tbl_area.guid, tbl_area.name, tbl_area.area_id, tbl_area.area_type_guid, " +
                "tbl_area.organization_guid, tbl_area.geo_coordinates_guid, tbl_area.chap, " +
                "tbl_area_type.type AS area_type " +
                "FROM tbl_area " +
                "INNER JOIN tbl_area_type ON tbl_area.area_type_guid = tbl_area_type.guid " +
                "WHERE tbl_area.organization_guid = ?"

        val cursor = db.rawQuery(query, arrayOf(organizationId))
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
                "area.guid AS area_area_guid, area.name AS area_name, area.area_id, fishType.guid AS fish_type_guid, fishType.type AS fish_type_type " +
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
            val fishTypeGuidIndex = cursor.getColumnIndex("fish_type_guid")
            val fishTypeTypeIndex = cursor.getColumnIndex("fish_type_type")

            if (sessionGuidIndex != -1  && dateIndex != -1 &&
                isActiveIndex != -1 && catchGuidIndex != -1 &&
                areaGuidIndex != -1 && areaNameIndex != -1 && areaIdIndex != -1 &&
                fishTypeGuidIndex != -1 && fishTypeTypeIndex != -1 && weightIndex != -1 &&
                fishCountIndex != -1 && lengthIndex != -1) {

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
                val fishTypeType = cursor.getString(fishTypeTypeIndex)

                val area = Area(areaGuid, areaName, areaId, null, null, null, null)
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

                val fishingSession = FishingSession(sessionGuid, null, area, localDateTime, isActive, catch)
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
            put("license_guid", fishingSession.licenseId?.guid)
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

    private fun executeInsertStatement(db: SQLiteDatabase, sqlStatement: String) {
        db.execSQL(sqlStatement)
    }

    private fun insertInitialOrganizations(db: SQLiteDatabase) {
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('BBA38923-693C-46DC-8606-B072915708B8', 'Bánovce nad Bebravou')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('12ACD0DC-E2FF-4182-B441-7AA42163BB15', 'Banská Bystrica')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('4C95A402-0C05-4EB5-A7B2-38497A44EA25', 'Banská Štiavnica')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('7FB1D415-78CD-4475-B333-8ED2377C8FA5', 'Bardejov')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('AFC7E560-F17F-49BE-A603-86E3ABB769BD', 'Bratislava')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('255D0D75-1B6B-4AB9-A42C-EA248EB9810A', 'Krupina')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('16D5EBDB-DAD0-4DD3-8F65-BE68FB7B7E96', 'Brezno')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('1D3483FC-8B71-488B-BFD8-22732D608BBF', 'Bytča')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('A4E688AF-DEAC-41E9-9D66-7CFC6DD81996', 'Čadca')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('558E8277-FAA9-4500-BA0D-15938831DE36', 'Detva')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('547196B3-947C-452A-AA5E-92B8718EAE0A', 'Dolný kubín')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('C4D6E127-6022-4E94-93A7-4E870E7B7BF6', 'Dunajská streda')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('75077447-576C-4BC0-9934-710450EFFE81', 'Galanta')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('2C1A0998-8D0A-4213-952A-75740A6D38AB', 'Gelnica')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('E2DCE6E2-C788-4305-8EBB-31AA7D516493', 'Hlohovec')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('FF530DC3-2C08-4A72-AB3A-9624DC858ED9', 'Humenné')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('A477DD92-106C-48F7-9C9B-2F8D7A529CF2', 'Ilava')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('C1319B30-6987-4385-ABC2-D5F2F527C434', 'Kežmarok')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('EF469EB0-3D47-429E-8BC8-9914469DFE2B', 'Komárno')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('7402D450-1690-4FF0-A8C8-F4BB71AF9CA1', 'Košice')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('AC8E8BF2-2CC5-46ED-A899-94D8CC83A58A', 'Kysucké Nové Mesto')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('211EEEA5-6585-4349-9691-9DCFDBED0A3B', 'Levice')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('E479B9CD-5ACC-4EC5-9DEC-6EACAD2E26D7', 'Levoča')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('8124D5E8-E40C-43B1-9560-46484EE9EF07', 'Liptovský Mikuláš')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('C79B9211-FA4A-4226-A62A-715035545BC3', 'Lúčenec')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('288D1C7A-0716-4B52-9BD8-73C7F1B0980F', 'Malacky')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('098760D6-983C-4077-A33B-838BDACAA504', 'Martin')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('4AA4E217-DEEE-499C-9359-D9CB9E3BE93A', 'Medzilaborce')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('0AD44E78-5728-469A-B700-09A1206B317F', 'Michalovce')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('5282DADA-E822-4890-B9D7-93224E3296FD', 'Myjava')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('4FDCD9F3-9CB9-43B7-A6FB-DF7FE4656202', 'Námestovo')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('A987C831-2BB3-4833-A597-F81F56038FC0', 'Nitra')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('B35A35BD-91D0-47BE-A15A-7CB48C2D4326', 'Nové Mesto nad Váhom')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('1879E42F-6BB4-4D63-8434-AF108FB1A88D', 'Nové Zámky')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('89D65182-3BA7-474A-9CA4-8CD9620B4D69', 'Partizánske')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('5F2B82FF-622C-4C6C-A9DB-08D4AE5FEE84', 'Pezinok')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('F69232FE-BDB8-43DD-A90C-F6A5E38EB05F', 'Piešťany')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('0A60F869-C130-48B5-82A0-965D2E8C561C', 'Poltár')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('C7093E1F-5CB7-47FB-90E3-065C03C485DF', 'Poprad')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('756D6ADF-25E1-4C20-AC19-3C8FDDE0ED31', 'Považská Bystrica')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('8F6909F0-0EC9-4A79-A905-EEBFB1D56C44', 'Prešov')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('A88835CB-C10A-4ACF-ABF7-4CAB496B9EA8', 'Prievidza')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('C1DB0A98-2BEF-4D52-B8BF-2E20BCCDBBB2', 'Púchov')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('830C9F40-64CE-447D-933A-5E004A59D323', 'Revúca')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('B5902C72-3809-4961-8300-B7BD5D46FF3D', 'Rimavská Sobota')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('00C01BA8-C5AB-4015-A8A3-A9786E3EC1D6', 'Rožňava')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('43145652-9234-473C-A8C8-CD4B5DE2B5AC', 'Ružomberok')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('C69CBA13-3FF4-4CDB-ACE0-B214D93B3C66', 'Sabinov')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('13BA841E-EF98-44BF-8D25-5811BBD7039F', 'Senec')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('6F0CE48B-B3C1-412A-B9DF-E99975B75A9F', 'Senica')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('2C2AC788-F26B-47EA-8C3F-1C02C0B6C9BA', 'Skalica')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('3775CB8C-B820-4FE1-948E-6BD73152DD58', 'Snina')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('438D09FF-A6BE-469C-A9E9-F507C185DA86', 'Sobrance')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('6995EDC1-B2DB-430C-867C-AF90E591A515', 'Spišská Nová Ves')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('010E1C6E-C233-4058-B310-7089329F9909', 'Stará Ľubovňa')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('B80BEA9D-5EFD-477F-BFBB-30A55C389C34', 'Stropkov')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('171602A7-8342-413A-8119-86A51BF31A9D', 'Svidník')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('3DD6DD3A-F033-4B6A-83C8-96835A1D4688', 'Šaľa')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('42EBFB2F-73B9-431E-9681-C1A2E73D4359', 'Šurany')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('5CE9D8D9-FA49-45E4-889B-93A9503AB862', 'Topoľčany')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('BABC9B2D-7C41-4DFE-90D8-00ABD8632869', 'Trebišov')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('EA3146C8-B14A-41B6-8F38-0264D5E7BBF8', 'Trenčín')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('E3B71541-1B5F-4EA9-8F19-CEF80266E65D', 'Trnava')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('F625CA5D-40E0-49A5-BEE1-E8BF8278EB80', 'Trstená')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('29EAD41F-8F5D-4115-BBDF-56E5386EA842', 'Turčianske Teplice')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('73DE763F-06EA-48E1-8A32-CE6413517123', 'Tvrdošín')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('11C0A25D-D347-44C4-95EE-6E3B107651F8', 'Veľký Krtíš')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('F0E0427E-F328-496C-B8DC-256B003B7B21', 'Vranov')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('155B6957-99FF-4C4E-9A24-8A7A1B13C317', 'Vranov nad Topľou')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('E34B78D1-73C8-4762-9A1A-3D0E1D1FC951', 'Zlaté Moravce')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('87620942-9AB0-48D2-AA8D-A38658BA7328', 'Zvolen')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('E782EE53-3D1F-492C-92D1-60F545F088DE', 'Žarnovica')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('3AB302CA-146E-4168-B174-DB8C57B4DF84', 'Žiar nad Hronom')")
        executeInsertStatement(db, "INSERT INTO tbl_organizations (guid, name) VALUES ('87B24368-6637-4BFC-B1F5-032F76B30DB6', 'Žilina')")
    }

    private fun insertInitialLicenseTypes(db: SQLiteDatabase) {
        executeInsertStatement(
            db,
            "INSERT INTO tbl_license_type (guid, type, description, price_for_adult, price_for_child) " +
                    "VALUES ('BD979080-113E-4573-802F-46996D1AD69C', 'Kaprové vody', " +
                    "'Kaprové vody sú vody, ktoré svojím charakterom a kvalitou vytvárajú prostredie na život predovšetkým takých druhov rýb, akými sú kapor sazan (Cyprinus carpio - divá forma), kapor rybničný (Cyprinus carpio - zdomácnená forma), šťuka severná (Esox lucius), zubáč veľkoústy (Stizostedion lucioperca), sumec veľký (Silurus glanis) a úhor európsky (Anguilla anguilla). Významné sprievodné druhy rýb sú hlavátka podunajská (Hucho hucho), podustva severná (Chondrostoma nasus), mrena severná (Barbus barbus), jalec hlavatý (Leuciscus cephalus) a jalec maloústy (Leuciscus leuciscus).&V kaprových vodách sa zakazuje:|a) loviť na viac ako dve udice|b) loviť na viac ako dva nadväzce na jednej udici opatrené jednoduchým háčikom alebo na viac ako jeden nadväzec opatrený dvojháčikom alebo trojháčikom a na viac ako tri mušky pri použití muškárskej udice|c) loviť na rybku prinesenú z iného revíru|d) loviť na rybku alebo prívlač od 1. februára do 15. júna|e) pri love na prívlač loviť na viac ako jednu udicu s viac ako jednou nástrahou|f) loviť v čase od 22.00 do 04.00 h bez osvetlenia miesta lovu', 60, 20)"
        )
        executeInsertStatement(
            db,
            "INSERT INTO tbl_license_type (guid, type, description, price_for_adult, price_for_child) " +
                    "VALUES ('4D8FEA6F-9C08-45EF-A3A9-2E2823D46C85', 'Pstruhové vody', " +
                    "'Pstruhové vody sú vody, ktoré svojím charakterom a kvalitou vytvárajú prostredie na život predovšetkým takých druhov rýb, akými sú pstruh potočný  (Salmo trutta morpha fario), pstruh dúhový (Oncorhynchus mykiss), sivoň potočný (Salvelinus fontinalis), hlavátka podunajská (Hucho hucho) a lipeň tymiánový (Thymallus thymallus). Významné sprievodné druhy rýb sú hlaváč bieloplutvý (Cottus gobio), hlaváč pásoplutvý (Cottus poecilopus), čerebľa pestrá (Phoxinus phoxinus) a slíž severný (Barbatula barbatula).&V pstruhových vodách sa zakazuje:|a) loviť viac než jednou udicou|b) používat ako nástrahu živú rybku|c) používat pri love iné ako umelé mušky v počte väčšom než tri|d) pri love prívlačou na mŕtvu rybku používať viac než jeden háčik|e) loviť na plávanú a položenú|f) používať pri love viac ako jednu nástrahu okrem lovu na umelé mušky|g) používat ako nástrahu červy a hmyz vo všetkých vývojových štádiách|h) loviť viac ako tri dni v týždni', 30, 15)"
        )
        executeInsertStatement(
            db,
            "INSERT INTO tbl_license_type (guid, type, description, price_for_adult) " +
                    "VALUES ('F20E2FAD-1D3D-4295-B997-DC2E8C9EEF6E', 'Lipňové vody', " +
                    "'V lipňových vodách sa zakazuje:|a) loviť viac než jednou udicou|b)používať ako nástrahu živú rybku|c) používať pri love iné ako umelé mušky v počte väčšom než tri a iné ako muškárske náradie|d )loviť na mŕtvu rybku alebo prívlač od 1. júna do 31. októbra|e) používať pri love na plávanú iné nástrahy ako rastlinného pôvodu|f) loviť ryby na položenú|g) používať ako nástrahu hmyz vo všetkých vývojových štádiách, červy všetkých druhov, ryžu, tarhoňu, krúpy a všetky napodobneniny ikier|h) loviť viac ako pät dní v týždni', 70)"
        )
        executeInsertStatement(
            db,
            "INSERT INTO tbl_license_type (guid, type, description, price_for_adult) " +
                    "VALUES ('3306AA97-0047-4BAA-BEFA-5AE510B49E73', 'Povolenie na lov hlavátky', " +
                    "'Pri love hlavátky sa zakazuje:|a) používať ako nástrahu živú rybku|b) loviť hlavátku inak než prívlačou na umelé nástrahy, rybku alebo muškárením|c) ponechať si v jednom kalendárnom roku viac ako jednu hlavátku, aj keď sa lov uskutočnuje vo viacerých revíroch', 100)"
        )
        executeInsertStatement(
            db,
            "INSERT INTO tbl_license_type (guid, type, description, price_for_adult, price_for_child) " +
                    "VALUES ('33910006-977D-4C3E-90CD-8C1E91156EB2', 'Zväzové povolenie', " +
                    "'Zväzové vody sú vody, ktoré svojím charakterom a kvalitou vytvárajú prostredie na život predovšetkým takých druhov rýb, akými sú kapor sazan (Cyprinus carpio - divá forma), kapor rybničný (Cyprinus carpio - zdomácnená forma), šťuka severná (Esox lucius), zubáč velkoústy (Stizostedion lucioperca), sumec velký (Silurus glanis) a úhor európsky (Anguilla anguilla). Významné sprievodné druhy rýb sú hlavátka podunajská (Hucho hucho), podustva severná (Chondrostoma nasus), mrena severná (Barbus barbus), jalec hlavatý (Leuciscus cephalus) a jalec maloústy (Leuciscus leuciscus).&V kaprových vodách sa zakazuje:|a) loviť na viac ako dve udice|b) loviť na viac ako dva nadväzce na jednej udici opatrené jednoduchým háčikom alebo na viac ako jeden nadväzec opatrený dvojháčikom alebo trojháčikom a na viac ako tri mušky pri použití muškárskej udice|c) loviť na rybku prinesenú z iného revíru|d) loviť na rybku alebo prívlač od 1. februára do 15. júna|e) pri love na prívlač loviť na viac ako jednu udicu s viac ako jednou nástrahou|f) loviť v čase od 22.00 do 04.00 h bez osvetlenia miesta lovu', 75, 15)"
        )
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

    private fun insertInitialLipnoveCoordinates(db: SQLiteDatabase) {
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('E13BA2BB-1A15-4BDA-9D7C-2DE37966F55B', 48.715436, 19.138723, 48.733228, 19.159516)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('00949F21-53CF-418B-A5F5-0E5201292E7D', 48.733057, 19.159878, 48.737960, 19.207111)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('172C352F-7FF5-4573-9382-693E07C53B83', 48.802584, 19.629284, 48.810107, 19.607095)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('45397F08-555F-4C97-B819-DF1876A699B0', 48.831254, 19.777420, 48.802442, 19.629744)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('1F5C7649-8EF6-486A-87B8-43967CA93EB6', 48.818582, 19.671298, 48.753612, 19.796824)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('26221CD1-562C-468C-90F1-98CB15F43DC9', 49.397098, 18.829540, 49.388881, 18.868900)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('8BAFC4B6-106B-49AF-92DA-D79C5966BAF4', 49.388746, 18.869169, 49.364378, 18.900331)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('D221527A-0EDF-4775-A620-71156D1AD490', 49.442547, 18.785875, 49.478683, 18.790146)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('2EF77059-7365-4EB7-953D-DE2A959DC997', 49.209516, 19.279793, 49.153483, 19.139574)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('C65A3B6D-D2B0-401D-8C2B-A01AAD4AF38A', 49.025877, 22.044302, 48.963134, 21.948973)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('7C2CE5AC-7805-4360-90EA-3A3A72E155EF', 49.225418, 20.498131, 49.245436, 20.512466)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('204433E0-5C04-4EB6-855D-C9EB5874DBAA', 49.086017, 18.925837, 48.940555, 18.829821)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('C58407A4-70C7-49B8-83B9-8456FBD0777A', 49.138324, 18.893894, 49.170690, 18.876768)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('8959723D-6A79-4E41-8959-7CE10E50C5B6', 49.380256, 19.364201, 49.384481, 19.436776)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('95165462-D892-47DC-9411-CDE34BDD7172', 49.296546, 20.923881, 49.325233, 20.897289)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('244C1EBF-F018-47D8-8645-ADEF3056303C', 49.287943, 20.791516, 49.257082, 20.846239)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('357EE221-E036-4660-B3E4-95D8AB919EBF', 48.804943, 19.560635, 48.794907, 19.582837)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('19A9DAEB-A9BC-4D52-98FD-CAE20B174105', 48.804751, 19.559924, 48.778410, 19.334263)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('B3000337-9974-4063-8C31-AED43B5A5D08', 48.803537, 19.558179, 48.810441, 19.606190)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('44621313-1147-4BA5-859C-9B4023C3C9E8', 49.264439, 19.376735, 49.261355, 19.328746)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('DCD627FA-EAB5-4C8E-9485-EABA345162B1', 49.264369, 19.376926, 49.286439, 19.477458)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('1322D381-BD29-487D-A0E9-2FFB1B5A0C26', 48.385487, 19.999267, 48.464536, 19.957385)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('86BB526C-F866-4FC6-876F-174A30D02CBF', 49.153548, 19.140771, 49.085597, 19.303034)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('90A53353-A183-4DCF-A3B5-A13626090F0C', 49.170936, 20.459764, 49.224392, 20.498040)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('ADD86749-2B1C-46ED-B999-9917DD45A2D2', 49.399413, 20.414774, 49.401745, 20.338162)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('53CFE309-8D15-41A4-B655-AAA473B881C2', 49.304912, 20.682041, 49.302961, 20.665822)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('D7314F14-254B-460D-9466-B602EE3C29F3', 49.288171, 20.791115, 49.305164, 20.682252)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('9029D564-FF15-4F22-8BF6-86B4EE2869C6', 49.245762, 20.512971, 49.302669, 20.664617)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('9A95BD5B-B550-4A7A-81C3-00E6D9C9F791', 49.419609, 20.722928, 49.341428, 20.871230)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('5F9D625D-8449-406A-A252-A35281702B40', 49.286492, 19.477644, 49.332356, 19.553327)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('5D8B7AC2-118C-47CC-8FA9-A6E41121CFA7', 48.940291, 18.829692, 48.863627, 18.796219)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('279310F6-C919-48D8-AAE4-D5BDA699E7E5', 48.966787, 21.706659, 48.962126, 21.721341)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('9B00B83E-C381-40E5-B192-0301DA0901FE', 48.563517, 19.106634, 48.626835, 19.140219)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('212A68CE-5F28-48BD-B35C-7A306BB7BC28', 49.230132, 18.710477, 49.178469, 18.727069)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('9D83E20A-4BCA-4870-B02E-78D60897FF19', 49.170691, 18.876369, 49.138120, 18.893790)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('DD3216D6-540F-4580-8901-CBDCBB6E5E70', 49.091285, 19.586396, 49.086203, 19.603367)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('BB46C049-5815-4079-A6EC-4A8F747C8717', 49.257201, 20.846362, 49.261039, 20.828517)")
        executeInsertStatement(db, "INSERT INTO tbl_coordinates (guid, latitude_first, longitude_first, latitude_second, longitude_second) VALUES ('6E1FCE15-17D8-44F1-9757-FE4DD960D9C6', 49.120411, 18.916425, 48.940512, 18.829815)")
    }

    private fun insertInitialLipnoveAreas(db: SQLiteDatabase) {
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('F197C5E3-EAFB-4D4A-9C77-B4B4051F0F37', 'Hron č. 9a (H)', '3-1110-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'E13BA2BB-1A15-4BDA-9D7C-2DE37966F55B')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('37A34EE5-4DAD-4B0E-8A8B-427DAD5CE371', 'Hron č. 9b (H)', '3-1111-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '00949F21-53CF-418B-A5F5-0E5201292E7D')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('5E51447D-290B-4074-8148-29DDC4470D28', 'Hron č. 11a (H)', '3-1130-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '172C352F-7FF5-4573-9382-693E07C53B83')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('D8AA6BE6-BF83-49A9-AED7-B9C78F43C877', 'Hron č. 11b (H)', '3-1131-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '45397F08-555F-4C97-B819-DF1876A699B0')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('9C3CB2EE-6944-42A7-BE76-CAC6F067ED39', 'Rohozná', '3-3350-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '1F5C7649-8EF6-486A-87B8-43967CA93EB6')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, chap, geo_coordinates_guid) VALUES ('B2BD8EA7-525F-4D36-B5C2-A446AE2AC57D', 'Bystrica č. 1a', '3-0380-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 1, '26221CD1-562C-468C-90F1-98CB15F43DC9')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('32AC227A-6AD5-432F-BE01-092AE4C20EB6', 'Bystrica č. 1b', '3-0381-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '8BAFC4B6-106B-49AF-92DA-D79C5966BAF4')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('F8971821-1C2C-4626-B68E-22BD4B8A62A5', 'Čierňanka č. 1', '3-0540-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'D221527A-0EDF-4775-A620-71156D1AD490')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('C73C5B4F-B54D-4BF3-93BE-2D04EAB003A7', 'Orava č. 1a (H)', '3-2710-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '2EF77059-7365-4EB7-953D-DE2A959DC997')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('870B24DD-E02F-4588-9530-3E8CF6F72874', 'Udava č. 1', '4-3020-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'C65A3B6D-D2B0-401D-8C2B-A01AAD4AF38A')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('B6CBF321-5FC5-40B7-AB0C-45CCAE9AE286', 'Poprad č. 4 (H)', '4-1970-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '7C2CE5AC-7805-4360-90EA-3A3A72E155EF')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('463032E1-0207-419B-BB68-C548A6F8BCF8', 'Turiec č. 1b (H)', '3-4431-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '204433E0-5C04-4EB6-855D-C9EB5874DBAA')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('B6854EC8-8ECE-4185-A5DD-D023EDD6D66F', 'Váh č. 16 (H)', '3-4650-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'C58407A4-70C7-49B8-83B9-8456FBD0777A')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('F20732AC-10D5-4158-B273-8B58D99429D7', 'Biela Orava č. 2', '3-0080-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '8959723D-6A79-4E41-8959-7CE10E50C5B6')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('16838F43-5498-4C7C-8A99-93FE9A37A9C0', 'Poprad č. 2a (H)', '4-1950-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '95165462-D892-47DC-9411-CDE34BDD7172')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('ACEF3C71-7087-48FA-96CF-DA96D554806C', 'Poprad č. 2c (H)', '4-1952-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '244C1EBF-F018-47D8-8645-ADEF3056303C')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('995BC028-C8C8-4B88-82FD-D480CE2F4767', 'Čierny Hron č. 1 (H)', '3-0560-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '357EE221-E036-4660-B3E4-95D8AB919EBF')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('6F9D879A-E3AD-4A7E-A172-9312C3D903DB', 'Hron č. 10a (H)', '3-1120-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '19A9DAEB-A9BC-4D52-98FD-CAE20B174105')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('1960F764-C47C-4A76-961C-7192FB84F9F9', 'Hron č. 10b (H)', '3-1121-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'B3000337-9974-4063-8C31-AED43B5A5D08')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, chap, geo_coordinates_guid) VALUES ('FBD358CC-B5D8-494B-8E5A-B2FFF951D094', 'Orava č. 2a (H)', '3-2720-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 1, '44621313-1147-4BA5-859C-9B4023C3C9E8')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('1330A168-6F37-408A-A90D-0A511FC413E3', 'Orava č. 2b (H)', '3-2721-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'DCD627FA-EAB5-4C8E-9485-EABA345162B1')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('63BB2B7A-B49B-4585-B703-AE6203949E2B', 'Rimava č. 3', '3-3270-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '1322D381-BD29-487D-A0E9-2FFB1B5A0C26')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('535A7EDB-1134-410D-B6F5-BB00A641D452', 'Váh č. 18a (H)', '3-4680-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '86BB526C-F866-4FC6-876F-174A30D02CBF')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('681F689E-AB4D-4969-93A2-871051D21F27', 'Poprad č. 5 (H)', '4-1980-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '90A53353-A183-4DCF-A3B5-A13626090F0C')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('87855189-97AD-4405-93B3-1239CE843A18', 'Dunajec č. 2 (H)', '4-0430-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'ADD86749-2B1C-46ED-B999-9917DD45A2D2')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, chap, geo_coordinates_guid) VALUES ('6F7D2B47-11B6-43B4-825B-5E6F1415F1CD', 'Poprad č. 3b (H)', '4-1961-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 1, '53CFE309-8D15-41A4-B655-AAA473B881C2')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('F2175BEA-D92D-4A5A-82E4-CC8CAF97289D', 'Poprad č. 3a (H)', '4-1960-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'D7314F14-254B-460D-9466-B602EE3C29F3')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('C2570FD7-5491-4FF0-AB2A-F6A649054630', 'Poprad č. 3c (H)', '4-1962-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '9029D564-FF15-4F22-8BF6-86B4EE2869C6')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('49BBF884-9896-4A8C-B847-A21567922076', 'Poprad č. 1 (H)', '4-1940-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '9A95BD5B-B550-4A7A-81C3-00E6D9C9F791')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('230F41CB-8ECC-4442-8D55-73F83E14F4CB', 'Orava č. 3 (H)', '3-2730-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '5F9D625D-8449-406A-A252-A35281702B40')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('9D470533-0A5C-44E5-A477-ECFDAA094D12', 'Turiec č. 2 (H)', '3-4480-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '5D8B7AC2-118C-47CC-8FA9-A6E41121CFA7')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('6EB3D99B-F42B-40D3-9E26-7E222088E7D5', 'Ondava č. 3', '4-1670-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '279310F6-C919-48D8-AAE4-D5BDA699E7E5')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('4AEC3BD9-E457-4517-9FB0-31E29518CC61', 'Hron č. 7b (H)', '3-1081-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '9B00B83E-C381-40E5-B192-0301DA0901FE')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('FBEE85C0-48ED-40C6-A9FF-123CE1461372', 'Rajčanka č. 1', '3-3130-5', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '212A68CE-5F28-48BD-B35C-7A306BB7BC28')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('7A9AFACF-1E8D-4A81-B9C7-0C41938334C4', 'Váh č. 15 (H)', '3-4640-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '9D83E20A-4BCA-4870-B02E-78D60897FF19')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid, chap) VALUES ('FF35F45A-2969-41AA-8CDC-F1C0E6D89B20', 'Váh č.20', '3-4710-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'DD3216D6-540F-4580-8901-CBDCBB6E5E70', 1)")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('8D104B35-E57A-452F-BFD2-EEF970D7EA5D', 'Poprad č. 2b (H)', '4-1951-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', 'BB46C049-5815-4079-A6EC-4A8F747C8717')")
        executeInsertStatement(db, "INSERT INTO tbl_area (guid, name, area_id, area_type_guid, geo_coordinates_guid) VALUES ('2039CA09-B2CC-4128-A638-9CF7E126BB87', 'Turiec č. 1 (H)', '3-4430-6', 'D780545C-BBB4-4A73-8BF6-97400B2797C8', '6E1FCE15-17D8-44F1-9757-FE4DD960D9C6')")
    }
}