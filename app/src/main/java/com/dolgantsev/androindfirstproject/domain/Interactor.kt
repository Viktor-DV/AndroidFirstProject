package com.dolgantsev.androindfirstproject.domain

import com.dolgantsev.androindfirstproject.data.MainRepository

class Interactor(val repo: MainRepository) {
    fun getFilmsDB(): List<Film> = repo.filmsDataBase
}