package com.n26.challenge


/**
 * CLass used to act like a Database.
 * Data are not persisted and then deleted when the program is closed.
 */
class LiveDataBase  private constructor(){
    private object Holder {val INSTANCE = LiveDataBase()}
    companion object {
        val intance : LiveDataBase by lazy { Holder.INSTANCE }
    }
    var ALL_TRANSACTIONS = mutableMapOf<Long,Stats>()

}

/**
 * Data class representing a Statistic object.
 */
data class Stats(var min : Double = 0.0, var max : Double = 0.0, var count : Long = 0, var sum : Double = 0.0, var avg : Double = 0.0)