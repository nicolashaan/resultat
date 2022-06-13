package fr.haan.resultat

/**
 * Test suite for [fr.haan.resultat.Resultat] class, a fork of [kotlin.Result] with Loading state.
 * Copyright 2022 Nicolas Haan.
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import kotlin.test.*


internal class ResultatTest {

    class CustomException(message: String) : Exception(message) {
        override fun toString(): String = "CustomException: $message"
    }

    fun error(message: String): Nothing = throw CustomException(message)

    @Test
    fun testRunCatchingSuccess() {
        val ok = runCatchingL { "OK" }
        checkSuccess(ok, "OK", true)
    }

    @Test
    fun testRunCatchingFailure() {
        val fail = runCatchingL { error("F") }
        checkFailure(fail, "F", true)
    }

    @Test
    fun testConstructedSuccess() {
        val ok = Resultat.success("OK")
        checkSuccess(ok, "OK", true)
    }

    @Test
    fun testConstructedFailure() {
        val fail = Resultat.failure<Unit>(CustomException("F"))
        checkFailure(fail, "F", true)
    }

    @Test
    fun testConstructedLoading() {
        val fail = Resultat.loading<Unit>()
        checkLoading(fail,  true)
    }

    @Test
    fun testConversionToResult() {
        val success: Resultat<String> = Resultat.success("Hello")
        val successResult: Result<String>? = success.toResult()
        assertIs<Result<String>>(successResult)
        assertTrue(successResult.isSuccess)

        val loading: Resultat<String> = Resultat.loading()
        val loadingResult: Result<String>? = loading.toResult()
        assertNull(loadingResult)

        val failure: Resultat<String> = Resultat.failure(Throwable("Fake error"))
        val failureResult: Result<String>? = failure.toResult()
        assertIs<Result<String>>(failureResult)
        assertTrue(failureResult.isFailure)
    }

    @Test
    fun testConversionFromResult() {
        val success: Result<String> = Result.success("Hello")
        val successResult: Resultat<String> = success.toResultat()
        assertIs<Resultat<String>>(successResult)
        assertIs<Resultat.Success<String>>(successResult)
        assertTrue(successResult.isSuccess)

        val failure: Result<String> = Result.failure(Throwable("Fake error"))
        val failureResult: Resultat<String> = failure.toResultat()
        assertIs<Resultat<String>>(failureResult)
        assertIs<Resultat.Failure>(failureResult)
        assertTrue(failureResult.isFailure)
    }

    private fun <T> checkSuccess(ok: Resultat<T>, v: T, topLevel: Boolean = false) {
        assertTrue(ok.isSuccess)
        assertFalse(ok.isFailure)
        assertFalse(ok.isLoading)
        assertEquals(v, ok.getOrThrow())
        assertEquals(v, ok.getOrElse { throw it })
        assertEquals(v, ok.getOrNull())
        assertEquals(v, ok.getOrElse { null })
        assertEquals(v, ok.getOrDefault("DEF"))
        assertEquals(v, ok.getOrElse { "EX:$it" })
        assertEquals("V:$v", ok.fold({ "V:$it" }, { "EX:$it" }, { "L" }))
        assertEquals(null, ok.exceptionOrNull())
        assertEquals(null, ok.fold(onSuccess = { null }, onFailure = { it }, onLoading = { null }))
        assertEquals("Success($v)", ok.toString())
        assertEquals(ok, ok)
        if (topLevel) {
            checkSuccess(ok.map { 42 }, 42)
            checkSuccess(ok.mapCatching { 42 }, 42)
            checkFailure(ok.mapCatching { error("FAIL") }, "FAIL")
            checkSuccess(ok.recover { 42 }, "OK")
            checkSuccess(ok.recoverCatching { 42 }, "OK")
            checkSuccess(ok.recoverCatching { error("FAIL") }, "OK")
        }
        var sCnt = 0
        var fCnt = 0
        var lCnt = 0
        assertEquals(ok, ok.onSuccess { sCnt++ })
        assertEquals(ok, ok.onFailure { fCnt++ })
        assertEquals(ok, ok.onLoading { lCnt++ })
        assertEquals(1, sCnt)
        assertEquals(0, fCnt)
        assertEquals(0, lCnt)
    }

    private fun <T> checkFailure(fail: Resultat<T>, msg: String, topLevel: Boolean = false) {
        assertFalse(fail.isSuccess)
        assertTrue(fail.isFailure)
        assertFalse(fail.isLoading)
        assertFails { fail.getOrThrow() }
        assertFails { fail.getOrElse { throw it } }
        assertEquals(null, fail.getOrNull())
        assertEquals(null, fail.getOrElse { null })
        assertEquals("DEF", fail.getOrDefault("DEF"))
        assertEquals("EX:CustomException: $msg", fail.getOrElse { "EX:$it" })
        assertEquals("EX:CustomException: $msg", fail.fold({ "V:$it" }, { "EX:$it" }, { "L" }))
        assertEquals(msg, fail.exceptionOrNull()!!.message)
        assertEquals(msg, fail.fold(onSuccess = { null }, onFailure = { it }, onLoading = { null })!!.message)
        assertEquals("Failure(CustomException: $msg)", fail.toString())
        assertEquals(fail, fail)
        if (topLevel) {
            checkFailure(fail.map { 42 }, msg)
            checkFailure(fail.mapCatching { 42 }, msg)
            checkFailure(fail.mapCatching { error("FAIL") }, msg)
            checkSuccess(fail.recover { 42 }, 42)
            checkSuccess(fail.recoverCatching { 42 }, 42)
            checkFailure(fail.recoverCatching { error("FAIL") }, "FAIL")
        }
        var sCnt = 0
        var fCnt = 0
        var lCnt = 0
        assertEquals(fail, fail.onSuccess { sCnt++ })
        assertEquals(fail, fail.onFailure { fCnt++ })
        assertEquals(fail, fail.onLoading { lCnt++ })
        assertEquals(0, sCnt)
        assertEquals(1, fCnt)
        assertEquals(0, lCnt)
    }

    private fun <T> checkLoading(load: Resultat<T>, topLevel: Boolean = false) {
        assertFalse(load.isSuccess)
        assertFalse(load.isFailure)
        assertTrue(load.isLoading)
        assertFails {  load.getOrThrow() }
        assertFails { load.getOrThrow() }
        assertFails { load.getOrElse { throw it } }
        assertEquals(null, load.getOrNull())
        assertEquals(null, load.getOrElse { null })
        assertEquals("DEF", load.getOrDefault("DEF"))
        assertEquals("L", load.getOrElse { "L" })
        assertEquals("L", load.fold({ "V:$it" }, { "EX:$it" }, { "L" }))
        assertEquals(null, load.exceptionOrNull())
        assertEquals(null, load.fold(onSuccess = { "V" }, onFailure = { it }, onLoading = { null }))
        assertEquals("Loading", load.toString())
        assertEquals(load, load)
        if (topLevel) {
            checkLoading(load.map { 42 },)
            checkLoading(load.mapCatching { 42 })
            checkLoading(load.mapCatching { error("FAIL") })
            checkLoading(load.recover { 42 })
            checkSuccess(load.recover(recoverLoading = true) { 42 },42)
            checkLoading(load.recoverCatching { 42 })
            checkSuccess(load.recoverCatching(recoverLoading = true) { 42 },42)
            checkLoading(load.recoverCatching { error("FAIL") })
        }
        var sCnt = 0
        var fCnt = 0
        var lCnt = 0
        assertEquals(load, load.onSuccess { sCnt++ })
        assertEquals(load, load.onFailure { fCnt++ })
        assertEquals(load, load.onLoading { lCnt++ })
        assertEquals(0, sCnt)
        assertEquals(0, fCnt)
        assertEquals(1, lCnt)
    }
}
