import requests
import json

url = "http://3.39.187.161:8000/user_sleep/sleep_get_list"
#url = "http://127.0.0.1:8000/user_sleep/sleep_get_list"
data = {
    'jwt': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MjExNDExOTEsImVtYWlsIjoidGVzdCJ9._uYZdTNvoXtVfBivE9wB3aMeoYS4BWp8wXWCxxmV09Y'
    #'jwt': 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MjQzOTc1MTUsImVtYWlsIjoiYSJ9.kPuOs32gliJF15DXObBOIg6TsLVuQs7c6VeDZrxTOhM'
}
json_data = json.dumps(data)
#headers = {"Authorization": "token"}
#response = requests.post(url, data=data)
try:
    # POST 요청을 보냅니다.
    response = requests.post(url,data=json_data)
    data = response.json()
    print(data)
    # 서버로부터 받은 응답을 출력합니다.
    print("응답 코드:", response.status_code)
    print("응답 내용:", response.text)
except requests.exceptions.RequestException as e:
    # 요청이 실패한 경우 예외를 처리합니다.
    print("요청 실패:", e)