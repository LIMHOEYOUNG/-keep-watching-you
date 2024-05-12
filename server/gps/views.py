from django.shortcuts import render
from django.http import JsonResponse
import jwt_app
import json
from user_gps.models import *
from django.views.decorators.csrf import csrf_exempt

# Create your views here.
@csrf_exempt
def gps_get(request):
    if request.method == "GET":
        return render(request, 'users/main.html')

    elif request.method == 'POST':
        # POST 요청에서의 데이터는 request.POST 딕셔너리에 저장됩니다.
        received_data = json.loads(request.body)
        userjwt = received_data.get('jwt')
        userid=jwt_app.validate_token(userjwt, "tkpo")
        userid=userid["email"]
        user0=Gps.objects.filter(userid=userid)
        if user0.exists():
            resp_data={
                'success': 1,
                'latitude': user0[0].gps_latitude,
                'longitude': user0[0].gps_longitude
            }
            return JsonResponse(resp_data)
            #쿼리셋이 비어있지 않으면 DB에 해당 값이 존재합니다.
        else:
            resp_data={
                'success': 0,
                'latitude': None,
                'longitude': None
            }
            return JsonResponse(resp_data)
    else:
        # POST 요청이 아닌 경우에는 오류 응답을 반환합니다.
        return JsonResponse({'error': 'POST 요청이 필요합니다.'}, status=400)

@csrf_exempt
def gps_put(request):
    if request.method == "GET":
        return render(request, 'users/main.html')

    elif request.method == 'POST':
# POST 요청에서의 데이터는 request.POST 딕셔너리에 저장됩니다.
        received_data = json.loads(request.body)
        userjwt = received_data.get('jwt')
        userid=jwt_app.validate_token(userjwt, "tkpo")
        userid=userid["email"]
        latitude = received_data.get('latitude')
        longitude = received_data.get('longitude')
        user0=Driveuser.objects.filter(id=userid)
        if user0.exists():
#쿼리셋이 비어있지 않으면 DB에 해당 값이 존재합니다.
            if(latitude==None and longitude==None):
                resp_data={
                    'success': 0
                }
                return JsonResponse(resp_data)
            else:
                user1=Gps.objects.filter(userid=userid)
                if user1.exists():
                    driveuser_instance = Driveuser.objects.get(id=userid)
                    Gps.objects.update(userid=driveuser_instance, gps_latitude=latitude,gps_longitude=longitude)
                    resp_data={
                        'success': 1
                    }
                    return JsonResponse(resp_data)
                else:
                    driveuser_instance = Driveuser.objects.get(id=userid)
                    Gps.objects.create(userid=driveuser_instance, gps_latitude=latitude,gps_longitude=longitude)
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