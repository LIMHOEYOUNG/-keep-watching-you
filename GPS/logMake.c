#include <stdio.h>
#include <time.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>

#include "com.h"

#define MAX_LEN 600
#define MAX_DATA_INFO 80

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
void convertArray(char *buf, char *ddd_result, char *result);


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

void show_data(){
	
	printf("UTC : %s\n",data_Struct.utc_time);
	printf("위도 : %s\n",data_Struct.slatitude);
	printf("북남 : %s\n",data_Struct.ns);
	printf("경도 : %s\n",data_Struct.slongitude);
	printf("동서 : %s\n",data_Struct.ew);
	
}

void convertArray(char *buf, char *ddd_result, char *result){
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

  //printf("ddd_result = %s\n result = %s\n",ddd_result,result);
  /*
  for(int i=2; sbuffer[i]!='\0'; i++){
    result[i]=sbuffer[i];
  }
  */
  
  /*
  char result[12];
  for(int i=2;i<=strlen(data_Struct.slatitude);i++){
    result[i-2]=data_Struct.slatitude[i];
  }
  //memcpy(dataInfo,data_Struct.utc_time,sizeof(data_Struct.utc_time));
  memcpy(data_Struct.latitude,result,sizeof(result));
  reset_data(result);
  for(int i=2;i<=strlen(data_Struct.slongitude);i++){
    result[i-2]=data_Struct.slongitude[i];
  }
  //memcpy(dataInfo,data_Struct.utc_time,sizeof(data_Struct.utc_time));
  memcpy(data_Struct.longitude,result,sizeof(result));
  reset_data(result);
  */
}

char* gpsDataToJSON(char *gpsTime) {
    char* json = (char*)malloc(200 * sizeof(char)); // 충분한 공간 할당
    printf("slatitude = %s, strlen = %ld, type = \n",data_Struct.slatitude,strlen(data_Struct.slatitude));
    printf("slongitude = %s, strlen = %ld\n",data_Struct.slongitude,strlen(data_Struct.slongitude));
    /*
    if((strlen(data_Struct.slatitude)!=0) && (strlen(data_Struct.slongitude)!=0)) {
      
    }
    */
    convertArray(data_Struct.slatitude,data_Struct.ddd_latitude,data_Struct.latitude);
    convertArray(data_Struct.slongitude,data_Struct.ddd_longitude,data_Struct.longitude);
    long ktime= strtol(data_Struct.utc_time,NULL,10);
    ktime+=90000;
    sprintf(json, "\n\t{\"time\":\"%s\",\"latitude\":%s,\"longitude\":%s}", 
              gpsTime, data_Struct.latitude, data_Struct.longitude);
    return json;
}

void writeLocationDataJSONToFile(char* fileName, char *gpsTime, FILE *file, char flag) {

    const char *json_frontData="{\n"
										"  \"date\": [";
		const char *json_backData="\n  ]\n"
									"}";

    //FILE* file = fopen(fileName, "w");
/*
    if(fwrite(json_frontData, sizeof(char), strlen(json_frontData),file)){
				printf("failed Write\n");
		}
*/
    if (file == NULL) {
        perror("Error opening file");
        return;
    }

    char* json = gpsDataToJSON(gpsTime);
    if(flag) fprintf(file,"%s",",");
    fprintf(file, "%s", json);
/*
    if(fwrite(json_backData, sizeof(char), strlen(json_backData),file)){
				printf("failed Write\n");
		}
*/
    //fclose(file);
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
	char fileName[40];
	sprintf(ymd, "%.8d", ((newtime.tm_year + 1900) * 10000) + ((newtime.tm_mon + 1) * 100) + (newtime.tm_mday));
	sprintf(hms, "%.6d", ((newtime.tm_hour) * 10000) + ((newtime.tm_min) * 100) + (newtime.tm_sec));

	char gpsTime[50];
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


	while (1/*fgets(str,MAX_LEN,fr) != NULL*/) {

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
              printf("data_Struct.parseData_flag = %d\n",data_Struct.parseData_flag);
              printf("data_Struct.use_Flag = %d\n",data_Struct.use_Flag);
              printf("수신 실패\n");
            }

            //show_data();
            //fgets(str, MAX_LEN, fr);

            //save_data(fileName);
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
                    char next[] = "\n";
                    strcat(dataInfo, data_Struct.slongitude);
                    strcat(dataInfo, next);
                    loc += sizeof(data_Struct.slongitude);
                    break;
                }
            }
            /*
            printf("===========================\n");
            printf("dataInfo = %s\n",dataInfo);
            printf("loc = %d\n",loc);
            printf("===========================\n");
            */
            if ((strlen(data_Struct.slatitude) != 0) && (strlen(data_Struct.slongitude) != 0)) {
                writeLocationDataJSONToFile(fileName, gpsTime, fw, start_flag);
                start_flag = 1;
            }
            /*
            if(fwrite(dataInfo,sizeof(char),strlen(dataInfo),fw)){
              printf("failed Write\n");
            }
            */


            /*
                        char *json_gpsData="     {\"time\": \"2024-03-16T03:38:13.776Z\",\n"
                                                "      \"latitude\": 37.3393,\n"
                                                "      \"longitude\": 126.7328}\n";

                        if(fwrite(json_gpsData, sizeof(char), strlen(json_gpsData),fw)){
                          printf("failed Write1\n");
                            }
            */
            //read_gps(str);
        }
	}
  close(fd);

	if (ret = fwrite(json_backData, sizeof(char), strlen(json_backData), fw)) {
		printf("json_backData Failed Write = %d\n",ret);
	}


	fclose(fw);

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

	/*
	char hms[20];
	char ymd[20];
	char fileName[40];
	sprintf(ymd,"%.8d",((newtime.tm_year+1900)*10000)+((newtime.tm_mon+1)*100)+(newtime.tm_mday));        sprintf(hms,"%.6d",((newtime.tm_hour)*10000)+((newtime.tm_min)*100)+(newtime.tm_sec));

	strcpy(fileName,ymd);
	strcat(fileName,hms);
	*/

	printf("ymd = %s\n", ymd);
	printf("hms = %s\n", hms);
	printf("fileName = %s\n", fileName);

	//fgets(str,MAX_LEN,fr);

	//printf("str = %s\n",str);
}
