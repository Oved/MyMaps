package com.example.mymaps.presenter

import android.content.Context
import com.example.mymaps.interfaces.iModel
import com.example.mymaps.interfaces.iPresenter
import com.example.mymaps.interfaces.iView
import com.example.mymaps.model.GetDataModel

class PresenterDataImpl(var view : iView, context: Context) : iPresenter {

    val model : iModel = GetDataModel(this, context)

    override fun searchData() {
        model.getDataApi()
    }

    override fun showLocations() {
        view.showLocations()
    }

    override fun showError(message: String) {
        view.showError(message)
    }
}