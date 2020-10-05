package me.skean.framework.example.db.dao

import androidx.room.*
import io.reactivex.Observable
import io.reactivex.Single
import me.skean.framework.example.db.entity.Dummy
import me.skean.framework.example.db.pojo.NameCount

/**
 * 测试的Dao
 */
@Dao
interface DummyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(vararg items: Dummy?): Single<List<Long>>

    @Delete
    fun delete(dummy: Dummy?): Single<Int>

    @Update
    fun update(item: Dummy?): Single<Int>

    @Query("SELECT * FROM Dummy")
    fun findAll(): Observable<List<Dummy>>

    @Query("SELECT * FROM Dummy WHERE id IN (:ids)")
    fun findAllById(ids: List<Long?>?): Observable<List<Dummy>>

    @Query("SELECT * FROM Dummy WHERE full_name LIKE :name")
    fun findByName(name: String?): Observable<List<Dummy>>

    @Query("SELECT full_name, count(id) count FROM Dummy GROUP BY full_name")
    fun countName(): Observable<List<NameCount>>
}