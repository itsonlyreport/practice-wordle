<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>관리자 - 유저 관리</title>
  <link rel="stylesheet" href="/css/admin.css">
</head>
<body>
<div class="admin-container">

  <!-- 사이드바 -->
  <nav class="sidebar">
    <h2>⚙️ 관리자</h2>
    <ul>
      <li><a href="/admin/words">📝 단어 관리</a></li>
      <li><a href="/admin/users" class="active">👥 유저 관리</a></li>
      <li><a href="/admin/stats">📊 게임 통계</a></li>
      <li><a href="/admin/system">🔧 시스템</a></li>
    </ul>
    <a href="/" class="btn-back">← 게임으로</a>
  </nav>

  <!-- 메인 컨텐츠 -->
  <main class="admin-main">
    <h1>👥 유저 관리</h1>

    <div class="card">
      <h2>전체 유저 (${users.size()}명)</h2>
      <table class="admin-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>아이디</th>
          <th>이메일</th>
          <th>로그인 타입</th>
          <th>권한</th>
          <th>권한 변경</th>
          <th>삭제</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${users}">
          <tr>
            <td>${user.id}</td>
            <td>${user.username != null ? user.username : '-'}</td>
            <td>${user.email != null ? user.email : '-'}</td>
            <td>
                                <span class="badge ${user.loginType == 'GOOGLE' ? 'badge-google' : 'badge-local'}">
                                    ${user.loginType}
                                </span>
            </td>
            <td>
                                <span class="badge ${user.role == 'ADMIN' ? 'badge-admin' : 'badge-user'}">
                                    ${user.role}
                                </span>
            </td>
            <td>
              <form method="post" action="/admin/users/role" class="inline-form">
                <input type="hidden" name="id" value="${user.id}" />
                <c:choose>
                  <c:when test="${user.role == 'ADMIN'}">
                    <input type="hidden" name="role" value="USER" />
                    <button type="submit" class="btn-edit">USER로 변경</button>
                  </c:when>
                  <c:otherwise>
                    <input type="hidden" name="role" value="ADMIN" />
                    <button type="submit" class="btn-primary">ADMIN으로</button>
                  </c:otherwise>
                </c:choose>
              </form>
            </td>
            <td>
              <form method="post" action="/admin/users/delete"
                    onsubmit="return confirm('정말 삭제하시겠습니까?')">
                <input type="hidden" name="id" value="${user.id}" />
                <button type="submit" class="btn-delete">삭제</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </main>
</div>
</body>
</html>