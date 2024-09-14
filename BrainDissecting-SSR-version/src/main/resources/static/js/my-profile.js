document.addEventListener("DOMContentLoaded", function showAndHideEditSection() {
    const profileButton = document.getElementById('editProfileButton');
    const editProfileSection = document.getElementById("editProfileSection");

    profileButton.addEventListener('click', function () {

        if (editProfileSection.classList.contains('d-none')) {
            editProfileSection.classList.remove('d-none');
            editProfileSection.classList.add('d-block');
        } else {
            editProfileSection.classList.remove('d-block');
            editProfileSection.classList.add('d-none');
        }

        console.log('Clicked!!!');


    });

    // editProfileSection.classList.remove('d-block');
    // editProfileSection.classList.add('d-none');
});