<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>관리자 - 게임 통계</title>
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
      <li><a href="/admin/stats" class="active">📊 게임 통계</a></li>
      <li><a href="/admin/system">🔧 시스템</a></li>
    </ul>
    <a href="/" class="btn-back">← 게임으로</a>
  </nav>

  <main class="admin-main">
    <h1>📊 게임 통계</h1>

    <!-- 전체 통계 요약 -->
    <div class="stats-summary">
      <div class="stat-card">
        <p class="stat-label">총 플레이</p>
        <p class="stat-value">${totalStats.totalCount}<span>회</span></p>
      </div>
      <div class="stat-card">
        <p class="stat-label">총 성공</p>
        <p class="stat-value green">${totalStats.winCount}<span>회</span></p>
      </div>
      <div class="stat-card">
        <p class="stat-label">총 실패</p>
        <p class="stat-value red">${totalStats.loseCount}<span>회</span></p>
      </div>
      <div class="stat-card">
        <p class="stat-label">전체 성공률</p>
        <p class="stat-value yellow">${totalStats.winRate}<span>%</span></p>
      </div>
      <div class="stat-card">
        <p class="stat-label">평균 시도</p>
        <p class="stat-value">${totalStats.avgTryCount}<span>회</span></p>
      </div>
    </div>

    <!-- 날짜별 통계 -->
    <div class="card">
      <h2>날짜별 통계</h2>
      <table class="admin-table">
        <thead>
        <tr>
          <th>날짜</th>
          <th>총 플레이</th>
          <th>성공</th>
          <th>실패</th>
          <th>성공률</th>
          <th>평균 시도</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="stat" items="${dailyStats}">
          <tr>
            <td>${stat.playDate}</td>
            <td>${stat.totalCount}회</td>
            <td class="green">${stat.winCount}회</td>
            <td class="red">${stat.loseCount}회</td>
            <td>
              <div class="progress-bar">
                <div class="progress-fill" style="width:${stat.winRate}%"></div>
                <span>${stat.winRate}%</span>
              </div>
            </td>
            <td>${stat.avgTryCount}회</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>

    <!-- 유저별 전적 -->
    <div class="card">
      <h2>유저별 전적</h2>
      <table class="admin-table">
        <thead>
        <tr>
          <th>순위</th>
          <th>아이디</th>
          <th>이메일</th>
          <th>로그인</th>
          <th>총 플레이</th>
          <th>성공</th>
          <th>성공률</th>
          <th>평균 시도</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="stat" items="${userStats}" varStatus="status">
          <tr>
            <td>
              <c:choose>
                <c:when test="${status.index == 0}">🥇</c:when>
                <c:when test="${status.index == 1}">🥈</c:when>
                <c:when test="${status.index == 2}">🥉</c:when>
                <c:otherwise>${status.index + 1}</c:otherwise>
              </c:choose>
            </td>
            <td>${stat.username != null ? stat.username : '-'}</td>
            <td>${stat.email != null ? stat.email : '-'}</td>
            <td>
                                <span class="badge ${stat.loginType == 'GOOGLE' ? 'badge-google' : 'badge-local'}">
                                    ${stat.loginType}
                                </span>
            </td>
            <td>${stat.totalCount}회</td>
            <td class="green">${stat.winCount}회</td>
            <td>
              <div class="progress-bar">
                <div class="progress-fill" style="width:${stat.winRate}%"></div>
                <span>${stat.winRate}%</span>
              </div>
            </td>
            <td>${stat.avgTryCount}회</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </main>
</div>
</body>
</html>