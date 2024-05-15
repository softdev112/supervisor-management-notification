package com.lightfeather

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class SupervisorModulePackage : ReactPackage {
    override fun createNativeModules(p0: ReactApplicationContext): MutableList<NativeModule> {
        return mutableListOf(SupervisorModule(p0))
    }

    override fun createViewManagers(p0: ReactApplicationContext): MutableList<ViewManager<View, ReactShadowNode<*>>> {
        return mutableListOf()
    }
}

class SupervisorModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "SupervisorModule"
    }

    @ReactMethod
    fun getSupervisors(callback: Callback) {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("https://o3m5qixdng.execute-api.us-east-1.amazonaws.com/api/managers")
                .build()

        client.newCall(request).enqueue(object : Callback, okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.invoke(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.invoke("Failed to fetch supervisors: ${response.code}")
                    return
                }

                val jsonResponse = response.body?.string()
                val supervisorList = parseSupervisors(jsonResponse)
                        .filter { !it.first.matches(Regex("\\d+")) } // Remove numeric jurisdictions
                        .sortedWith(compareBy({ it.first }, { it.second }, { it.third })) // Sort by jurisdiction, last name, and first name
                        .map { "${it.first} - ${it.second}, ${it.third}" } // Format supervisor name
                callback.invoke(null, Arguments.fromArray(supervisorList.toTypedArray()))
            }

            override fun invoke(vararg p0: Any?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun parseSupervisors(jsonString: String?): List<Triple<String, String, String>> {
        val supervisorList = mutableListOf<Triple<String, String, String>>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val jurisdiction = jsonObject.getString("jurisdiction")
                val lastName = jsonObject.getString("lastName")
                val firstName = jsonObject.getString("firstName")
                supervisorList.add(Triple(jurisdiction, lastName, firstName))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return supervisorList
    }

    @ReactMethod
    fun submitEmployee(
            firstName: String,
            lastName: String,
            email: String?,
            phoneNumber: String?,
            supervisor: String,
            callback: Callback
    ) {
        // Check if required fields are empty
        if (firstName.isBlank() || lastName.isBlank() || supervisor.isBlank()) {
            callback.invoke("Error: Missing required fields")
            return
        }

        // Validate first name and last name
        val nameRegex = Regex("[A-Za-z]+")
        if (!firstName.matches(nameRegex) || !lastName.matches(nameRegex)) {
            callback.invoke("Error: Invalid first name or last name")
            return
        }

        // Validate email if provided
        if (email != null && email != "" && !isValidEmail(email)) {
            callback.invoke("Error: Invalid email")
            return
        }

        // Validate phone number if provided
        if (phoneNumber != null && phoneNumber != "" && !isValidPhoneNumber(phoneNumber)) {
            callback.invoke("Error: Invalid phone number")
            return
        }

        // Perform submission logic here
        // Print the employee information
        val employeeInfo = "First Name: $firstName\n" +
                "Last Name: $lastName\n" +
                "Email: ${email ?: "N/A"}\n" +
                "Phone Number: ${phoneNumber ?: "N/A"}\n" +
                "Supervisor: $supervisor"
        println(employeeInfo)

        // Invoke callback with success message
        callback.invoke(null, "Employee submitted successfully")
    }

    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return email.matches(emailRegex)
    }

    // Function to validate phone number format
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Implement phone number validation logic
        // This is a simplified example, you can use libraries or more complex logic for validation
        val phoneRegex = Regex("\\d{10}") // Example regex for 10-digit phone number
        return phoneNumber.matches(phoneRegex)
    }

}
