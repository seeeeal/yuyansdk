package com.yuyan.imemodule.database.table

/**
 * 常用字符表
 * jianghuan
 */
object UsedCharacterTable {
    //============== 表名 ==============
    const val TABLE_NAME = "used_chararcter_table" //表名
    const val CHARACTER = "character" //操作标识
    const val LATEST_TIME = "latest_time" //最后使用时间

    //============== 构造表的语句 ==============
    const val CREATE_TABLE = ("create table if not exists " + TABLE_NAME + " ("
            + CHARACTER + " text,"
            + LATEST_TIME + " text default '0')")
}
