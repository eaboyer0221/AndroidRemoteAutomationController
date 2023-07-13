package com.example.androiddevicecontroller.dto.runshellcommand

import com.example.androiddevicecontroller.dto.AamResponseBase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class RunShellCommandResponse(val string: String): AamResponseBase() {
    override fun <TResponse> translate(): TResponse {
        val typeToken = object : TypeToken<RunShellCommandResponse>() {}.type
        return Gson().fromJson(string, typeToken)
    }
}
