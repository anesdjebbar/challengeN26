import java.time.Instant

/**
 * Helper class to generates samples for the test class SeverN26Tests.
 */
class SampleHelper {
    var seedAmount  = 0.1
    var max = 100

    fun CURRENT_DATE() = Instant
            .now()
            .toEpochMilli()

    /**
     * function generating 100 hundred transactions.
     */
    fun getSamples() : List<Pair<Double,Long>>{
        var count = 0
        var samples = mutableListOf<Pair<Double,Long>>()
        while (count < max){
            count++
            samples.add(Pair<Double,Long>(seedAmount++,CURRENT_DATE()))
        }
        return samples
    }

}