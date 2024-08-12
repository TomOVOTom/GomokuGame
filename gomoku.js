const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/game', function (message) {
        const move = JSON.parse(message.body);
        handleMove(move);
    });
});

const board = document.getElementById('board');
const cells = [];

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

function makeMove(x, y) {
    const player = 'X'; // 或 'O'，根据实际情况设置
    const move = { x, y, player };
    console.log('Sending move:', move);
    stompClient.send('/app/move', {}, JSON.stringify(move));
}

function handleMove(move) {
    console.log('Received move:', move);
    const cell = cells.find(c => c.dataset.x == move.x && c.dataset.y == move.y);
    if (cell) {
        cell.textContent = move.player; // 显示棋子
        if (move.winner) {
            alert(`${move.winner} wins!`);
        } else if (move.invalid) {
            alert('Invalid move!');
        }
    }
}