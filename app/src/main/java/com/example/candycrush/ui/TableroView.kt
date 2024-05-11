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
import com.example.candycrush.Joya
import com.example.candycrush.R
import com.example.candycrush.Tipo
import kotlin.math.abs
import kotlin.time.times

class TableroView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var model: Tablero? = null
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 8f
    }
    private val ofsetY = 2
    //((((height/(width / model!!.columnas)).toInt())- model!!.filas-1)/2).toInt()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        model = Tablero(context, w)
        inicializarJoyasTamano()
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
                        val centerY = ((i + ofsetY) * alto + alto / 2).toFloat()
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
        inicializarJoyasTamano()
        if(animacion()){
            invalidate()
            dibujar(canvas)
            return
        }
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
                val centerY = ((i + ofsetY) * alto + alto / 2).toFloat()
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
                    // Si se esta moviendo se dibuja la imagen en la nueva posición
                if (board.matriz[i][j].dragging && !board.matriz[i][j].desaparecer) {
                    //Se verifica si el movimiento es horizontal o vertical
                    //Vertical
                    if (movAbsX > movAbsY) {
                        //Se calcula la diferencia entre el centro de la imagen y la posición del dedo,
                        val dif = centerX - dragX
                        if(dif < 0){
                            if (abs(dif) < img.width) {
                                model!!.matriz[i][j + 1].x -= abs(dif.toInt())
                            }else{
                                model!!.matriz[i][j + 1].x -= img.width
                            }
                        }else {
                            if (abs(dif) < img.width) {
                                model!!.matriz[i][j - 1].x += abs(dif.toInt())
                            }else{
                                model!!.matriz[i][j - 1].x += img.width
                            }
                        }
                        //Se verifica si el movimiento supera el tamaño de la imagen para intercambiar las posiciones
                        if (movAbsX > img.width){
                            //Se verifica si el movimiento es hacia la derecha o izquierda
                            //Derecha
                            if(dragX > centerX){
                                //Se calcula la nueva posición de la imagen
                                moveX = centerX + (img.width/2)
                                //Se intercambian las posiciones de las joyas
                                val temp = model!!.matriz[i][j]
                                intercambiar(i,j,i,j+1)
                                //Se verifica si se puede realizar el movimiento
                                if(temp.tipo == Tipo.CUBO){
                                    poderCubo(model!!.matriz[i][j])
                                    model!!.matriz[i][j+1].tipo = Tipo.NORMAL
                                }
                                else if (!verificarMovimiento(i,j+1) && !verificarMovimiento(i,j)){
                                    //Si no se puede se regresan las posiciones
                                    intercambiar(i,j,i,j+1)
                                }
                            }
                            //Izquierda
                            else{
                                moveX = centerX - (img.width +(img.width/2))
                                val temp = model!!.matriz[i][j]
                                intercambiar(i,j,i,j-1)
                                if(temp.tipo == Tipo.CUBO){
                                    poderCubo(model!!.matriz[i][j])
                                    model!!.matriz[i][j-1].tipo = Tipo.NORMAL
                                }
                                else if (!verificarMovimiento(i,j-1) && !verificarMovimiento(i,j)){
                                    intercambiar(i,j,i,j-1)
                                }
                            }
                        }
                        //Se dibuja la imagen en la nueva posición
                        canvas.drawBitmap(
                            img,
                            moveX,
                            model!!.matriz[i][j].y.toFloat(),
                            paint
                        )
                        //Vertical
                    }else{

                        val dif = centerY - dragY
                        if(dif < 0){
                            if (abs(dif) < img.height) {
                                model!!.matriz[i+1][j].y -= abs(dif.toInt())
                            }else{
                                model!!.matriz[i+1][j].y -= img.width
                            }
                        }else {
                            if (abs(dif) < img.height) {
                                model!!.matriz[i-1][j].y += abs(dif.toInt())
                            }else{
                                model!!.matriz[i-1][j].y += img.width
                            }
                        }

                        //Se verifica si el movimiento supera el tamaño de la imagen para intercambiar las posiciones
                        if (movAbsY > img.height){
                            //Se verifica si el movimiento es hacia abajo o arriba
                            //Abajo
                            if(dragY > centerY){
                                //Se calcula la nueva posición de la imagen
                                moveY = centerY + (img.height/2)
                                //Se intercambian las posiciones de las joyas
                                val temp = model!!.matriz[i][j]
                                intercambiar(i,j,i+1,j)
                                if(temp.tipo == Tipo.CUBO){
                                    poderCubo(model!!.matriz[i][j])
                                    model!!.matriz[i+1][j].tipo = Tipo.NORMAL
                                }
                                else if (!verificarMovimiento(i+1,j) && !verificarMovimiento(i,j)){
                                    //Si no se puede se regresan las posiciones
                                    intercambiar(i,j,i+1,j)
                                }
                            }
                            //Arriba
                            else{
                                moveY = centerY - (img.height +(img.height/2))
                                val temp = model!!.matriz[i][j]
                                intercambiar(i,j,i-1,j)
                                if(temp.tipo == Tipo.CUBO){
                                    poderCubo(model!!.matriz[i][j])
                                    model!!.matriz[i-1][j].tipo = Tipo.NORMAL
                                }
                                else if (!verificarMovimiento(i-1,j) && !verificarMovimiento(i,j)){
                                    intercambiar(i,j,i-1,j)
                                }
                            }
                        }
                        //Se dibuja la imagen en la nueva posición
                        canvas.drawBitmap(
                            img,
                            model!!.matriz[i][j].x.toFloat(),
                            moveY,
                            paint
                        )
                    }
                    
                }
            }
        }
        dibujar(canvas)
    }

    private fun dibujar(canvas: Canvas) {
        val board = model
        val ancho = width / board!!.columnas

        for (i in 1 until board.filas) {
            for (j in 0 until board.columnas) {
                var img = board.lista[board.matriz[i][j].num]
                // Se verifica el tipo de joya para cambiar la imagen
                if (board.matriz[i][j].tipo == Tipo.FUEGO) {
                    img = crearFuego(img)
                }
                if (board.matriz[i][j].tipo == Tipo.RAYO) {
                    img = crearRayo(img)
                }
                if (board.matriz[i][j].tipo == Tipo.CUBO) {
                    img = crearCubo(ancho)
                }

                // Si no se esta moviendo se dibuja la imagen en su posición
                if (!board.matriz[i][j].dragging) {
                    if(board.matriz[i][j].y > (ofsetY) * img.height){
                        canvas.drawBitmap(
                            img,
                            board.matriz[i][j].x.toFloat(),
                            board.matriz[i][j].y.toFloat(),
                            paint
                        )

                    }
                }
            }
        }
    }

    private fun animacion(): Boolean {
        val board = model
        val alto = width / board!!.columnas
        val lista = model!!.matriz
        var bandera = false
        for (i in 0 until model!!.filas) {
            for (j in 0 until model!!.columnas) {
                var img = board.lista[board.matriz[i][j].num]
                val centerY = ((i + ofsetY) * alto + alto / 2).toFloat()
                val imageY = centerY - img.height / 2
                if(lista[i][j].animacion && !lista[i][j].desaparecer){
                    if(lista[i][j].y < imageY){
                        lista[i][j].y += 3
                        bandera = true
                    } else{
                        lista[i][j].animacion = false
                    }
                }
            }
        }
        return bandera
    }
    private fun intercambiar(i:Int,j:Int, i2:Int,j2:Int){
        val temp = model!!.matriz[i][j]
        model!!.matriz[i][j] = model!!.matriz[i2][j2]
        model!!.matriz[i2][j2] = temp
    }
    private fun verificarCombo(): Boolean{
        val lista = model!!.matriz
        var cambio = false
        for (i in 1 until model!!.filas) {
            for (j in 0 until model!!.columnas) {
                if(verificarMovimiento(i,j)) {
                    cambio = true
                    continue
                }
            }
        }
        actualizarTablero()
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
            if (lista[i][jMenor].equals(lista[i][jMenor + 1]) && lista[i][jMenor].equals(lista[i][jMenor + 2])
                && !model!!.matriz[i][jMenor].desaparecer && !model!!.matriz[i][jMenor+1].desaparecer && !model!!.matriz[i][jMenor+2].desaparecer){
                if (lista[iMenor][j].equals(lista[iMenor + 1][j]) && lista[iMenor][j].equals(lista[iMenor + 2][j])
                    && !model!!.matriz[iMenor][j].desaparecer && !model!!.matriz[iMenor+1][j].desaparecer && !model!!.matriz[iMenor+2][j].desaparecer){
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
                        if(model!!.matriz[y][j].tipo == Tipo.RAYO){
                            poderRayo(y,j)
                            return true
                        }
                        if(model!!.matriz[y][j].tipo == Tipo.FUEGO){
                            poderFuego(y,j)
                            return true
                        }
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
                        if(model!!.matriz[i][x].tipo == Tipo.RAYO){
                            poderRayo(i,x)
                            return true
                        }
                        if(model!!.matriz[i][x].tipo == Tipo.FUEGO){
                            poderFuego(i,x)
                            return true
                        }
                        model!!.matriz[i][x].desaparecer = true
                    }
                    return true
                }
            }
        return false
    }

    private fun poderCubo(joya: Joya){
        var lista = model!!.matriz
        for (i in 0 until model!!.filas) {
            for (j in 0 until model!!.columnas) {
                if (lista[i][j].equals(joya) && !lista[i][j].desaparecer) {
                    lista[i][j].desaparecer = true
                }
            }
        }
        actualizarTablero()
    }

    private fun poderRayo(i:Int,j: Int){
        val lista = model!!.matriz
        //Se eliminan las joyas en la misma fila
        for (y in 1 until model!!.filas) {
            model!!.matriz[y][j].desaparecer = true
        }
        //Se eliminan las joyas en la misma columna
        for (x in 0 until model!!.columnas) {
            model!!.matriz[i][x].desaparecer = true

        }

    }

    private fun poderFuego(i:Int,j: Int){
        val lista = model!!.matriz
        //linea de arriba
        if (i>1) {
            model!!.matriz[i-1][j].desaparecer = true
            if(j>0){
                model!!.matriz[i-1][j-1].desaparecer = true
            }
            if(j<model!!.columnas-1){
                model!!.matriz[i-1][j+1].desaparecer = true
            }
        }
        //linea del medio
        model!!.matriz[i][j].desaparecer = true
        if(j>0){
            model!!.matriz[i][j-1].desaparecer = true
        }
        if(j<model!!.columnas-1){
            model!!.matriz[i][j+1].desaparecer = true
        }
        //linea de abajo
        if(i<model!!.filas-1){
            model!!.matriz[i+1][j].desaparecer = true
            if(j>0){
                model!!.matriz[i+1][j-1].desaparecer = true
            }
            if(j<model!!.columnas-1){
                model!!.matriz[i+1][j+1].desaparecer = true
            }
        }
    }

    private fun actualizarTablero(){
        val lista = model!!.matriz
        for (j in 0 until model!!.columnas) {
            val off = findDesaparecer(j)
            for (i in 1 until model!!.filas) {
                if (lista[i][j].desaparecer) {
                    actualizarFila(i,j,off)
                }
            }
        }
        inicializarJoyasTamano()
        invalidate()
    }
    private fun actualizarFila(i: Int, j: Int, off: Int){
        val board = model
        val ancho = width / board!!.columnas
        val alto = width / board.columnas
        if(i == 0){
            model!!.matriz[i][j] = model!!.rand()
            return
        }
        model!!.matriz[i][j] = model!!.matriz[i-1][j]
        model!!.matriz[i][j].x = j * ancho
        model!!.matriz[i][j].y = ((i + ofsetY) * alto) - (off * alto)
        model!!.matriz[i][j].animacion = true
        actualizarFila(i-1,j, off)
    }

    private fun findDesaparecer(j:Int): Int{
        var contar = 0
        for (i in 1 until model!!.filas) {
            if(model!!.matriz[i][j].desaparecer){
                contar++
            }
        }
        return contar
    }

    private fun crearFuego(originalBitmap: Bitmap): Bitmap {

        val fuego = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, originalBitmap.config)


        val canvas = Canvas(fuego)


        val paint = Paint().apply { color = Color.RED }

        canvas.drawCircle(canvas.width.toFloat()/2, canvas.height.toFloat()/2, canvas.width.toFloat()/2, paint)

        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        return fuego
    }

    private fun crearRayo(originalBitmap: Bitmap): Bitmap {
        val rayo = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, originalBitmap.config)

        val canvas = Canvas(rayo)

        val paint = Paint().apply { color = Color.BLUE }
        canvas.drawCircle(canvas.width.toFloat()/2, canvas.height.toFloat()/2, canvas.width.toFloat()/2, paint)

        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        return rayo
    }

    private fun crearCubo(ancho: Int): Bitmap {
        val cubo = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources, R.drawable.black_emperor_symbol), ancho, ancho, true)
        return cubo
    }

    private fun inicializarJoyasTamano(){
        val board = model
        val ancho = width / board!!.columnas
        val alto = width / board.columnas
        for (i in 1 until board.filas) {
            for (j in 0 until board.columnas) {
                if (!board.matriz[i][j].animacion) {
                    board.matriz[i][j].x = j * ancho
                    board.matriz[i][j].y = (i + ofsetY) * alto
                }
            }
        }
    }
}