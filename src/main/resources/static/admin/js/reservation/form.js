window.addEventListener("DOMContentLoaded", function() {
    const { ajaxLoad } = commonLib;
    const yearMonthButtons = document.querySelectorAll(".year_month button");
    for (const button of yearMonthButtons) {
        button.addEventListener("click", function() {
            const dataset = this.dataset;
            const cCode = dataset.cCode;
            const year = dataset.year;
            const month = dataset.month;

            const url = `/admin/reservation/calendar?cCode=${cCode}&year=${year}&month=${month}`;
            ajaxLoad('GET', url)
                .then(res => console.log(res))
                .catch(err => console.error(err));
        });
    }
});