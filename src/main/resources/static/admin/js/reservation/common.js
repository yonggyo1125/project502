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
                selectCenterEl.innerHTML = "";
                for (const item of items) {
                    const option = document.createElement("option");
                    option.value=item.cCode;

                    const optionText = document.createTextNode(item.cName);
                    option.appendChild(optionText);
                    selectCenterEl.appendChild(option);
                }
            })
            .catch(err => console.error(err));
    });
});