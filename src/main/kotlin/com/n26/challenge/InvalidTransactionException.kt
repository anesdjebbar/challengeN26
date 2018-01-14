package com.n26.challenge

/**
 * Custom Exception, Raised when a transaction is older than 60 seconds.
 */
class InvalidTransactionException : Exception {
    constructor() : super()
    constructor (message : String ) : super(message)
}