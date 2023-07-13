package com.example.androiddevicecontroller

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.androiddevicecontroller.dto.invokecustomprocess.InvokeCustomProcedureRequest
import com.example.androiddevicecontroller.dto.invokecustomprocess.InvokeCustomProcedureResponse
import com.example.androiddevicecontroller.dto.runshellcommand.RunShellCommandRequest
import com.example.androiddevicecontroller.dto.runshellcommand.RunShellCommandResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.NetworkInterface
import java.util.*


class WebAppService : Service() {
    private lateinit var server: ApplicationEngine
    private val runtime: Runtime = Runtime.getRuntime()


    override fun onCreate() {
        val interfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface: NetworkInterface = interfaces.nextElement()
            val addresses = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && !address.hostAddress?.contains(":")!!) {
                    val ipAddress = address.hostAddress
                    Toast.makeText(applicationContext, "IP Address: $ipAddress", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onCreate()

        server = embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                jackson {  }
            }

            routing {

                post("/api/run-shell-command") {
                    try {
                        val request = call.receive<RunShellCommandRequest>()
                        toastAndLog("\"run-shell-command\"" , request.command)

                        val pro = withContext(Dispatchers.IO) {
                            runtime.exec(request.command)
                        }
                        val read = BufferedReader(InputStreamReader(pro.inputStream))
                        val out = StringBuilder()
                        var ln: String?
                        while (withContext(Dispatchers.IO) {
                                read.readLine()
                            }.also { ln = it } != null) {
                            out.append(ln)
                            out.append(System.lineSeparator())
                        }
                        withContext(Dispatchers.IO) {
                            read.close()
                            pro.waitFor()
                        }

                        val s = out.toString()
                        Log.d("\"run-shell-command\"", "output: $s")
                        val res = RunShellCommandResponse(s)
                        println("Output: $out")
                        call.respond(res)
                    } catch (e: java.lang.Exception) {
                        call.respond(RunShellCommandResponse(e.toString()))
                    }
                }

                post("/api/invoke-custom-procedure") {
                    val request = call.receive<InvokeCustomProcedureRequest>()
                    toastAndLog("custom-procedure" , "!")

                    // Process the request and create the response
                    val json = "{\"response\":\"Sample JSON response\"}"
                    val response = InvokeCustomProcedureResponse(json)
                    call.respond(response)
                }
            }
        }

        server.start()
    }

    private fun toastAndLog(title: String, string: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, string, Toast.LENGTH_LONG).show()
        }
        Log.d(title, string)
    }

    override fun onDestroy() {
        server.stop(0 , 60000 )
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
