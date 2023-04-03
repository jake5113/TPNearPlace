package com.jake5113.tpkaosearchapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.jake5113.tpkaosearchapp.R
import com.jake5113.tpkaosearchapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        // 액션바에 업버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignup.setOnClickListener { clickSignUp() }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignUp() {
        //Firebase Firestore DB 에 사용자 정보 저장하기

        var email: String = binding.etEmail.text.toString()
        var password = binding.etPassword.text.toString()
        var passwordConfirm = binding.etPasswordConfirm.text.toString()

        // 유효성 검사 - 패스워드와 패스워드 확인이 맞는지 검사
        if (password != passwordConfirm) {
            AlertDialog.Builder(this).setMessage("비밀번호가 다릅니다.").create().show()
            binding.etPasswordConfirm.selectAll()
            return
        }

        // Firestore DB instance 얻어오기
        val db = FirebaseFirestore.getInstance()

        // 저장할 값(이메일, 비밀번호) 을 HashMap으로 저장
        val user: MutableMap<String, String> = mutableMapOf()
        user.put("email", email)
        user["password"] = password

        // Collection 명은 "emailUsers"로 지정 [ RDBMS의 테이블명 같은 역할 ]
        // 혹시 중복된 email 을 가진 회원정보가 있을 수 있으니 확인
        db.collection("emailUsers").whereEqualTo("email", email)
            .get().addOnSuccessListener {
                // 같은 값을 가진 Document가 있다면 사이즈가 0개 이상일 것이므로
                if (it.documents.size > 0) {
                    AlertDialog.Builder(this).setMessage("중복된 이메일이 있습니다.").show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                } else {
                    // 랜덤하게 만들어지는 document명을 회원 id 값으로 사용할 에정
                    db.collection("emailUsers").add(user).addOnSuccessListener {
                        AlertDialog.Builder(this)
                            .setMessage("회원가입 완료").setPositiveButton("확인", object : OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    finish()
                                }

                            })
                    }
                }
            }
    }
}
