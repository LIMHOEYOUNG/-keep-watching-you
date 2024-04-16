from django.shortcuts import render

# Create your views here.
from django.http import JsonResponse
import jwt_app
import json
from join_member.models import *
from django.views.decorators.csrf import csrf_exempt
from rest_framework import viewsets
from join_member.serializers import MyModelSerializer


@csrf_exempt
def main(request):
    if request.method == "GET":
        return render(request, 'users/main.html')

    elif request.method == 'POST':
        # POST 요청에서의 데이터는 request.POST 딕셔너리에 저장됩니다.
        received_data = json.loads(request.body)
        username = received_data.get('username')
        password = received_data.get('password')
        email = received_data.get('email')
        user0=Driveuser.objects.filter(id=username)
        if user0.exists():
        # 쿼리셋이 비어있지 않으면 DB에 해당 값이 존재합니다.
            if(username == user0[0].id):
                resp_data={
                    'success': 0
                }
                return JsonResponse(resp_data)
        # 이후 작업 수행
        else:
            if(password==None):
                resp_data={
                    'success': 1
                }
                return JsonResponse(resp_data)
            else:
                Driveuser.objects.create(id=username, passwd=password,email=email)
                token= jwt_app.create_token("tkpo", username)
                resp_data={
                    'success': 1,
                    'jwt' : token
                }
                return JsonResponse(resp_data)


    else:
        # POST 요청이 아닌 경우에는 오류 응답을 반환합니다.
        return JsonResponse({'error': 'POST 요청이 필요합니다.'}, status=400)
