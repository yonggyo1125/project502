window.addEventListener("DOMContentLoaded", function() {
    const { ajaxLoad } = commonLib;

    const selectTimes = document.getElementsByClassName("select_time");
    for (const el of selectTimes) {
        el.addEventListener("click", function() {
            const cCode = this.dataset.cCode;
            const date = this.dataset.date;
            const time = this.value;

            const targetEl = document.getElementById("select_person");

            const url = `/api/reservation/available_capacity?cCode=${cCode}&date=${date}&time=${time}`;
            console.log("url", url);
            ajaxLoad("GET", url, null, 'json')
                .then(res => {
                    targetEl.innerHTML = "";
                    const capacity = isNaN(res.data) ? 0 : Number(res.data);
                    if (capacity > 0) { // 신청 인원수 선택 가능
                        let html = "";
                        for (let i = 1; i <= capacity; i++) {
                            html += `<option value='${i}'>${i}명</option>`;
                        }
                        targetEl.innerHTML = html;
                    } else { // 불가능
                        targetEl.innerHTML = "<option value='0'>선택 불가</option>";
                    }
                })
                .catch(err => console.error(err));
        });
    }
});