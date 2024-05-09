package com.example.candycrush.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.candycrush.R
import kotlin.math.abs

class TableroView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var model: Tablero? = null
    private val paint = Paint()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        model = Tablero(context, w)
    }
    private var dragging = false
    private var dragX = 0f
    private var dragY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Check if the touch event is on an image
                val x = event.x.toInt()
                val y = event.y.toInt()
                val board = model
                val ancho = width / board!!.columnas
                val alto = width / board.columnas
                for (i in 0 until board.filas) {
                    for (j in 0 until board.columnas) {
                        val centerX = (j * ancho + ancho / 2).toFloat()
                        val centerY = ((i + 3) * alto + alto / 2).toFloat()
                        if (x in (centerX - ancho / 2).toInt()..(centerX + ancho / 2).toInt() &&
                            y in (centerY - alto / 2).toInt()..(centerY + alto / 2).toInt()
                        ) {
                            model!!.matriz[i][j].dragging = true
                            dragging = true
                            dragX = centerX
                            dragY = centerY
                            return true
                        }
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (dragging) {
                    // Update the position of the image
                    dragX = event.x
                    dragY = event.y
                    invalidate() // Redraw the view
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                for (i in 0 until model!!.filas) {
                    for (j in 0 until model!!.columnas) {
                        model!!.matriz[i][j].dragging = false
                    }
                }
                dragging = false
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        verificarCombo()
        val board = model
        val ancho = width / board!!.columnas
        val alto = width / board.columnas

        for (i in 1 until board.filas) {
            for (j in 0 until board.columnas) {
                if (!board.matriz[i][j].dragging && !board.matriz[i][j].desaparecer) {
                    var img = board.lista[board.matriz[i][j].num]
                    val centerX = (j * ancho + ancho / 2).toFloat()
                    val centerY = ((i + 3) * alto + alto / 2).toFloat()
                    val imageX = centerX - img.width / 2
                    val imageY = centerY - img.height / 2
                    canvas.drawBitmap(
                        img,
                        imageX,
                        imageY,
                        paint
                    )
                } else if (board.matriz[i][j].dragging && !board.matriz[i][j].desaparecer) {
                    // Draw the image at the new position
                    var img = board.lista[board.matriz[i][j].num]
                    var moveX = dragX - img.width / 2
                    var moveY = dragY - img.height / 2
                    val centerX = (j * ancho + ancho / 2).toFloat()
                    val centerY = ((i + 3) * alto + alto / 2).toFloat()
                    val imageX = centerX - img.width / 2
                    val imageY = centerY - img.height / 2
                    var movAbsX= abs(dragX - centerX)
                    var movAbsY= abs(dragY - centerY)
                    if (movAbsX > movAbsY) {
                        if (movAbsX > img.width){
                            if(dragX > centerX){
                                moveX = centerX + (img.width/2)
                                var temp = model!!.matriz[i][j]
                                model!!.matriz[i][j] = model!!.matriz[i][j+1]
                                model!!.matriz[i][j+1] = temp
                                if (!verificarCombo()){
                                    model!!.matriz[i][j+1] = model!!.matriz[i][j]
                                    model!!.matriz[i][j] = temp
                                }
                            }
                            else{
                                moveX = centerX - (img.width +(img.width/2))
                                var temp = model!!.matriz[i][j]
                                model!!.matriz[i][j] = model!!.matriz[i][j-1]
                                model!!.matriz[i][j-1] = temp
                                if (!verificarCombo()){
                                    model!!.matriz[i][j-1] = model!!.matriz[i][j]
                                    model!!.matriz[i][j] = temp
                                }
                            }
                        }
                        canvas.drawBitmap(
                            img,
                            moveX,
                            imageY,
                            paint
                        )
                    }else{
                        if (movAbsY > img.height){
                            if(dragY > centerY){
                                moveY = centerY + (img.height/2)
                                var temp = model!!.matriz[i][j]
                                model!!.matriz[i][j] = model!!.matriz[i+1][j]
                                model!!.matriz[i+1][j] = temp
                                if (!verificarCombo()){
                                    model!!.matriz[i+1][j] = model!!.matriz[i][j]
                                    model!!.matriz[i][j] = temp
                                }
                            }
                            else{
                                moveY = centerY - (img.height +(img.height/2))
                                var temp = model!!.matriz[i][j]
                                model!!.matriz[i][j] = model!!.matriz[i-1][j]
                                model!!.matriz[i-1][j] = temp
                                if (!verificarCombo()){
                                    model!!.matriz[i-1][j] = model!!.matriz[i][j]
                                    model!!.matriz[i][j] = temp
                                }
                            }
                        }
                        canvas.drawBitmap(
                            img,
                            imageX,
                            moveY,
                            paint
                        )
                    }
                }
            }
        }
    }
    fun verificarCombo(): Boolean{
        var lista = model!!.matriz
        var cambio = false
        var cordenadas = arrayOf<Pair<Int,Int>>()
        for (i in 1 until model!!.filas) {
            for (j in 0 until model!!.columnas) {

                if (i < model!!.filas - 2) {
                    if ((lista[i][j].equals(lista[i + 1][j]) && lista[i][j].equals(lista[i + 2][j]))&& !lista[i][j].desaparecer) {
                        model!!.matriz[i][j].desaparecer = true
                        model!!.matriz[i+1][j].desaparecer = true
                        model!!.matriz[i+2][j].desaparecer = true
                        cambio = true
                        actualizarTablero()
                    }
                }
                if (j < model!!.columnas - 2) {
                    if ((lista[i][j].equals(lista[i][j + 1]) && lista[i][j].equals(lista[i][j + 2]))&& !lista[i][j].desaparecer) {
                        model!!.matriz[i][j].desaparecer = true
                        model!!.matriz[i][j+1].desaparecer = true
                        model!!.matriz[i][j+2].desaparecer = true
                        cambio = true
                        actualizarTablero()
                    }
                }
            }
        }
        return cambio
    }

    fun actualizarTablero(){
        var lista = model!!.matriz
        for (i in 0 until model!!.filas) {
            for (j in 0 until model!!.columnas) {
                if (lista[i][j].desaparecer) {
                    actualizarFila(i,j)
                }
            }
        }
    }
    fun actualizarFila(i: Int, j: Int){
        if(i == 0){
            model!!.matriz[i][j] = model!!.rand()
            return
        }
        model!!.matriz[i][j] = model!!.matriz[i-1][j]
        actualizarFila(i-1,j)
    }

}