package ru.netology.nmedia.dto

import org.junit.Test

import org.junit.Assert.*

class NumbersToStringKtTest {

    @Test
    fun numbersToString() {
        val num = 156000
        val result:String =numbersToString(num)
        assertEquals("156K",result)
    }
    @Test
    fun numbersToString_erros() {
        val num = 15600
        val result:String =numbersToString(num)
        assertNotEquals("156K",result)
    }

}