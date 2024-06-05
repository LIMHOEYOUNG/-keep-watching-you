폴더생성 시점 고민하기
실행 후 바로?
조건 ?


writeLocationDataJSONToFile

/*
json write locate info 10 sec interval
*/
time_t start_json_write, end_json_write;


// 위치 정보를 json파일에 작성
    double time_check= difftime(end_json_write,start_json_write);
    if(time_check>=10.0){
      fprintf(file, "%s", json);
      start_json_write=end_json_write;
    }


main

  // 위치 로그파일 작성 timer 시작
  time(&start_json_write);

while(1) 내부

    time(&end_json_write);  //초 간격 확인을 위함 end_json_write - start_json_write가 >=10 이여야 한다.
