package de.hpled.zinia.shows.interfaces

interface OnShowViewListener {
    fun onClick(show: Show)
    fun onLongClick(show: Show)
    fun onEdit(show: Show)
    fun onDelete(show: Show)
}