const heartButtons = document.querySelectorAll(".wishlist");

heartButtons.forEach((button) => {
    button.addEventListener("click", () => {
        button.classList.toggle("clicked");
    });
});