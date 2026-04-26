<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>관리자 - 시스템</title>
  <link rel="stylesheet" href="/css/admin.css">
</head>
<body>
<div class="admin-container">

  <!-- 사이드바 -->
  <nav class="sidebar">
    <h2>⚙️ 관리자</h2>
    <ul>
      <li><a href="/admin/words">📝 단어 관리</a></li>
      <li><a href="/admin/users">👥 유저 관리</a></li>
      <li><a href="/admin/stats">📊 게임 통계</a></li>
      <li><a href="/admin/system" class="active">🔧 시스템</a></li>
    </ul>
    <a href="/" class="btn-back">← 게임으로</a>
  </nav>

  <main class="admin-main">
    <h1>🔧 시스템 관리</h1>

    <!-- 성공/에러 메시지 -->
    <c:if test="${not empty successMsg}">
      <div class="success-msg">${successMsg}</div>
    </c:if>
    <c:if test="${not empty errorMsg}">
      <div class="error-msg">${errorMsg}</div>
    </c:if>

    <!-- 오늘/내일 단어 현황 -->
    <div class="stats-summary">
      <div class="stat-card">
        <p class="stat-label">오늘 (${today})</p>
        <c:choose>
          <c:when test="${todayWord != null}">
            <p class="stat-value green">${todayWord.word}</p>
          </c:when>
          <c:otherwise>
            <p class="stat-value red">없음</p>
          </c:otherwise>
        </c:choose>
      </div>
      <div class="stat-card">
        <p class="stat-label">내일 (${tomorrow})</p>
        <c:choose>
          <c:when test="${tomorrowWord != null}">
            <p class="stat-value green">${tomorrowWord.word}</p>
          </c:when>
          <c:otherwise>
            <p class="stat-value red">없음</p>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <!-- 단어 생성 -->
    <div class="card">
      <h2>🤖 단어 생성</h2>

      <!-- 특정 날짜 생성 -->
      <div class="system-row">
        <span class="system-label">특정 날짜 생성</span>
        <form method="post" action="/admin/system/generate" class="inline-form">
          <input type="date" name="playDate" class="admin-input" required />
          <button type="submit" class="btn-primary">생성</button>
        </form>
      </div>

      <!-- 특정 날짜 강제 재생성 -->
      <div class="system-row">
        <span class="system-label">특정 날짜 강제 재생성</span>
        <form method="post" action="/admin/system/regenerate"
              class="inline-form"
              onsubmit="return confirm('기존 단어를 삭제하고 새로 생성합니다. 진행할까요?')">
          <input type="date" name="playDate" class="admin-input" required />
          <button type="submit" class="btn-edit">재생성</button>
        </form>
      </div>

      <!-- 7일치 일괄 생성 -->
      <div class="system-row">
        <span class="system-label">향후 7일치 일괄 생성</span>
        <form method="post" action="/admin/system/generate-week"
              onsubmit="return confirm('향후 7일치 단어를 생성합니다. 진행할까요?')">
          <button type="submit" class="btn-secondary">🤖 7일치 생성</button>
        </form>
      </div>
    </div>

    <!-- 스케줄러 상태 -->
    <div class="card">
      <h2>⏰ 스케줄러 상태</h2>
      <div class="system-row">
        <span class="system-label">실행 주기</span>
        <span class="badge badge-local">매일 23:50 자동 실행</span>
      </div>
      <div class="system-row">
        <span class="system-label">다음 실행</span>
        <span>내일 00:00 이전 단어 생성</span>
      </div>
    </div>

  </main>
</div>
</body>
</html>