package com.example.candycrush.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.candycrush.Joya
import com.example.candycrush.R

class Tablero(context: Context?, width: Int) {
    var filas = 7
    var columnas = 8
    var ancho = width / columnas
    var foolimg = Joya(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources, R.drawable.fool_symbol), ancho, ancho, true))
    var doorImg = Joya(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources, R.drawable.door_symbol), ancho, ancho, true))
    var visionaryImg = Joya(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources, R.drawable.visionary_symbol), ancho, ancho, true))
    private var tyrantImg = Joya(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources, R.drawable.tyrant_symbol), ancho, ancho, true))
    var sunSymbol = Joya(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources, R.drawable.sun_symbol), ancho, ancho, true))
    private var deathSymbol = Joya(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources, R.drawable.death_symbol), ancho, ancho, true))
    var lista = listOf(foolimg, doorImg, visionaryImg, tyrantImg, sunSymbol, deathSymbol)

    // matriz llena de números del 1 al 6
    var matriz: Array<Array<Joya>> = arrayOf(
        arrayOf(rand(), rand(), rand(), rand(), rand(), rand(), rand(), rand()),
        arrayOf(rand(), rand(), rand(), rand(), rand(), rand(), rand(), rand()),
        arrayOf(rand(), rand(), rand(), rand(), rand(), rand(), rand(), rand()),
        arrayOf(rand(), rand(), rand(), rand(), rand(), rand(), rand(), rand()),
        arrayOf(rand(), rand(), rand(), rand(), rand(), rand(), rand(), rand()),
        arrayOf(rand(), rand(), rand(), rand(), rand(), rand(), rand(), rand()),
        arrayOf(rand(), rand(), rand(), rand(), rand(), rand(), rand(), rand())
    )

    fun rand(): Joya {
        val randomJoya = lista[(0..5).random()]
        val newBitmap = Bitmap.createBitmap(randomJoya.getImg())
        val newJoya = Joya(newBitmap)
        return newJoya
    }
}