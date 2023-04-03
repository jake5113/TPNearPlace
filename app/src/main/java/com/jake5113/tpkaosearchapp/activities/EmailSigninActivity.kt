package com.jake5113.tpkaosearchapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.jake5113.tpkaosearchapp.G
import com.jake5113.tpkaosearchapp.R
import com.jake5113.tpkaosearchapp.databinding.ActivityEmailSigninBinding
import com.jake5113.tpkaosearchapp.model.UserAccount

class EmailSigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailSigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignin.setOnClickListener { clickSignIn() }


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignIn() {
        var email = binding.etEmail.text.toString()
        var password = binding.etPassword.text.toString()

        // Firebase Firestore DB 에서 이메일과 패스워드 확인
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("emailUsers")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get().addOnSuccessListener{
                if (it.documents.size>0){
                    // 로그인 성공
                    var id:String = it.documents[0].id // document 명
                    G.userAccount = UserAccount(id, email)

                    // 로그인 성공했으니 곧바로 MainActivity 로 이동
                    val intent: Intent = Intent(this, MainActivity::class.java)

                    // 기존 task의 모든 액티비티들을 제거하고 새로운 task를 시작
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }else{
                    // 로그인 실패
                    AlertDialog.Builder(this).setMessage("이메일과 비밀번호를 다시 확인해주시기 바랍니다.").show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                }
            }
    }
}