window.onload = function () {
    const urlParams = new URLSearchParams(window.location.search);
    const openArticleId = urlParams.get('open');

    if(openArticleId) {
        const commentSection = document.getElementById('collapse-comments-' + openArticleId);
        commentSection.classList.add('show');
    }
}