package com.example.candycrush.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.candycrush.R
import com.example.candycrush.Tipo
import kotlin.math.abs

class TableroView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var model: Tablero? = null
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 8f
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        model = Tablero(context, w)
        verificarCombo()
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
                var img = board.lista[board.matriz[i][j].num]
                // Se calcula la posición de la imagen de donde se movio
                var moveX = dragX - img.width / 2
                var moveY = dragY - img.height / 2
                // Se calcula el centro de la imagen
                val centerX = (j * ancho + ancho / 2).toFloat()
                val centerY = ((i + 3) * alto + alto / 2).toFloat()
                // Se calcula la posicion inicial de x y y de la imagen
                val imageX = centerX - img.width / 2
                val imageY = centerY - img.height / 2
                // Se calcula el movimiento absoluto en x y y
                val movAbsX= abs(dragX - centerX)
                val movAbsY= abs(dragY - centerY)

                // Se verifica el tipo de joya para cambiar la imagen
                if(board.matriz[i][j].tipo == Tipo.FUEGO){
                    img = crearFuego(img)
                }
                if(board.matriz[i][j].tipo == Tipo.RAYO){
                    img = crearRayo(img)
                }
                if(board.matriz[i][j].tipo == Tipo.CUBO){
                    img = crearCubo(ancho)
                }

                // Si no se esta moviendo se dibuja la imagen en su posición
                if (!board.matriz[i][j].dragging && !board.matriz[i][j].desaparecer) {
                    canvas.drawBitmap(
                        img,
                        imageX,
                        imageY,
                        paint
                    )
                    // Si se esta moviendo se dibuja la imagen en la nueva posición
                } else if (board.matriz[i][j].dragging && !board.matriz[i][j].desaparecer) {
                    //Se verifica si el movimiento es horizontal o vertical
                    //Vertical
                    if (movAbsX > movAbsY) {
                        //Se verifica si el movimiento supera el tamaño de la imagen para intercambiar las posiciones
                        if (movAbsX > img.width){
                            //Se verifica si el movimiento es hacia la derecha o izquierda
                            //Derecha
                            if(dragX > centerX){
                                //Se calcula la nueva posición de la imagen
                                moveX = centerX + (img.width/2)
                                //Se intercambian las posiciones de las joyas
                                val temp = model!!.matriz[i][j]
                                model!!.matriz[i][j] = model!!.matriz[i][j+1]
                                model!!.matriz[i][j+1] = temp
                                //Se verifica si se puede realizar el movimiento
                                if (!verificarMovimiento(i,j+1)){
                                    //Si no se puede se regresan las posiciones
                                    model!!.matriz[i][j+1] = model!!.matriz[i][j]
                                    model!!.matriz[i][j] = temp
                                }
                            }
                            //Izquierda
                            else{
                                moveX = centerX - (img.width +(img.width/2))
                                val temp = model!!.matriz[i][j]
                                model!!.matriz[i][j] = model!!.matriz[i][j-1]
                                model!!.matriz[i][j-1] = temp
                                if (!verificarMovimiento(i,j-1)){
                                    model!!.matriz[i][j-1] = model!!.matriz[i][j]
                                    model!!.matriz[i][j] = temp
                                }
                            }
                        }
                        //Se dibuja la imagen en la nueva posición
                        canvas.drawBitmap(
                            img,
                            moveX,
                            imageY,
                            paint
                        )
                        //Vertical
                    }else{
                        //Se verifica si el movimiento supera el tamaño de la imagen para intercambiar las posiciones
                        if (movAbsY > img.height){
                            //Se verifica si el movimiento es hacia abajo o arriba
                            //Abajo
                            if(dragY > centerY){
                                //Se calcula la nueva posición de la imagen
                                moveY = centerY + (img.height/2)
                                //Se intercambian las posiciones de las joyas
                                val temp = model!!.matriz[i][j]
                                model!!.matriz[i][j] = model!!.matriz[i+1][j]
                                model!!.matriz[i+1][j] = temp
                                if (!verificarMovimiento(i+1,j)){
                                    //Si no se puede se regresan las posiciones
                                    model!!.matriz[i+1][j] = model!!.matriz[i][j]
                                    model!!.matriz[i][j] = temp
                                }
                            }
                            //Arriba
                            else{
                                moveY = centerY - (img.height +(img.height/2))
                                val temp = model!!.matriz[i][j]
                                model!!.matriz[i][j] = model!!.matriz[i-1][j]
                                model!!.matriz[i-1][j] = temp
                                if (!verificarMovimiento(i-1,j)){
                                    model!!.matriz[i-1][j] = model!!.matriz[i][j]
                                    model!!.matriz[i][j] = temp
                                }
                            }
                        }
                        //Se dibuja la imagen en la nueva posición
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
    private fun verificarCombo(): Boolean{
        val lista = model!!.matriz
        var cambio = false
        for (i in 1 until model!!.filas) {
            for (j in 0 until model!!.columnas) {

                if(verificarMovimiento(i,j)) {
                    actualizarTablero()
                    continue
                }
            }
        }
        return cambio
    }

    private fun verificarMovimiento(i:Int,j:Int): Boolean {
        if(verificarRayo(i,j)) {
            actualizarTablero()
            return true
        }
        if(verificarCubo(i,j)) {
            actualizarTablero()
            return true
        }
        if(verificarFuego(i,j)) {
            actualizarTablero()
            return true
        }
        if(verificar3(i,j)) {
            actualizarTablero()
            return true
        }
        return false
    }
    private fun verificarRayo(i:Int,j:Int): Boolean {
        val lista = model!!.matriz
        var iMenor = i
        //Se retrocede a la joya igual que esta mas atras
        for(y in i-1 downTo 1) {
            if (lista[y][j].equals(lista[i][j]) && !lista[y][j].desaparecer) {
                iMenor = y
            } else {
                break
            }
        }
        var jMenor = j
        for(x in j-1 downTo 0) {
            // Se retrocede a la joya igual que esta mas atras
            if (lista[i][x].equals(lista[i][j]) && !lista[i][x].desaparecer) {
                jMenor = x
            } else {
                break
            }
        }
        if (iMenor + 2 < model!!.filas && jMenor + 2 < model!!.columnas) {
            if (lista[i][jMenor].equals(lista[i][jMenor + 1]) && lista[i][jMenor].equals(lista[i][jMenor + 2])){
                if (lista[iMenor][j].equals(lista[iMenor + 1][j]) && lista[iMenor][j].equals(lista[iMenor + 2][j])){
                    for (y in iMenor..iMenor + 2) {
                        if (y == i) {
                            //Se cambia la joya a tipo fuego
                            model!!.matriz[y][j].tipo = Tipo.RAYO
                        } else {
                            //Se desaparecen las joyas
                            model!!.matriz[y][j].desaparecer = true
                        }
                    }
                    for (x in jMenor..jMenor + 2) {
                        if (x == j) {
                            model!!.matriz[i][x].tipo = Tipo.RAYO
                        } else {
                            model!!.matriz[i][x].desaparecer = true
                        }
                    }
                    return true
                }
            }
        }
        return false
    }
    private fun verificarCubo(i:Int, j:Int): Boolean {
        val lista = model!!.matriz
        var iMenor = i
        //Se retrocede a la joya igual que esta mas atras
        for(y in i-1 downTo 1) {
            if (lista[y][j].equals(lista[i][j]) && !lista[y][j].desaparecer) {
                iMenor = y
            } else {
                break
            }
        }
            //Se verifica si hay 5 joyas iguales en linea vertical
            if (iMenor + 4 < model!!.filas) {
                if ((lista[iMenor][j].equals(lista[iMenor + 1][j]) && lista[iMenor][j].equals(lista[iMenor + 2][j]) && lista[iMenor][j].equals(
                        lista[iMenor + 3][j]
                    ) && lista[iMenor][j].equals(lista[iMenor + 4][j])) && !lista[iMenor][j].desaparecer
                ) {
                    for (y in iMenor..iMenor + 4) {
                        if (y == i) {
                            //Se cambia la joya a tipo cubo
                            model!!.matriz[y][j].tipo = Tipo.CUBO
                        } else {
                            //Se desaparecen las joyas
                            model!!.matriz[y][j].desaparecer = true
                        }
                    }
                    return true
                }
            }

        var jMenor = j
        for(x in j-1 downTo 0) {
            // Se retrocede a la joya igual que esta mas atras
            if (lista[i][x].equals(lista[i][j]) && !lista[i][x].desaparecer) {
                jMenor = x
            } else {
                break
            }
        }
            //Se verifica si hay 5 joyas iguales en linea horizontal
            if (jMenor + 4 < model!!.columnas) {
                if ((lista[i][jMenor].equals(lista[i][jMenor + 1]) && lista[i][jMenor].equals(lista[i][jMenor + 2]) && lista[i][jMenor].equals(
                        lista[i][jMenor + 3]
                    ) && lista[i][jMenor].equals(lista[i][jMenor + 4])) && !lista[i][jMenor].desaparecer
                ) {
                    for (x in jMenor..jMenor + 4) {
                        if (x == j) {
                            model!!.matriz[i][x].tipo = Tipo.CUBO
                        } else {
                            model!!.matriz[i][x].desaparecer = true
                        }
                    }
                    return true
                }
            }
        return false
    }

    private fun verificarFuego(i: Int,j: Int): Boolean {
        val lista = model!!.matriz
        var iMenor = i
        //Se retrocede a la joya igual que esta mas atras
        for (y in i - 1 downTo 1) {
            if (lista[y][j].equals(lista[i][j]) && !lista[y][j].desaparecer) {
                iMenor = y
            } else {
                break
            }
        }
        //Se verifica si hay 4 joyas iguales en linea vertical
        if (iMenor + 3 < model!!.filas) {
            if ((lista[iMenor][j].equals(lista[iMenor + 1][j]) && lista[iMenor][j].equals(lista[iMenor + 2][j]) && lista[iMenor][j].equals(lista[iMenor + 3][j])) && !lista[iMenor][j].desaparecer) {
                for (y in iMenor..iMenor + 2) {
                    if (y == i) {
                        //Se cambia la joya a tipo fuego
                        model!!.matriz[y][j].tipo = Tipo.FUEGO
                    } else {
                        //Se desaparecen las joyas
                        model!!.matriz[y][j].desaparecer = true
                    }
                }
                return true
            }
        }
        var jMenor = j
        for (x in j - 1 downTo 0) {
            // Se retrocede a la joya igual que esta mas atras
            if (lista[i][x].equals(lista[i][j]) && !lista[i][x].desaparecer) {
                jMenor = x
            } else {
                break
            }
        }
        //Se verifica si hay 4 joyas iguales en linea horizontal
        if (jMenor + 3 < model!!.columnas) {
            if ((lista[i][jMenor].equals(lista[i][jMenor + 1]) && lista[i][jMenor].equals(lista[i][jMenor + 2]) && lista[i][jMenor].equals(lista[i][jMenor + 3])) && !lista[i][jMenor].desaparecer) {
                for (x in jMenor..jMenor + 2) {
                    if (x == j) {
                        model!!.matriz[i][x].tipo = Tipo.FUEGO
                    } else {
                        model!!.matriz[i][x].desaparecer = true
                    }
                }
                return true
            }
        }
        return false
    }

    private fun verificar3(i: Int,j: Int): Boolean {
        val lista = model!!.matriz
        var iMenor = i
        //Se retrocede a la joya igual que esta mas atras
        for(y in i-1 downTo 1) {
            if (lista[y][j].equals(lista[i][j]) && !lista[y][j].desaparecer) {
                iMenor = y
            } else {
                break
            }
        }
            //Se verifica si hay 3 joyas iguales en linea vertical
            if (iMenor + 2 < model!!.filas) {
                if ((lista[iMenor][j].equals(lista[iMenor + 1][j]) && lista[iMenor][j].equals(lista[iMenor + 2][j])) && !lista[iMenor][j].desaparecer) {
                    for (y in iMenor..iMenor + 2) {
                        //Se desaparecen las joyas
                        model!!.matriz[y][j].desaparecer = true
                    }
                    return true
                }
            }
        var jMenor = j
        for(x in j-1 downTo 0) {
            // Se retrocede a la joya igual que esta mas atras
            if (lista[i][x].equals(lista[i][j]) && !lista[i][x].desaparecer) {
                jMenor = x
            } else {
                break
            }
        }
            //Se verifica si hay 3 joyas iguales en linea horizontal
            if (jMenor + 2 < model!!.columnas) {
                if ((lista[i][jMenor].equals(lista[i][jMenor + 1]) && lista[i][jMenor].equals(lista[i][jMenor + 2])) && !lista[i][jMenor].desaparecer) {
                    for (x in jMenor..jMenor + 2) {
                        model!!.matriz[i][x].desaparecer = true
                    }
                    return true
                }
            }
        return false
    }


    private fun actualizarTablero(){
        val lista = model!!.matriz
        for (i in 0 until model!!.filas) {
            for (j in 0 until model!!.columnas) {
                if (lista[i][j].desaparecer) {
                    actualizarFila(i,j)
                }
            }
        }
    }
    private fun actualizarFila(i: Int, j: Int){
        if(i == 0){
            model!!.matriz[i][j] = model!!.rand()
            return
        }
        model!!.matriz[i][j] = model!!.matriz[i-1][j]
        actualizarFila(i-1,j)
    }

    private fun crearFuego(originalBitmap: Bitmap): Bitmap {
        // Create a mutable bitmap with the same size as the original
        val fuego = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, originalBitmap.config)

        // Create a canvas to draw on the new bitmap
        val canvas = Canvas(fuego)

        // Draw a red rectangle on the entire canvas
        val paint = Paint().apply { color = Color.RED }
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

        // Draw the original bitmap on top of the red rectangle
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        return fuego
    }

    private fun crearRayo(originalBitmap: Bitmap): Bitmap {
        // Create a mutable bitmap with the same size as the original
        val rayo = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, originalBitmap.config)

        // Create a canvas to draw on the new bitmap
        val canvas = Canvas(rayo)

        // Draw a red rectangle on the entire canvas
        val paint = Paint().apply { color = Color.BLUE }
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

        // Draw the original bitmap on top of the red rectangle
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        return rayo
    }

    private fun crearCubo(ancho: Int): Bitmap {
        val cubo = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources, R.drawable.black_emperor_symbol), ancho, ancho, true)
        return cubo
    }
}