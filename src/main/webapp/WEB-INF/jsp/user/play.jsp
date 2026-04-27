<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Wordle</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>WORDLE</h1>
        <p class="subtitle">5글자 단어를 6번 안에 맞춰보세요!</p>
        <div class="header-btns">
            <%-- 관리자면 관리자 페이지 버튼 표시 --%>
            <c:if test="${loginUser.admin}">
                <a href="/admin/words" class="btn-admin">⚙️ 관리자</a>
            </c:if>


            <c:choose>
                <%-- 게스트면 로그인 버튼 --%>
                <c:when test="${loginUser.loginType == 'GUEST'}">
                    <a href="/" class="btn-login-link">로그인</a>
                </c:when>
                <%-- 로그인 유저면 로그아웃 버튼 --%>
                <c:when test="${loginUser != null}">
                    <a href="/logout" class="btn-logout">로그아웃</a>
                </c:when>
                <%-- 비로그인이면 로그인 버튼 --%>
                <c:otherwise>
                    <a href="/" class="btn-login-link">로그인</a>
                </c:otherwise>
            </c:choose>
        </div>
    </header>

    <!-- 오늘 이미 완료한 경우 -->
    <c:if test="${alreadyDone}">
        <div class="already-done">
            <p>🌙 오늘은 이미 플레이했습니다!</p>
            <p class="already-done-sub">자정이 지나면 새 단어로 다시 도전할 수 있어요.</p>
        </div>
    </c:if>

    <!-- 게임 진행 중 -->
    <c:if test="${not alreadyDone}">

        <!-- 게임 보드 -->
        <div class="board" id="board">
            <c:forEach var="i" begin="0" end="${game.maxTries - 1}" varStatus="rowStatus">
                <div class="row" id="row-${rowStatus.index}">
                    <c:forEach var="j" begin="0" end="4" varStatus="colStatus">
                        <c:set var="letter"    value="" />
                        <c:set var="cellClass" value="cell" />
                        <c:if test="${rowStatus.index < game.guesses.size()}">
                            <c:set var="word"   value="${game.guesses[rowStatus.index]}" />
                            <c:set var="letter" value="${word.charAt(colStatus.index)}" />
                            <c:choose>
                                <c:when test="${game.results[rowStatus.index][colStatus.index] == 2}">
                                    <c:set var="cellClass" value="cell correct" />
                                </c:when>
                                <c:when test="${game.results[rowStatus.index][colStatus.index] == 1}">
                                    <c:set var="cellClass" value="cell present" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="cellClass" value="cell absent" />
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <div class="${cellClass}" id="cell-${rowStatus.index}-${colStatus.index}">${letter}</div>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>

        <!-- 에러 메시지 -->
        <c:if test="${not empty errorMsg}">
            <div class="error-msg" id="errorMsg">${errorMsg}</div>
        </c:if>

        <!-- 게임 종료 결과 -->
        <c:if test="${game.gameOver}">
            <div class="result-msg ${game.win ? 'win' : 'lose'}">
                <c:choose>
                    <c:when test="${game.win}">
                        🎉 정답! <strong>${game.answer.value()}</strong>
                        (${game.guesses.size()}번 만에 성공)
                    </c:when>
                    <c:otherwise>
                        😢 정답은 <strong>${game.answer.value()}</strong> 였습니다.
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <!-- 숨겨진 폼 -->
        <c:if test="${!game.gameOver}">
            <form id="guessForm" method="post" action="/play">
                <input type="hidden" name="guess" id="guessValue" />
            </form>
        </c:if>

    </c:if>
</div>

<c:if test="${not alreadyDone and !game.gameOver}">
    <script>
        const currentRow = ${game.guesses.size()};
        const gameOver   = ${game.gameOver};

        if (!gameOver) {
            let currentCol = 0;
            const letters  = Array(5).fill('');

            function getCell(col) {
                return document.getElementById('cell-' + currentRow + '-' + col);
            }

            function renderCell(col, letter) {
                const cell = getCell(col);
                if (!cell) return;
                cell.textContent = letter;
                cell.classList.toggle('filled', letter !== '');
            }

            function showError(msg) {
                let el = document.getElementById('errorMsg');
                if (!el) {
                    el = document.createElement('div');
                    el.id = 'errorMsg';
                    el.className = 'error-msg';
                    document.querySelector('.container').appendChild(el);
                }
                el.textContent = msg;
                setTimeout(() => el.textContent = '', 2000);
            }

            document.addEventListener('keydown', (e) => {
                if (e.key === 'Enter') {
                    if (letters.filter(l => l !== '').length < 5) {
                        showError('5글자를 모두 입력해주세요.');
                        return;
                    }
                    document.getElementById('guessValue').value = letters.join('');
                    document.getElementById('guessForm').submit();

                } else if (e.key === 'Backspace') {
                    if (currentCol > 0) {
                        currentCol--;
                        letters[currentCol] = '';
                        renderCell(currentCol, '');
                    }

                } else if (/^[a-zA-Z]$/.test(e.key)) {
                    if (currentCol < 5) {
                        letters[currentCol] = e.key.toUpperCase();
                        renderCell(currentCol, letters[currentCol]);
                        currentCol++;
                    }
                }
            });

            document.getElementById('row-' + currentRow)?.classList.add('active-row');
        }
    </script>
</c:if>
</body>
</html>
