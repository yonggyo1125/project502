window.addEventListener("DOMContentLoaded", function() {
    const searchCenterEl = document.getElementById("search_center");
    const selectCenterEl = document.getElementById("select_center");

    searchCenterEl.addEventListener("keyup", function() {
        const { ajaxLoad } = commonLib;
        const skey = this.value.trim();

        ajaxLoad('GET', `/api/reservation/center?skey=${skey}`, null, 'json')
            .then(res => {
                if (!res.success) {
                    return;
                }


                const items = res.data;
                selectCenterEl.innerHTML = "<option value=''>- 선택하세요 -</option>";
                for (const item of items) {
                    const option = document.createElement("option");
                    option.value=item.ccode;

                    const optionText = document.createTextNode(item.cname);
                    option.appendChild(optionText);
                    selectCenterEl.appendChild(option);
                }
            })
            .catch(err => console.error(err));
    });

    selectCenterEl.addEventListener("change", function() {
        if (this.classList.contains("refresh")) {
            location.href=`?cCode=${this.value}`;
        }
    });
});