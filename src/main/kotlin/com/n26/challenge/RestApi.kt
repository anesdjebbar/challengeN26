package com.n26.challenge

import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.time.Instant
import java.time.ZoneOffset
import java.util.logging.Logger

/**
 * Api used to manage the two endpoints.
 */

@Controller
class RestApi {

    private val logger = Logger.getGlobal()

    /**
     * /statistics endpoint managing function
     * return the statistics in JSON Object.
     */
    @RequestMapping(value = "statistics",
            method = arrayOf(RequestMethod.GET),
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun getStatistics() : ResponseEntity<Stats> {
        return ResponseEntity
                .ok(Statistics.generateStatistics())
    }


    /**
     * /transactions endpoint managing function
     */
    @RequestMapping(value = "transactions",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE),
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun postTransation(@RequestBody transaction : String) : ResponseEntity<HttpStatus>{
        return try{
            val jsonTransaction  = JSONObject(transaction)
            val _amount = jsonTransaction.getDouble("amount")
            val _timestamp = jsonTransaction.getLong("timestamp")
            managePostTransaction(_amount,_timestamp)
        }catch (ex : Exception){
            logger.info(ex.message)
            when(ex){
                is InvalidTransactionException -> return ResponseEntity(HttpStatus.NO_CONTENT)
                else -> ResponseEntity(HttpStatus.BAD_REQUEST)
            }
        }
    }

    /**
     * Manage the incomming transactions.
     * If the transaction is older than 60 seconds then an @InvalidTransactionException is raised
     * and a 204 Http Status is returned, and the transaction is not saved.
     * If the transaction is not older than 60 seconds then a 201 Http Status is returned, and the transaction is saved.
     * If any exception is raised a 204 status is returned
     */
    fun managePostTransaction(_amount : Double, _timestamp : Long) : ResponseEntity<HttpStatus>{
        return try  {

            // converts the time-stamp parameter to epoch seconds
            val timestamp 	= Instant
                    .ofEpochMilli(_timestamp)
                    .atZone(ZoneOffset.UTC)
                    .toEpochSecond()

            // save the transaction
            Statistics.addNewTransaction(_amount,timestamp)

            // create a time comparator in seconds.
            val comparator 	= Statistics.createTimeComparator();
            //checks if the transaction's time-stamp is older than 60 seconds based on the created comparator.
            if(Statistics.isTimestampValid(timestamp,comparator)) {
                // time-stamp < 60 seconds
                // return successful response.
                ResponseEntity(HttpStatus.CREATED)
            }else{
                //time-stamp older than 60 seconds.
                // raise exception.
                throw InvalidTransactionException("Transaction older than 60 seconds.");
            }
        }catch(ex  : Exception){
            logger.info(ex.message)
            ResponseEntity(HttpStatus.NO_CONTENT)
        }
    }


}