package com.example.androiddevicecontroller.dto

interface ResponseTranslator { fun <TResponse> translate(): TResponse }

abstract class BaseDto

abstract class AamRequestBase: BaseDto()

abstract class AamResponseBase: BaseDto(), ResponseTranslator
