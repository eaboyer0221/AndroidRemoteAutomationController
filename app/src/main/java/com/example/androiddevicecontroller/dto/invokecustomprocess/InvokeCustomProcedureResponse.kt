package com.example.androiddevicecontroller.dto.invokecustomprocess

import com.example.androiddevicecontroller.dto.AamResponseBase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class InvokeCustomProcedureResponse(private val json: String): AamResponseBase() {
    override fun <TResponse> translate(): TResponse {
        val typeToken = object : TypeToken<InvokeCustomProcedureResponse>() {}.type
        return Gson().fromJson(json, typeToken)
    }
}
