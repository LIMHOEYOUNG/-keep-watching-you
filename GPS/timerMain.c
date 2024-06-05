#include <stdio.h>
#include <time.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>

#include "com.h"
#include "serverSend.h"

#define MAX_LEN 600
#define MAX_DATA_INFO 80

/*
json write locate info 10 sec interval
*/
time_t start_json_write, end_json_write;




char drive_startTime[50];
char drive_lastTime[50];
char startloc[50];
char lastloc[50];

char fileName[40];
char gpsTime[50];

//serverSend.h 사용
static char gps_url[]="http://3.39.187.161:8000/user_gps/gps_put";
static char log_url[]="http://3.39.187.161:8000/user_log/log_put";

char jwt_token[300];
char gps_json[300];
char username[50];
char envName[30];
char start_location_lati[25];
char start_location_longi[25];
char end_location_lati[25];
char end_location_longi[25];

//char fileName[40];
FILE* fw;

struct {
  char gpsData[80];
  char data_flag;       // 데이터 수신 확인 flag
  char parseData_flag;  // 파싱 완료 flag
  char utc_time[12];    // UTC 시간
  char slatitude[15];   // 위도
  char ns[2];           // 북/남
  char slongitude[15];  // 경도
  char ew[2];           // 동/서
  char use_Flag;        // 사용가능 여부 flag
  char latitude[12];
  char longitude[12];
  char ddd_latitude[3];
  char ddd_longitude[3];
} data_Struct;

void read_gps(char *data_buffer);
void parseing_data();
void data_save();
void reset_data(char *data);
void show_data();
void save_data(char *fileName);
char* gpsDataToJSON(char *gpsTime);
void writeLocationDataJSONToFile(char* fileName, char *gpsTime, FILE *file, char flag);
float convertArray(char *buf, char *ddd_result, char *result);


int fd;
char read_buf[BUFFER_SIZE];
int read_buffer_size;
//int now;

void read_gps(char *data_buffer){
	//printf("=========================================\n");
	//printf("%s\n",strstr(data_buffer,"$GPRMC,"));
    char* dataHead;
    char* dataTail;
	/*
	if(((dataHead= strstr(data_buffer,"$GPRMC,"))!=NULL)){
		printf("strstr(data_buffer,$GPRMC,) = %s\n",strstr(data_buffer,"$GPRMC"));
	}
	else if(((dataHead= strstr(data_buffer,"$GNRMC,"))!=NULL)){
		printf("strstr(data_buffer,$GNRMC,) = %s\n",strstr(data_buffer,"$GNRMC"));
	}
	*/

	// GPRMC와 GNRMC문자열 필터
	if(((dataHead= strstr(data_buffer,"$GPRMC,"))!=NULL) || ((dataHead= strstr(data_buffer,"$GNRMC,"))!=NULL)){

		//printf("dataHead = %s\n",dataHead);
		//printf("dataTail = %s\n",dataTail);
		//printf("gpsData = %s\n",data_Struct.gpsData);
        if (((dataTail = strstr(dataHead, "\n")) != NULL) && (dataTail > dataHead)){
                memset(data_Struct.gpsData, 0, strlen(data_Struct.gpsData));
                memcpy(data_Struct.gpsData, dataHead, dataTail - dataHead);
				//printf("dataHead = %s\n",dataHead);
				//printf("dataTail = %s\n",dataTail);
				//printf("gpsData = %s\n",data_Struct.gpsData);
                data_Struct.data_flag = 1;
				//printf("data_Struct.data_flag = %d\n",data_Struct.data_flag);
        }
    }
	//printf("========================================\n");
}


void parsing_data(){
	char *parseString;
	char *nextString;

	// 데이터가 정상 수신 되었다면
	if(data_Struct.data_flag){
		//printf("gpsData = %s\n",data_Struct.gpsData);

		parseString= strstr(data_Struct.gpsData,",");
		//printf("(parseString = strstr(data_Struct.gpsData, ,) = %s\n",parseString);
		
		if(parseString==NULL){
			printf("Error(parsing_data()) getting data\n");
		}
		// MessageID, UTC, Latitude, N/S, Longitude, E/W 추출
		else{
			for(int i=0; i<6; i++){
				//printf("parseString 1 = %s\n",parseString);
				parseString++;
				//printf("parseString 2 = %s\n",parseString);
				nextString= strstr(parseString,",");
				//printf("nextString 1 = %s\n",nextString);
				if(nextString==NULL){
					printf("Error parsing data, nextString = %s\n",nextString);
				}
				else{
					char buf[20];
					char data_reliability;

					switch(i){
						case 0:
							reset_data(data_Struct.utc_time);
                            memcpy(data_Struct.utc_time,parseString,nextString-parseString);
                            break;
                        case 1:
                            data_reliability= parseString[0];
							break;
                        case 2:
                            reset_data(data_Struct.slatitude);
                            memcpy(data_Struct.slatitude,parseString,nextString-parseString);
                            break;
                        case 3:
                            reset_data(data_Struct.ns);
                            memcpy(data_Struct.ns,parseString,nextString-parseString);
                        	break;
                        case 4:
                            reset_data(data_Struct.slongitude);
                            memcpy(data_Struct.slongitude,parseString,nextString-parseString);
                            break;
                        case 5:
                            reset_data(data_Struct.ew);
                            memcpy(data_Struct.ew,parseString,nextString-parseString);
                            break;
                        default:
                            break;
					}

					//printf("swtich 이후 parseString = %s\n",parseString);
					//printf("swtich 이후 nextString = %s\n",nextString);
                    parseString= nextString;
					//printf("parseString = nextString 이후 parseString = %s\n",parseString);
					//printf("parseString = nextString 이후 nextString = %s\n",nextString);

                    data_Struct.parseData_flag= 1;
                                        
					if(data_reliability=='A'){
						//printf("data_reliability A = %c\n",data_reliability);
                        data_Struct.use_Flag= 1;
                    }
                    else if(data_reliability=='V'){
						//printf("data_reliability V = %c\n",data_reliability);
                        data_Struct.use_Flag= 0;
                    }
					else{
						//printf("non %c\n",data_reliability);
					}
				}
				//printf("nextString 2 = %s\n",nextString);
			}
		}
		data_Struct.data_flag= 0;
	}
}

void reset_data(char *data){
	memset(data,0,strlen(data));
}

void reset_struct(){
  reset_data(data_Struct.gpsData);
  reset_data(data_Struct.utc_time);
  reset_data(data_Struct.slatitude);
  reset_data(data_Struct.slongitude);
  reset_data(data_Struct.ns);
  reset_data(data_Struct.ew);
  reset_data(data_Struct.latitude);
  reset_data(data_Struct.longitude);
  reset_data(data_Struct.ddd_latitude);
  reset_data(data_Struct.ddd_longitude);
}

void show_data(){
	
	printf("UTC : %s\n",data_Struct.utc_time);
	printf("위도 : %s\n",data_Struct.slatitude);
	printf("북남 : %s\n",data_Struct.ns);
	printf("경도 : %s\n",data_Struct.slongitude);
	printf("동서 : %s\n",data_Struct.ew);
	
}

float convertArray(char *buf, char *ddd_result, char *result){
  char *p;
  char subBuf[20];
  p= strstr(buf,".");
  int place= p-buf;

  for(int i=0; i<place-2; i++){
    subBuf[i]=buf[i];
  }
  subBuf[place-2]=' ';
  for(int i= place-1; buf[i-1]!='\0'; i++){
    subBuf[i]=buf[i-1];
  }

  char *ptr = strtok(subBuf," ");
  memcpy(ddd_result,ptr,sizeof(ptr));
  ptr=strtok(NULL," ");
  memcpy(result,ptr,sizeof(ptr));

  float ddd= atof(ddd_result);
  float cal= atof(result);

  //reset_struct(result);
  //sprintf(result,"%lf",(ddd+(cal/60)));

  return(ddd+(cal/60));


}

char* gpsDataToJSON(char *gpsTime) {
    char* json = (char*)malloc(200 * sizeof(char)); // 충분한 공간 할당
   
    printf("=================\n");
    show_data();
    printf("=================\n");

    float c_latitude= convertArray(data_Struct.slatitude,data_Struct.ddd_latitude,data_Struct.latitude);
    float c_longitude= convertArray(data_Struct.slongitude,data_Struct.ddd_longitude,data_Struct.longitude);

    printf("data_Struct.slatitude: %s\n",data_Struct.slatitude);
    printf("data_Struct.slongitude: %s\n",data_Struct.slongitude);
    printf("data_Struct.ddd_latitude: %s\n",data_Struct.ddd_latitude);
    printf("data_Struct.ddd_longitude: %s\n",data_Struct.ddd_longitude);
    printf("data_Struct.latitude: %s\n",data_Struct.latitude);
    printf("data_Struct.longitude: %s\n",data_Struct.longitude);

    sprintf(json, "\n\t{\"time\":\"%s\",\"latitude\":%f,\"longitude\":%f}", 
              gpsTime, c_latitude, c_longitude);
    //sprintf(lastloc,"%s %s",data_Struct.latitude, data_Struct.longitude);
    sprintf(end_location_lati,"%lf",c_latitude);
    sprintf(end_location_longi,"%lf",c_longitude);
    sprintf(drive_lastTime,"%s",gpsTime);

    return json;

}

void writeLocationDataJSONToFile(char* fileName, char *gpsTime, FILE *file, char flag) {

    const char *json_frontData="{\n"
										"  \"date\": [";
		const char *json_backData="\n  ]\n"
									"}";

    if (file == NULL) {
        perror("Error open file");
        return;
    }

    char* json = gpsDataToJSON(gpsTime);
    
    // 위치 정보를 json파일에 작성
    double time_check= difftime(end_json_write,start_json_write);

    if(flag&&(time_check>=10.0)) {
      fprintf(file,"%s",",");
      fprintf(file,"%s",json);
      start_json_write=end_json_write;
      printf("중간위치 작성 = %s\n",json);
    }
    else if(!flag){
      //시작  flag을 사용하여 1번만 실행
      sprintf(drive_startTime,"%s",gpsTime);
      //sprintf(startloc,"%s %s",data_Struct.latitude, data_Struct.longitude);
      sprintf(start_location_lati,"%s",data_Struct.latitude);
      sprintf(start_location_longi,"%s",data_Struct.longitude);
      fprintf(file,"%s",json);
      printf("시작점 작성 = %s\n",json);
    }

    /*
    if(time_check>=10.0){
      fprintf(file, "%s", json);
      start_json_write=end_json_write;
    }
    */

    //변환 완료된 정보들을 사용하여 서버로 정보 전송
    //serverSend.h활용
    //사용자정보 JSON파싱한 후 실행 되어야 됨

    // 1. 위경도와 토큰사용하여 서버로 보낼 json형식 생성
    data_to_json(data_Struct.latitude, data_Struct.longitude, gps_json, jwt_token);
    // 2. 생성한 gps_json을 서버로 전송 (현재는 위치정보 업데이트 url사용)
    //printf("실행전\n");
    json_to_server(gps_url,gps_json);
    //printf("실행후\n");

    free(json);
}

void OnSignal(int sig)  // 콘솔 ctrl+c 입력시 인터럽트 발생
{
    signal(sig, SIG_IGN);
    printf("exit\n");

    char* json_backData = "\n ]\n"
		"}";

    int ret;

    if (ret = fwrite(json_backData, sizeof(char), strlen(json_backData), fw)) {
		printf("json_backData Failed Write = %d\n",ret);
	}

    fclose(fw);

    //마지막 정보 보낼 json파일 작성및 전송
    last_loc_info(gps_json, jwt_token, drive_startTime,start_location_lati,start_location_longi,drive_lastTime,end_location_lati,end_location_longi);
    
    
    log_to_server(log_url,gps_json,fileName);

    
    exit(0);
}

void endWrite(){
    char* json_backData = "\n ]\n"
		"}";

    int ret;

    if (ret = fwrite(json_backData, sizeof(char), strlen(json_backData), fw)) {
		printf("json_backData Failed Write = %d\n",ret);
	}

    fclose(fw);

}

int main() {
  
  //JSON파일 경로 체크하기
  json_parsing(username,jwt_token);

  printf("u = %s, j = %s= ",username,jwt_token);
/*
  FILE* fr;
  fr= fopen("/home/jetson/usr/shareData.txt","r");
  //username=
  printf("username = %s\n",username);
  fr.close();
*/

	//FILE* fr;
	//fr = fopen("t.txt", "r");
  signal(SIGINT, OnSignal); // 인터럽트 시그널 콜백 설정
	char str[MAX_LEN];

	//FILE* fw;
	char start_flag = 0;

	struct tm newtime;
	time_t now;
	char buf[50];

	now = time(&now);
	localtime_r(&now, &newtime);

	char hms[20];
	char ymd[20];
	//char fileName[40];
	sprintf(ymd, "%.8d", ((newtime.tm_year + 1900) * 10000) + ((newtime.tm_mon + 1) * 100) + (newtime.tm_mday));
	sprintf(hms, "%.6d", ((newtime.tm_hour) * 10000) + ((newtime.tm_min) * 100) + (newtime.tm_sec));

	//char gpsTime[50];
	sprintf(gpsTime, "%.4d-%.2d-%.2dT%.2d:%.2d:%.2d", newtime.tm_year + 1900, newtime.tm_mon + 1, newtime.tm_mday, newtime.tm_hour, newtime.tm_min, newtime.tm_sec);


	strcpy(fileName, ymd);
	strcat(fileName, hms);
	strcat(fileName, ".json");

	fw = fopen(fileName, "wb");

	if (fileName == NULL) {
		perror("Error open file");
	}

	char* json_frontData = "{\n"
		"  \"date\": [\n";
	char* json_backData = "\n ]\n"
		"}";

  int ret;
	if (ret = fwrite(json_frontData, sizeof(char), strlen(json_frontData), fw)) {
		printf("json_frontData Failed Write = %d\n",ret);
	}

	fd = open_port("/dev/ttyACM0");
	if (set_com_config(fd, 115200, 8, 'N', 1) < 0) {
		perror("set_com_config");
		return 1;
	}

  // 위치 로그파일 작성 timer 시작
  time(&start_json_write);

	while (1/*fgets(str,MAX_LEN,fr) != NULL*/) {
	  fd = open_port("/dev/ttyACM0");
    
    time(&end_json_write);  //초 간격 확인을 위함 end_json_write - start_json_write가 >=10 이여야 한다.

		memset(read_buf, 0, BUFFER_SIZE);
		read_buffer_size = read_Buffer(fd, read_buf);
		if (read_buffer_size > 0) {
            //printf("%s",str);
            read_gps(read_buf);
            parsing_data();
            if(data_Struct.parseData_flag==1 && data_Struct.use_Flag==1){
              data_Struct.parseData_flag=0;
              data_Struct.use_Flag=0;
            }
            else{
              //printf("data_Struct.parseData_flag = %d\n",data_Struct.parseData_flag);
              //printf("data_Struct.use_Flag = %d\n",data_Struct.use_Flag);
              printf("수신 실패\n");
              //reset_data(dataInfo);
            }

            //format : UTC 위도 경도  
            char dataInfo[MAX_DATA_INFO];
            reset_data(dataInfo);
            int loc = 0;
            char gap[] = " ";

            for (int i = 0; i < 3; i++) {
                switch (i) {
                case 0:	// UTC
                    memcpy(dataInfo, data_Struct.utc_time, sizeof(data_Struct.utc_time));
                    strcat(dataInfo, gap);
                    loc += sizeof(data_Struct.utc_time);
                    //printf("sizeof = %ld\n",sizeof(data_Struct.utc_time));
                    break;
                case 1:	// 위도
                    strcat(dataInfo, data_Struct.slatitude);
                    strcat(dataInfo, gap);
                    loc += sizeof(data_Struct.slatitude);
                    break;
                case 2:	// 경도
                    //char next[] = "\n";
                    strcat(dataInfo, data_Struct.slongitude);
                    strcat(dataInfo, "\n");
                    loc += sizeof(data_Struct.slongitude);
                    break;
                }
            }
            if ((strlen(data_Struct.slatitude) != 0) && (strlen(data_Struct.slongitude) != 0)) {
                now = time(&now);
	              localtime_r(&now, &newtime);
                memset(gpsTime,0,sizeof(gpsTime));
              	sprintf(gpsTime, "%.4d-%.2d-%.2dT%.2d:%.2d:%.2d", newtime.tm_year + 1900, newtime.tm_mon + 1, newtime.tm_mday, newtime.tm_hour, newtime.tm_min, newtime.tm_sec);
                writeLocationDataJSONToFile(fileName, gpsTime, fw, start_flag);
                start_flag = 1;
                //sleep(10);
                reset_struct();
            }
            
        }
        
  close(fd);
	}

	if (ret = fwrite(json_backData, sizeof(char), strlen(json_backData), fw)) {
		printf("json_backData Failed Write = %d\n",ret);
	}


	fclose(fw);

/*
	printf("sec        : %d\n", newtime.tm_sec);
	printf("min        : %d\n", newtime.tm_min);
	printf("hour       : %d\n", newtime.tm_hour);
	printf("day        : %d\n", newtime.tm_mday);
	printf("month      : %d\n", newtime.tm_mon + 1);
	printf("year       : %d\n", newtime.tm_year + 1900);
	printf("weekday    : %d\n", newtime.tm_wday);
	printf("total      : %d\n", newtime.tm_yday);
	printf("summerTiem : %d\n", newtime.tm_isdst);
	printf("The date and time is %s", asctime_r(&newtime, buf));


	printf("ymd = %s\n", ymd);
	printf("hms = %s\n", hms);
	printf("fileName = %s\n", fileName);
*/
}

