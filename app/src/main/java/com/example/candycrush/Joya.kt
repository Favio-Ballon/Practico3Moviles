package com.example.candycrush

import android.graphics.Bitmap

class Joya(img: Bitmap) {
    private var img: Bitmap = img
    var dragging = false

    fun getImg(): Bitmap {
        return img
    }

    fun setImg(img: Bitmap) {
        this.img = img
    }



    fun equals(joya: Joya): Boolean {
        return this.img == joya.img
    }




}