from django.shortcuts import render
from django.http import JsonResponse
import jwt_app
import json
from user_log.models import *
from django.views.decorators.csrf import csrf_exempt
from .models import File

# Create your views here.

@csrf_exempt
def log_get_list(request):
    if request.method == "GET":
        return render(request, 'users/main.html')

    elif request.method == 'POST':
        # POST 요청에서의 데이터는 request.POST 딕셔너리에 저장됩니다.

        received_data = json.loads(request.body)
        userjwt = received_data.get('jwt')
        userid=jwt_app.validate_token(userjwt, "tkpo")
        userid=userid["email"]
        user0=Driveuser.objects.filter(id=userid)
        if user0.exists():
            user1=Driveuserlog.objects.filter(userid=userid)
            print(user1)
            if user1.exists():
                return JsonResponse(list(user1.values()), safe=False)
            else:
                resp_data={
                    'success': 0,
                }
                return JsonResponse(resp_data)

            #쿼리셋이 비어있지 않으면 DB에 해당 값이 존재합니다.
        else:
            resp_data={
                'success': 0,
            }
            return JsonResponse(resp_data)
    else:
        # POST 요청이 아닌 경우에는 오류 응답을 반환합니다.
        return JsonResponse({'error': 'POST 요청이 필요합니다.'}, status=400)
@csrf_exempt
def log_get(request):
    if request.method == "GET":
        return render(request, 'users/main.html')

    elif request.method == 'POST':
        # POST 요청에서의 데이터는 request.POST 딕셔너리에 저장됩니다.

        received_data = json.loads(request.body)
        userjwt = received_data.get('jwt')
        userid=jwt_app.validate_token(userjwt, "tkpo")
        userid=userid["email"]
        fileid=received_data.get('id')
        user0=Driveuser.objects.filter(id=userid)
        if user0.exists():
            user1=Driveuserlog.objects.filter(id=fileid)
            file_path = user1[0].drive_log
            try:
                with open(file_path, 'r') as file:
                    file_content = file.read()  # 파일 내용을 읽음
                    if file_content:  # 파일 내용이 비어있지 않은 경우
                        json_data = json.loads(file_content)  # JSON 문자열을 파이썬 객체로 변환
                    else:
                        print("파일 내용이 비어있습니다.")
            except FileNotFoundError:
                print(f"파일 '{file_path}'를 찾을 수 없습니다.")
            except Exception as e:
                print(f"파일을 읽는 동안 오류가 발생했습니다: {e}")

            return JsonResponse(json_data)

            #쿼리셋이 비어있지 않으면 DB에 해당 값이 존재합니다.
        else:
            resp_data={
                'success': 0,
            }
            return JsonResponse(resp_data)
    else:
        # POST 요청이 아닌 경우에는 오류 응답을 반환합니다.
        return JsonResponse({'error': 'POST 요청이 필요합니다.'}, status=400)

@csrf_exempt
def log_put(request):

    if request.method == "GET":
        return render(request, 'users/main.html')

    elif request.method == 'POST':


        # POST 요청에서의 데이터는 request.POST 딕셔너리에 저장됩니다.

        json_data = request.POST.get('json_data')
        received_data = json.loads(json_data)
        #received_data = json.loads(request.body)
        userjwt = received_data.get('jwt')
        userid=jwt_app.validate_token(userjwt, "tkpo")
        userid=userid["email"]
        start_time = received_data.get('start_time')
        start_location_lati = received_data.get('start_location_lati')
        start_location_longi = received_data.get('start_location_longi')
        end_time = received_data.get('end_time')
        end_location_lati = received_data.get('end_location_lati')
        end_location_longi = received_data.get('end_location_longi')
        user0=Driveuser.objects.filter(id=userid)

        if user0.exists():
        #쿼리셋이 비어있지 않으면 DB에 해당 값이 존재합니다
            uploaded_file = request.FILES.get('file')
            if uploaded_file:
                file_instance = File(file=uploaded_file)
                file_instance.save()
                file_path = file_instance.file.path

            print("파일이 저장된 경로:", file_path)
            driveuser_instance = Driveuser.objects.get(id=userid)
            Driveuserlog.objects.create(userid=driveuser_instance,
                                    start_time=start_time,
                                    start_location_lati=start_location_lati,
                                    start_location_longi=start_location_longi,
                                    end_time=end_time,
                                    end_location_lati=end_location_lati,
                                    end_location_longi=end_location_longi,
                                    drive_log=file_path
                                    )
            resp_data={
                'success': 1
            }
            return JsonResponse(resp_data)
        # Driveuser 모델에서 특정 사용자를 가져옴
        # 이후 작업 수행
        else:
            resp_data={
                'success': 0
            }
            return JsonResponse(resp_data)
    else:
        # POST 요청이 아닌 경우에는 오류 응답을 반환합니다.
        return JsonResponse({'error': 'POST 요청이 필요합니다.'}, status=400)