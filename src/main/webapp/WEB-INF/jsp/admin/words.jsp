<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 - 단어 관리</title>
    <link rel="stylesheet" href="/css/admin.css">
</head>
<body>
<div class="admin-container">

    <!-- 사이드바 -->
    <nav class="sidebar">
        <h2>⚙️ 관리자</h2>
        <ul>
            <li><a href="/admin/words" class="active">📝 단어 관리</a></li>
            <li><a href="/admin/users">👥 유저 관리</a></li>
            <li><a href="/admin/stats">📊 게임 통계</a></li>
            <li><a href="/admin/system">🔧 시스템</a></li>
        </ul>
        <a href="/" class="btn-back">← 게임으로</a>
    </nav>

    <!-- 메인 컨텐츠 -->
    <main class="admin-main">
        <h1>📝 단어 관리</h1>

        <!-- 에러 메시지 -->
        <c:if test="${not empty errorMsg}">
            <div class="error-msg">${errorMsg}</div>
        </c:if>

        <!-- 단어 수동 등록 -->
        <div class="card">
            <h2>단어 등록</h2>
            <form method="post" action="/admin/words/add" class="add-form">
                <input type="text"
                       name="word"
                       placeholder="5글자 단어"
                       maxlength="5"
                       class="admin-input"
                       required />
                <input type="date"
                       name="playDate"
                       class="admin-input"
                       required />
                <button type="submit" class="btn-primary">등록</button>
            </form>

            <!-- Claude로 자동 생성 -->
            <form method="post" action="/admin/words/generate" class="add-form" style="margin-top:10px">
                <input type="date" name="playDate" class="admin-input" required />
                <button type="submit" class="btn-secondary">🤖 Claude로 생성</button>
            </form>
        </div>

        <!-- 단어 목록 -->
        <div class="card">
            <h2>단어 목록 (${words.size()}개)</h2>
            <table class="admin-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>날짜</th>
                    <th>단어</th>
                    <th>수정</th>
                    <th>삭제</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="word" items="${words}">
                    <tr>
                        <td>${word.id}</td>
                        <td>${word.playDate}</td>
                        <td><strong>${word.word}</strong></td>
                        <td>
                            <form method="post" action="/admin/words/edit" class="inline-form">
                                <input type="hidden" name="id" value="${word.id}" />
                                <input type="text"
                                       name="word"
                                       value="${word.word}"
                                       maxlength="5"
                                       class="admin-input-sm" />
                                <button type="submit" class="btn-edit">수정</button>
                            </form>
                        </td>
                        <td>
                            <form method="post" action="/admin/words/delete"
                                  onsubmit="return confirm('삭제하시겠습니까?')">
                                <input type="hidden" name="id" value="${word.id}" />
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