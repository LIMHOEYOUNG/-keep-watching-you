#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

int main() {
    // 로그인한 사용자의 이름 가져오기
    char *login_name = getlogin();
    if (login_name != NULL) {
        printf("로그인한 사용자: %s\n", login_name);
    } else {
        perror("로그인한 사용자를 가져오는 데 실패했습니다");
        // 환경 변수에서 사용자 이름 가져오기
        char *user_name = getenv("USER");
        if (user_name != NULL) {
            printf("환경 변수에서 사용자: %s\n", user_name);
        } else {
            perror("환경 변수에서 사용자를 가져오는 데 실패했습니다");
            return 1;
        }
    }
    return 0;
}

