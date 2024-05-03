#ifndef _SERVERSEND_H_
#define _SERVERSEND_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <curl/curl.h>
#include "cJSON.h"

#define MAX_LEN 600
#define URL "http://3.39.187.161:8000/gps_send/"

// char jwt_token[300]
// char gps_json[300]

void data_to_json(char *latitude, char *longitude, char *jwt_token, char *fileName); //위도,경도,토큰,파일명
void json_to_server(char *jwt_token, char *gps_json);   //log데이터와 실시간 데이터 전송
void log_to_json(char *jwt_token, char *file_path);
void rewrite_json(char *jwt_token, char *username, char *envName, char *fileName);
void json_parsing(char *username, char *jwt_token);
void reset_data(char *data);
// void send_server();

void data_to_json(char *latitude, char *longitude, , char *gps_json, char *jwt_token){
    reset_data(gps_json)
    sprintf(gps_json,"{\"jwt\" : \"%s\", \"latitude\" : \"%s\", \"longitude\" : \"%s\"}",jwt_token,latitude,longitude)
    //memset으로 초기화시킨다
}

void json_to_server(char *jwt_token, char *gps_json){
    CURL *curl;
    CURLcode res;

    curl_global_init(CURL_GLOBAL_ALL);  

    curl = curl_easy_init();

    if(curl) {
        /* 요청을 보낼 URL 설정 */
        curl_easy_setopt(curl, CURLOPT_URL, URL);

        /* POST 데이터 설정 */
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, gps_json);

        /* POST 데이터의 크기 설정 */
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, (long)strlen(gps_json));

        /* 요청 실행 */
        res = curl_easy_perform(curl);

        /* 에러 체크 */
        if(res != CURLE_OK)
            fprintf(stderr, "curl_easy_perform() failed: %s\n",
                    curl_easy_strerror(res));

        /* 리소스 정리 */
        curl_easy_cleanup(curl);
        curl_global_cleanup();
    }

    curl_global_cleanup();
}

void log_to_json(char *jwt_token, char *file_path){
    //서버로 보내느 방식 작성
}

void json_parsing(char *username, char *jwt_token){
    // 파일에서 JSON 데이터를 읽기
    FILE* file; 

    // fopen_s
    fopen_s(&file, "shareData.json", "r");


    if (!file) {
        printf("파일을 열 수 없습니다.");
        return 1;
    }

    // 파일 크기 계산
    fseek(file, 0, SEEK_END);
    long file_size = ftell(file);
    fseek(file, 0, SEEK_SET);

    // 파일 크기에 맞는 메모리 할당 및 데이터 읽기
    char* json_data = (char*)malloc(file_size + 1);
    fread(json_data, 1, file_size, file);
    fclose(file);
    json_data[file_size] = '\0'; // 문자열 끝에 NULL 추가

    // cJSON으로 JSON 데이터 파싱
    cJSON* json = cJSON_Parse(json_data);
    free(json_data); // 사용이 끝난 메모리 해제

    if (!json) {
        const char* error_ptr = cJSON_GetErrorPtr();
        if (error_ptr != NULL) {
            fprintf(stderr, "Error before: %s\n", error_ptr);
        }
        return 1;
    }

    // 예제로부터 데이터 추출
    cJSON* id = cJSON_GetObjectItemCaseSensitive(json, "username");
    cJSON* jwt = cJSON_GetObjectItemCaseSensitive(json, "jwt");

    // 데이터 출력
    // printf("username: %s\n", username->valuestring);
    // printf("jwt: %s\n", jwt->valuestring);

    reset_data(username);
    reset_data(jwt_token);

    strcpy_s(username, sizeof(username), id->valuestring);
    strcpy_s(jwt_token, sizeof(jwt_token), jwt->valuestring);

    // cJSON 객체 메모리 해제
    cJSON_Delete(json);
}

void rewrite_json(char *jwt_token, char *username, char *envName, char *fileName){
    char *userInfo =(char *)malloc(300 * sizeof(char));

    strcpy_s(envName,sizeof(getenv("USER")),getenv("USER"));

    sprintf(userInfo,"{\"jwt\" : \"%s\", \"fileName\" : \"%s\", \"username\" : \"%s\"}",jwt_token,fileName,username);

    string file_path= "/home"+envName+"/driverLog/shareData.json"

    FILE *file;
    file=fopen_s(&file,file_path,'w');
    if(int ret=fwrite(userInfo, sizeof(char), strlen(userInfo), fw)){
        printf("userInfo write failed = %d\n",ret);
    }

    fclose(file);
    free(userInfo);
}

#endif


// /* 콜백 함수 정의 */
// size_t write_callback(char *ptr, size_t size, size_t nmemb, void *userdata) {
//     FILE *file = (FILE *)userdata;
//     return fwrite(ptr, size, nmemb, file);
// }

// int main(void) {
//     CURL *curl;
//     CURLcode res;

//     curl_global_init(CURL_GLOBAL_DEFAULT);

//     curl = curl_easy_init();
//     if (curl) {
//         /* 요청 보낼 URL 설정 */
//         curl_easy_setopt(curl, CURLOPT_URL, "http://example.com/a.json");

//         /* HTTP POST 설정 */
//         curl_easy_setopt(curl, CURLOPT_POST, 1L);

//         /* 보낼 JSON 파일 열기 */
//         FILE *json_file = fopen("a.json", "rb");
//         if (!json_file) {
//             fprintf(stderr, "파일을 열 수 없습니다.");
//             return 1;
//         }

//         /* POST 데이터 설정 */
//         curl_easy_setopt(curl, CURLOPT_READDATA, json_file);

//         /* 콜백 함수 설정 */
//         curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_callback);

//         /* 서버로부터의 응답을 받을 파일 열기 */
//         FILE *response_file = fopen("response.json", "wb");
//         if (!response_file) {
//             fprintf(stderr, "응답 파일을 열 수 없습니다.");
//             return 1;
//         }

//         /* 서버로부터의 응답을 파일로 저장하기 위한 옵션 설정 */
//         curl_easy_setopt(curl, CURLOPT_WRITEDATA, response_file);

//         /* 요청 실행 */
//         res = curl_easy_perform(curl);

//         /* 에러 체크 */
//         if (res != CURLE_OK) {
//             fprintf(stderr, "curl_easy_perform() failed: %s\n",
//                     curl_easy_strerror(res));
//         }

//         /* 리소스 정리 */
//         curl_easy_cleanup(curl);
//         curl_global_cleanup();

//         /* 파일 닫기 */
//         fclose(json_file);
//         fclose(response_file);
//     }

//     return 0;
// }