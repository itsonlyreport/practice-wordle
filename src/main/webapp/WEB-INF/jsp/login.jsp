<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Wordle - 로그인</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>WORDLE</h1>
        <p class="subtitle">로그인 후 게임을 시작하세요!</p>
    </header>

    <div class="login-box">

        <!-- 에러 메시지 -->
        <c:if test="${not empty errorMsg}">
            <div class="error-msg">${errorMsg}</div>
        </c:if>

        <!-- 일반 로그인 폼 -->
        <form method="post" action="${pageContext.request.contextPath}/login" class="login-form">
            <input type="text"
                   name="username"
                   placeholder="아이디"
                   class="login-input"
                   autocomplete="username"
                   required />
            <input type="password"
                   name="password"
                   placeholder="비밀번호"
                   class="login-input"
                   autocomplete="current-password"
                   required />
            <button type="submit" class="btn-login">로그인</button>
        </form>

        <!-- 회원가입 토글 -->
        <div class="toggle-register">
            <span>계정이 없으신가요?</span>
            <a href="#" id="toggleRegister">회원가입</a>
        </div>

        <!-- 회원가입 폼 (기본 숨김) -->
        <form method="post"
              action="${pageContext.request.contextPath}/register"
              class="login-form"
              id="registerForm"
              style="display:none;">
            <input type="text"
                   name="username"
                   placeholder="아이디"
                   class="login-input"
                   required />
            <input type="password"
                   name="password"
                   placeholder="비밀번호"
                   class="login-input"
                   required />
            <input type="email"
                   name="email"
                   placeholder="이메일"
                   class="login-input"
                   required />
            <button type="submit" class="btn-login">회원가입</button>
        </form>

        <div class="divider">또는</div>

        <!-- 구글 로그인 -->
        <a href="https://accounts.google.com/o/oauth2/v2/auth?client_id=${googleClientId}&redirect_uri=${googleRedirectUri}&response_type=code&scope=email%20profile"
           class="btn-google">
            🔵 구글로 로그인
        </a>

        <!-- 게스트 -->
        <a href="${pageContext.request.contextPath}/guest" class="btn-guest">
            👾 로그인 없이 게임하기
        </a>

    </div>
</div>

<script>
    // 회원가입 폼 토글
    const toggleBtn    = document.getElementById('toggleRegister');
    const registerForm = document.getElementById('registerForm');
    const loginForm    = document.querySelector('.login-form');
    const toggleText   = document.querySelector('.toggle-register span');

    let isRegister = false;

    toggleBtn.addEventListener('click', (e) => {
        e.preventDefault();
        isRegister = !isRegister;

        if (isRegister) {
            loginForm.style.display    = 'none';
            registerForm.style.display = 'flex';
            toggleBtn.textContent      = '로그인으로 돌아가기';
            toggleText.textContent     = '이미 계정이 있으신가요?';
        } else {
            loginForm.style.display    = 'flex';
            registerForm.style.display = 'none';
            toggleBtn.textContent      = '회원가입';
            toggleText.textContent     = '계정이 없으신가요?';
        }
    });
</script>
</body>
</html>