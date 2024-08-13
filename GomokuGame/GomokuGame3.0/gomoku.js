const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

let currentPlayer = 'X';
let myPlayer = ''; // 将在连接时设置
const cells = []; // 存储所有单元格

// 生成棋盘
const board = document.getElementById('board');
for (let i = 0; i < 15; i++) {
    for (let j = 0; j < 15; j++) {
        const cell = document.createElement('div');
        cell.classList.add('cell');
        cell.dataset.x = i;
        cell.dataset.y = j;
        cell.addEventListener('click', () => makeMove(i, j));
        board.appendChild(cell);
        cells.push(cell);
    }
}

function respondToUndo(accepted) {
    stompClient.send('/app/undo-response', {}, JSON.stringify(accepted));
}

function requestUndo() {
    if (currentPlayer !== myPlayer) {
        alert("不是你的回合，无法请求悔棋！");
        return;
    }
    stompClient.send('/app/undo-request', {}, myPlayer);
}

function resetGame() {
    if (confirm("确定要重新开始游戏吗？")) {
        stompClient.send('/app/reset', {}, {});
    }
}

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/players', function (message) {
        const response = JSON.parse(message.body);
        console.log('Received player response:', response);
        if (response.player && !myPlayer) {
            myPlayer = response.player;
            currentPlayer = response.currentPlayer;
            updateTurnIndicator();
        }
        if (response.message) {
            alert(response.message);
        }
    });
    stompClient.subscribe('/topic/game', function (message) {
        const move = JSON.parse(message.body);
        handleMove(move);
    });
    stompClient.subscribe('/topic/undo-request', function (message) {
        const response = JSON.parse(message.body);
        if (response.player !== myPlayer) {
            if (confirm(response.message + "。是否同意？")) {
                respondToUndo(true);
            } else {
                respondToUndo(false);
            }
        }
    });
    stompClient.send('/app/join', {}, {});
});

function makeMove(x, y) {
    if (currentPlayer !== myPlayer) {
        alert("不是你的回合！");
        return;
    }
    const move = { x, y, player: myPlayer };
    console.log('Sending move:', move);
    stompClient.send('/app/move', {}, JSON.stringify(move));
}

function handleMove(move) {
    console.log('Received move:', move);
    if (move.message) {
        alert(move.message);
        if (move.message === "游戏已重置") {
            cells.forEach(cell => cell.textContent = '');
            currentPlayer = 'X';
            myPlayer = '';
            updateTurnIndicator();
        } else if (move.message === "悔棋成功" && move.undoneMovesForResponse) {
            move.undoneMovesForResponse.forEach(undoneMove => {
                const cell = cells.find(c => c.dataset.x == undoneMove.x && c.dataset.y == undoneMove.y);
                if (cell) {
                    cell.textContent = '';
                }
            });
            currentPlayer = move.nextPlayer;
            updateTurnIndicator();
        }
        return;
    }
    const cell = cells.find(c => c.dataset.x == move.x && c.dataset.y == move.y);
    if (cell) {
        if (move.invalid) {
            alert('无效的移动！');
        } else if (cell.textContent === '') {
            cell.textContent = move.player;
            if (move.winner) {
                alert(`${move.winner} 赢了！`);
            } else {
                currentPlayer = move.nextPlayer;
                updateTurnIndicator();
            }
        }
    }
}

function updateTurnIndicator() {
    const turnIndicator = document.getElementById('turnIndicator');
    const waitingMessage = document.getElementById('waitingMessage');
    if (myPlayer) {
        waitingMessage.style.display = 'none';
        const isMyTurn = currentPlayer === myPlayer;
        turnIndicator.textContent = `你是 ${myPlayer} 玩家。${isMyTurn ? '现在是你的回合' : '等待对手下棋'}`;
    } else {
        waitingMessage.style.display = 'block';
        turnIndicator.textContent = '等待加入游戏...';
    }
}

