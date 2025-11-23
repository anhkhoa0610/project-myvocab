package com.example.project

data class SanPham (var name: String, var type: String, var img: Int) {
    override fun toString(): String {
        return name;
    }
}