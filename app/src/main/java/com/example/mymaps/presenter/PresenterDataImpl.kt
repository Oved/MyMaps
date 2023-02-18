package com.example.mymaps.presenter

import com.example.mymaps.interfaces.iModel
import com.example.mymaps.interfaces.iPresenter
import com.example.mymaps.interfaces.iView
import com.example.mymaps.model.GetDataModel
import com.example.mymaps.model.data.Map

class PresenterDataImpl(var view : iView) : iPresenter {

    val model : iModel = GetDataModel(this)

    override fun searchData() {
        model.getDataApi()
    }

    override fun showLocations(locations: List<Map>) {
        view.showLocations(locations)
    }
}