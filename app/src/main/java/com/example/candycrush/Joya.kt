package com.example.candycrush

import android.graphics.Bitmap

class Joya(num: Int, tipo: Tipo) {
    var dragging = false
    var desaparecer = false
    var num = num
    //tipo
    var tipo = tipo


    fun equals(joya: Joya): Boolean {
        return this.num == joya.num
    }




}