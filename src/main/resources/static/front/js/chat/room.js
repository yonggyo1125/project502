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

    ws.onopen = function(e) {}

    };

    ws.onclose = function(e) {

    };

    ws.onmessage = function(message) {
        console.log(message);
    };
});