package com.biggates.bumap.ui.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.biggates.bumap.Model.LoginParam
import com.biggates.bumap.Model.Message
import com.biggates.bumap.R
import com.biggates.bumap.Retrofit.RetrofitClient
import com.biggates.bumap.Retrofit.RetrofitService
import com.biggates.bumap.ViewModel.loginInfo.LoginInfo
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.security.MessageDigest


class LoginActivity() : AppCompatActivity(),LifecycleOwner {

    var isLoading = false
    private val made = "madeByBigGates"
    var dbFirestore = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_login.setOnClickListener {
            progressbar_login.visibility = View.VISIBLE
            btn_login.setOnClickListener {
                Toast.makeText(applicationContext, "로그인중입니다 잠시만 기달려주세요.", Toast.LENGTH_SHORT).show()
            }
            checkProcess()

        }

    }

    private fun checkProcess() {
        var id = id_login.text.toString()
        var pw = pw_login.text.toString()

        when{

            TextUtils.isEmpty(id) -> Toast.makeText(
                applicationContext,
                "아이디를 입력해주세요",
                Toast.LENGTH_SHORT
            ).show()

            TextUtils.isEmpty(pw) -> Toast.makeText(
                applicationContext,
                "비밀번호를 입력해주세요",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                var loginParam = LoginParam()
                loginParam.setId(id)
                loginParam.setPw(pw)
                buLogin(loginParam)
            }

        }


    }

    private fun buLogin(param: LoginParam) {
        val myAPI = RetrofitClient.getInstance().create(RetrofitService::class.java)
        myAPI.buLogin(param).enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if (response.isSuccessful) {
                    var message = response.body()!!

                    if (message.getMessage("status") == "success") {
                        var oDialog = AlertDialog.Builder(
                            this@LoginActivity,
                            android.R.style.Theme_DeviceDefault_Light_Dialog
                        );

                        oDialog.setMessage("개인정보 이용약관에 동의 하시겠습니까?")
                            .setTitle("개인정보 이용약관 동의 안내")
                            .setPositiveButton("아니오", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    progressbar_login.visibility = View.GONE
                                    Toast.makeText(
                                        applicationContext,
                                        "서비스를 이용하실수 없습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show();
                                    btn_login.setOnClickListener {
                                        progressbar_login.visibility = View.VISIBLE
                                        btn_login.setOnClickListener {
                                            Toast.makeText(
                                                applicationContext,
                                                "로그인중입니다 잠시만 기다려주세요.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        checkProcess()

                                    }
                                }
                            })
                            .setNeutralButton("예", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {

                                    LectureScheduleViewModel.setContext(applicationContext)
                                    LectureScheduleViewModel.loadSchedule(param)
                                    LectureScheduleViewModel.isViewLoading.observe(
                                        this@LoginActivity,
                                        Observer {
                                            if (!it) {
                                                progressbar_login.visibility = View.GONE
                                                var name =
                                                    LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()!!.keys.first()
                                                var pref = getSharedPreferences("userInfo", 0)
                                                var editor = pref.edit()
                                                editor.putBoolean("isAutoLogin", true)
                                                editor.putString("id", param.getId())
                                                editor.putString("pw", param.getPw())
                                                editor.putString("name", name)
                                                editor.commit()
                                                setResult(202)
                                                LoginInfo.autoLogin.postValue(true)
                                                finish()
                                                registerIntoFirebase(
                                                    name,
                                                    param.getId(),
                                                    param.getPw()
                                                )
                                                btn_login.setOnClickListener {
                                                    progressbar_login.visibility = View.VISIBLE
                                                    btn_login.setOnClickListener {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            "로그인중입니다 잠시만 기다려주세요.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    checkProcess()

                                                }

                                            }
                                        })

                                }
                            })
                            .setCancelable(true)
                            .show()


                    } else {
                        Toast.makeText(
                            applicationContext,
                            "${message.getMessage("message")}\n비밀번호 오류 횟수 초기화를 할려면 PC로 로그인해주세요",
                            Toast.LENGTH_LONG
                        ).show()
                        progressbar_login.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "로그인 실패\n서버 또는 네트워크에 문제가 생겼습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                progressbar_login.visibility = View.GONE
            }
        })

    }

    private fun registerIntoFirebase(
        name: String,
        id: String,
        pw: String
    ) {
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        md.update(pw.toByteArray(charset("utf8")))
        val hashPw = java.lang.String.format("%064x", BigInteger(1, md.digest()))
        val map = HashMap<String, String>()

        val pwHex: String = byteArrayToHex(pw.toByteArray(charset("utf8")))!!
        val madeHex: String = byteArrayToHex(made.toByteArray(charset("utf8")))!!
        val uid: String = encryptDecrypt(pwHex, madeHex)!!
        val uidHex: String = byteArrayToHex(uid.toByteArray())!!

        val uidUn = hexStringToByteArray("005c0057010700000254040807020501050507010400")
        val uidd = String(uidUn!!)
        val pwHexDe = encryptDecrypt(uidd, madeHex)
        val pww = String(hexStringToByteArray(pwHexDe!!)!!)

        map["name"] = name.substring(0, name.indexOf("님"))
        map["id"] = id
        map["pw"] = hashPw
        map["uid"] = uidHex

        dbFirestore.reference.child("users").child(id).setValue(map).addOnCompleteListener {
            if(it.isSuccessful) Toast.makeText(applicationContext, "등록완료", Toast.LENGTH_SHORT).show()
            else Toast.makeText(applicationContext, "서버에 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show()
        }

    }

    fun encryptDecrypt(inputString: String, key: String): String? {
        var outputString = ""
        val len = inputString.length
        var j = 0

        for (i in 0 until len) {
            if (key.length == j) j = 0
            var a = inputString[i].toInt()
            var b = key[j].toInt()
            var c = inputString[i].toInt().xor(key[j].toInt())
            outputString = outputString +
                    Character.toString(inputString[i].toInt().xor(key[j].toInt()).toChar())
            j++
        }
        return outputString
    }


    fun byteArrayToHex(byteArray: ByteArray?): String? {
        if (byteArray == null || byteArray.size == 0) {
            return null
        }
        val stringBuffer = StringBuilder(byteArray.size * 2)
        var hexNumber: String
        for (aBa in byteArray) {
            hexNumber = "0" + Integer.toHexString(0xff and aBa.toInt())
            stringBuffer.append(hexNumber.substring(hexNumber.length - 2))
        }
        return stringBuffer.toString()
    }


    fun hexStringToByteArray(s: String): ByteArray? {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    override fun onBackPressed() {
        setResult(400)
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setResult(400)
        finish()
        return super.onOptionsItemSelected(item)
    }
}
