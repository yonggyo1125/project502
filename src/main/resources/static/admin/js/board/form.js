window.addEventListener("DOMContentLoaded", function() {
    const { loadEditor } = commonLib;


    loadEditor("html_top")
        .then(editor => window.editor1 = editor)
        .catch(err => console.error(err));

    loadEditor("html_bottom")
        .then(editor => window.editor2 = editor)
        .catch(err => console.error(err));
});

/**
* 파일 업로드 후 후속처리 함수
*
*/
function callbackFileUpload(files) {
    if (!files || files.length == 0) {
        return;
    }



    for (const file of files) {
       const editor = file.location == 'html_bottom' ? editor2 : editor1;
       editor.execute('insertImage', { source: file.fileUrl });
    }
}