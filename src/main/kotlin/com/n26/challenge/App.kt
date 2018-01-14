package com.n26.challenge

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Entry point of the program.
 */
@SpringBootApplication
open class App

fun main(args : Array<String>){
    SpringApplication.run(App::class.java, *args)
}

