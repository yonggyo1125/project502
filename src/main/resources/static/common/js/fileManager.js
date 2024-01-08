var commonLib = commonLib || {};
/**
* 1. 파일 업로드
*
*/
commonLib.fileManager = {
    /**
    * 파일 업로드 처리
    *
    */
    update() {

    }
};


// 이벤트 처리
window.addEventListener("DOMContentLoaded", function() {
    const uploadFiles = document.getElementsByClassName("upload_files");

    const fileEl = document.createElement("input");
    fileEl.type="file";

    for (const el of uploadFiles) {
        el.addEventListener("click", function() {
            fileEl.click();
        });
    }

});