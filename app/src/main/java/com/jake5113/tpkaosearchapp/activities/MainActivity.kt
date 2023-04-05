package com.jake5113.tpkaosearchapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.jake5113.tpkaosearchapp.R
import com.jake5113.tpkaosearchapp.databinding.ActivityMainBinding
import com.jake5113.tpkaosearchapp.fragments.PlaceListFragment
import com.jake5113.tpkaosearchapp.fragments.PlaceMapFragment
import com.jake5113.tpkaosearchapp.model.KakaoSearchPlaceResponse
import com.jake5113.tpkaosearchapp.network.RetrofitApiService
import com.jake5113.tpkaosearchapp.network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // 카카오 검색에 필요한 요청 데이터 : query(검색장소명), x(경도: longitude), y(위도:latitude)
    // 1. 검색 장소명
    var searchQuery: String = "화장실" // 앱 초기 검색어 - 내 주변 개방 화장실

    // 2. 현재 내위치 정보 객체(위도, 경도 정보를 멤버로 보유한 객체)
    var myLocation: Location? = null

    // [ Google Fused Location API 사용 : play-servieces-location ]
    val providerClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
    }

    // 검색결과 응답객체 참조변수
    var searchPlaceResponse:KakaoSearchPlaceResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //작업
        //툴바를 제목줄로 대체 - 옵션메뉴랑 연결되도록
        setSupportActionBar(binding.toolbar)

        // 처음 보여질 프레그먼트 동적 추가
        supportFragmentManager.beginTransaction().add(R.id.container_fragment, PlaceListFragment())
            .commit()

        // 탭 레이아웃의 탭버튼 클릭시에 보여줄 프레그먼트 변경
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.text == "LIST") {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, PlaceListFragment()).commit()
                } else if (tab?.text == "MAP") {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, PlaceMapFragment()).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        // 소프트 키보드의 검색 버튼 클릭했을때
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            searchQuery = binding.etSearch.text.toString()
            // 카카오 검색 API를 이용하여 장소들 검색하기
            searchPlace()

            false
        }

        // 특정 키워드 단축 검색 버튼들에 리스너 처리하는 함수 호출
        setChoiceButtonsListener()

        // 내 위치 정보 제공에 대한 동적 퍼미션 요청
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            // 퍼미션 요청 대행사 이용 - 계약 체결
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)


        } else {
            // 내 위치 요청
            requestMyLocation()
        }


    }//onCreate method..

    // 퍼미션 요청 대행사 계약 및 등록
    val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission(),
            object : ActivityResultCallback<Boolean> {
                override fun onActivityResult(result: Boolean?) {
                    if (result!!) requestMyLocation()
                    else Toast.makeText(
                        this@MainActivity,
                        "위치정보제공에 동의하지 않았습니다. 검색기능이 제한됩니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })


    // 내 위치 요청 작업 메소드
    private fun requestMyLocation() {

        // 위치검색 기준 설정하는 요청 객체
        val request: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        // 실시간 위치 정보 갱신 요청
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())


    }

    // 위치검색 결과 콜백객체
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation = p0.lastLocation

            // 위치 탐색되었으니 실시간 업데이트를 종료
            providerClient.removeLocationUpdates(this) // this : locationCallback 객체

            // 위치 정보 얻었으니 검색 시작
            searchPlace()
        }
    }


    // 카카오 장소 검색 API를 파싱하는 작업메소드
    private fun searchPlace() {
        //Toast.makeText(this, "$searchQuery - ${myLocation?.latitude}, ${myLocation?.longitude}", Toast.LENGTH_SHORT).show()

        // Kakao keyword place search api.. REST API 작업 - Retrofit
        val retrofit:Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitApiService = retrofit.create(RetrofitApiService::class.java)
        retrofitApiService.searchPlace(searchQuery, myLocation?.latitude.toString(), myLocation?.longitude.toString()).enqueue(
            object : Callback<KakaoSearchPlaceResponse> {
                override fun onResponse(
                    call: Call<KakaoSearchPlaceResponse>,
                    response: Response<KakaoSearchPlaceResponse>
                ) {
                    searchPlaceResponse = response.body()
                    Toast.makeText(this@MainActivity, "${searchPlaceResponse?.meta?.total_count}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    // 특정 키워드 검색 단축 버튼들에 리스너 처리
    private fun setChoiceButtonsListener() {
        binding.layoutChoice.choiceEvStation.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFastfood.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceCafe.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceGasStation.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceMovies.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceParking.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePharmacy.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePark.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceWc.setOnClickListener { clickChoice(it) }
    }

    // 멤버 변수(property)
    var choiceID = R.id.choice_fastfood

    private fun clickChoice(view: View) {
        // 기존 선택되었던 버튼을 찾아 배경이미지를 흰색 원 그림으로 변경
        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice)

        // 현재 클릭된 버튼의 배경을 회색 원 그림으로 변경
        view.setBackgroundResource(R.drawable.bg_choice_select)

        // 다음 클릭시에 이전 클릭된 뷰의 ID를 기억하도록
        choiceID = view.id

        // 초이스한 것에 따라 검색장소명을 변경하여 다시 검색
        when (choiceID) {
            R.id.choice_ev_station -> searchQuery = "전기차 충전소"
            R.id.choice_fastfood -> searchQuery = "패스트푸드"
            R.id.choice_cafe -> searchQuery = "카페"
            R.id.choice_gas_station -> searchQuery = "주유소"
            R.id.choice_movies -> searchQuery = "영화관"
            R.id.choice_parking -> searchQuery = "주차장"
            R.id.choice_pharmacy -> searchQuery = "약국"
            R.id.choice_park -> searchQuery = "공원"
            R.id.choice_wc -> searchQuery = "화장실"
        }
        // 새로운 검색 시작
        searchPlace()

        // 검색창에 글씨가 있다면 지우기
        binding.etSearch.text.clear()
        binding.etSearch.clearFocus()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_aa -> Toast.makeText(this, "aa", Toast.LENGTH_SHORT).show()
            R.id.menu_bb -> Toast.makeText(this, "bb", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }
}