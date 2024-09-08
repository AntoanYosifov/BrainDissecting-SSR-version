window.onload = function () {
    const urlParams = new URLSearchParams(window.location.search);
    const openArticleId = urlParams.get('open');

    if(openArticleId) {
        const commentSection = document.getElementById('collapse-comments-' + openArticleId);
        commentSection.classList.add('show');
    }

    const fragment = window.location.hash;

    if(fragment && fragment.startsWith('#comment-')) {
        const commentId = parseInt(fragment.replace('#comment-', ''), 10);

        if(commentId > 3) {
            const targetCommentId = commentId - 2;
            const targetComment = document.getElementById('comment-' + targetCommentId);


            if(targetComment) {
                targetComment.scrollIntoView({behavior: 'smooth'});
            }

        }
    }
}

