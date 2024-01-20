const boardLib = {
    /**
    * 게시글 찜하기
    *   - 로그인 회원 정보를 가지고 저장

    * @param bSeq : 게시글 번호
    *
    */
    save(bSeq) {

    },
    /**
    * 찜하기 해제
    *
    * @param bSeq : 게시글 번호
    */
    deleteSave(bSeq) {

    }
}

window.addEventListener("DOMContentLoaded", function() {
    /* 찜하기 처리 S */
    const savePosts = document.getElementsByClassName("save_post");
    for (const el of savePosts) {
        el.addEventListener("click", function() {
            // data-seq : 게시글 번호
            const bSeq = this.dataset.seq;

            boardLib.save(bSeq);

        });
    }

    /* 찜하기 처리 E */

    /* 찜하기 해제 처리 S */

    /* 찜하기 해제 처리 E */
});