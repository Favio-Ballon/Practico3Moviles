package com.example.candycrush


class Joya(var num: Int, //tipo
           var tipo: Tipo
) {
    var dragging = false
    var desaparecer = false


    fun equals(joya: Joya): Boolean {
        return this.num == joya.num
    }




}