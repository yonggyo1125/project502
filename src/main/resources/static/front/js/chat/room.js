const chat = {
    ws: null,
    roomId: null,
    init(ws) {
        this.ws = ws;
        this.roomId = frmChat.roomId.value;
    },
    send(message) {
        const nickName = frmChat.nickName.value.trim();
        if (!nickName) {
            alert("닉네임을 입력하세요.");
            frmChat.nickName.focus();
            return;
        }
        const roomId = this.roomId;
        const data = { roomId, nickName, message };
        ws.send(JSON.stringify(data));
    }
};

window.addEventListener("DOMContentLoaded", function() {
    const ws = new WebSocket("ws://localhost:3000/chat");

    chat.init(ws);

    ws.onopen = function(e) {}
        const message = `${frmChat.nickName}님 입장`;
        chat.send(message);
    };

    ws.onclose = function(e) {
        const message = `${frmChat.nickName}님 퇴장`;
        chat.send(message);
    };

    ws.onmessage = function(message) {
        console.log(message);
    };
});