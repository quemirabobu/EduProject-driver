
# BitCamp Final Project
<hr/>

##### 비트캠프 마지막 프로젝트 3조
##### 프로젝트 명 : 에듀벤쳐 (Edu-Venture)
##### 앱 이름 : EduVenture-Student
##### 팀원 : 김민제, 강호현, 김한슬, 이완재, 조현진, 김정우, 김은석
##### 프로젝트 기간 : 2020.08.02 ~ 2020.09.11
##### 개발 환경: Android Studio, Java, Retrofit API, SpringBoot, MySQL, NaverCloud
<hr/>

## Edu-Driver
<hr/>

+ 앱 내용:

  + Edu-Venture의 버스기사를 위한 앱
    + 차량 번호, 기사의 핸드폰번호, 위치의 위도, 경도를 서버에 전송
    + 버스기사가 학생들이 승차를 할때마다 사진을 찍어서 서버에 전송


<hr/>

## 구현 방식

######  0) 전체적인 구조

+ Retrofit API, OKHttp를 통해 Spring에 접근. LocationUpdatesService 에서 현재 위치를 가져와 MainActivity에 전해줌.
+ MainActivity에서 OKHTTP로 서버에 차량번호, 핸드폰번호, 위도, 경도 전송
+ MainActivity에서 카메라 버튼을 눌러 사진을 찍고 서버에 Multifile 값 전송


######  1) 위치, 핸드폰번호, 차량번호 전송

+ 위치전송기능

  + fusedLocationProviderClient 라이브러리를 통해 나의 위치 찾기(locationResult.getLocations() 메소드)
  + locationResult.getLocations().getLongitude() 메소드를 통해 위도값, 경도값 조회
  + MainActivity의 EditText값에 적혀있던 차량번호, 기사의 핸드폰번호를 SharedPreference에서 가져옮
  + 모든값을 JSONObject 에 넣고 OkHttpClient 를 통해 서버에 전송

######  2) 사진 전송

+   FileProvider.getUriForFile 를 통해 찍은 사진의 uri를 가져온 후 현재시간을 이름에 넣고 MultiFileBody를 통해 서버에 전송


## 예시

<hr/>


<br/>
<br/>

1) 차량, 핸드폰번호, 위치 전송


<br/>
<br/>

<img src="./images/1.gif"  width="700" height="370">

<br/>
<br/>


2) 학원생들 승차 사진전송

<br/>
<br/>

<img src="./images/2.gif"  width="700" height="370">


<br/>
<br/>

3) 위치조작 확인

<br/>
<br/>

  
  
  

<img src="./images/3.gif"  width="700" height="370">

<br/>
<br/>


#### 영상 링크
https://youtu.be/3axZLl6HEaM?si=Si4G7HE1vtIiDkPy