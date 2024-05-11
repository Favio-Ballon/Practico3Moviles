package com.example.candycrush


class Joya(var num: Int, //tipo
           var tipo: Tipo
) {
    var dragging = false
    var desaparecer = false
    var x = 0
    var y = 0
    var animacion = false


    fun equals(joya: Joya): Boolean {
        return this.num == joya.num
    }

    fun copySize(joya: Joya){
        this.x = joya.x
        this.y = joya.y
    }

    fun copy(joya: Joya){
        this.num = joya.num
        this.tipo = joya.tipo
        this.dragging = joya.dragging
        this.desaparecer = joya.desaparecer
    }

}