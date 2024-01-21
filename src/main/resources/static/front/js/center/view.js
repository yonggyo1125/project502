window.addEventListener("DOMContentLoaded", function() {
    /* 헌혈의 집 지도 노출 처리 S */
    const { map } = commonLib;
    const addressEl = document.querySelector("input[name='address']");
    if (addressEl && addressEl.value.trim()) {
        map.load("center_map", 500, 400, {
            zoom: 5,
            address : addressEl.value.trim(),
            title: addressEl.dataset.cName,
            selectable: false
        });
    }
    /* 헌혈의 집 지도 노출 처리 E */
});

