const heartButtons = document.querySelectorAll(".addToWishlist");

heartButtons.forEach((button) => {
    button.addEventListener("click", () => {
        button.classList.toggle("clicked");
    });
});