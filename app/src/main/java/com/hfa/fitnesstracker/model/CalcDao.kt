package com.hfa.fitnesstracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CalcDao {
    @Insert
    fun insert(calc: Calc)

    @Update
    fun update(calc: Calc)

    @Query("SELECT * FROM Calc WHERE type = :type")
    fun getRegisterByType(type: String) : List<Calc>

    @Delete
    fun deleteRegister(calc: Calc) : Int
}