package com.n26.challenge

import java.time.Instant
import java.time.ZoneOffset

/**
 * Class used to computes the statistics and manage the new transactions
 */
class Statistics {

    companion object{

        val SIXTY_SEC = 60

        /**
         * Compares if a date (@timestampSeconds) is within a 60 seconds range in the past based on a second date(@comparatorInSecond)
         * @timestampSeconds
         * @comparatorInSecond
         */
        fun isTimestampValid(timestampInSeconds : Long, comparatorInSecond : Long)
                = (comparatorInSecond - SIXTY_SEC) <= timestampInSeconds && timestampInSeconds <= comparatorInSecond

        /**
         * Returns the current time in epoch milliseconds to have a reference
         * during date comparison.
         */
        fun createTimeComparator() = Instant
                .now()
                .atZone(ZoneOffset.UTC)
                .toEpochSecond()

        /**
         * Adds a new transaction in the list of the transactions
         * Locks the ressource LiveDataBase.intance.ALL_TRANSACTIONS
         */
        fun addNewTransaction(amount : Double, timestamp : Long){

            //Lock
            synchronized(LiveDataBase.intance.ALL_TRANSACTIONS){
                var stat = LiveDataBase.intance.ALL_TRANSACTIONS.get(timestamp)
                if(stat != null ){
                    stat.count++
                    stat.sum += amount
                    if(amount > stat.max){
                        stat.max = amount
                    }else if(amount < stat.min){
                        stat.min = amount
                    }
                }else{
                    stat = Stats(amount,amount,1,amount,amount)
                }
                LiveDataBase.intance.ALL_TRANSACTIONS.put(timestamp,stat)
            }

        }

        /**
         * Generates the statistics.
         * Complexity O(1)
         */
        fun generateStatistics() : Stats{

            val timeComparator = createTimeComparator()
            var maxValue = 0.0
            var count : Long = 0
            var sum  = 0.0
            var minValue = 0.0
            var avg  = 0.0
            synchronized(LiveDataBase.intance.ALL_TRANSACTIONS){
                var index = timeComparator
                var firstTime = true
                while (index > timeComparator - SIXTY_SEC){
                    val stat = LiveDataBase.intance.ALL_TRANSACTIONS.get(index)
                    if(stat != null){
                        if(firstTime){
                            minValue = stat.min
                            firstTime = false
                        }
                        count += stat.count
                        sum += stat.sum
                        if( stat.max > maxValue){
                            maxValue = stat.max
                        } else if(stat.min < minValue){
                            minValue = stat.min
                        }
                    }
                    index--
                }
                if(count > 0) {
                    avg = sum / count
                }
            }
         return Stats(minValue,maxValue,count,sum,avg)
        }
    }
}

