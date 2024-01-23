window.addEventListener("DOMContentLoaded", function() {
    const { ajaxLoad } = commonLib;

    const selectTimes = document.getElementsByClassName("select_time");
    for (const el of selectTimes) {
        el.addEventListener("click", function() {
            const cCode = this.dataset.cCode;
            const date = this.dataset.date;
            const time = this.value;

            const url = `/api/reservation/available_capacity?cCode=${cCode}&date=${date}&time=${time}`;
            ajaxLoad("GET", url, null, 'json')
                .then(res => console.log(res))
                .catch(err => console.error(err));
        });
    }
});