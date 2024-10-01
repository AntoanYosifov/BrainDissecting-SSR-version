function toggleContent(articleId) {
    const dots = document.getElementById("dots-" + articleId);
    const moreText = document.getElementById("more-" + articleId);
    const btnText = document.getElementById("myBtn-" + articleId);

    console.log('Clicked')

    if (dots.style.display === "none") {
        dots.style.display = "inline";
        btnText.textContent = "See More";
        moreText.style.display = "none";
    } else {
        dots.style.display = "none";
        btnText.textContent = "See Less";
        moreText.style.display = "inline";
    }
}