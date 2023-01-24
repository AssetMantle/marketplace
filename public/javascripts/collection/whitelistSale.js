function setSoldNFTProgressBar(){
    let progressBar = document.querySelector('.progressBar > span');
    let totalNFTs = progressBar.getAttribute("data-totalNFT");
    let soldNFTs = progressBar.getAttribute("data-soldNFT");
    let progress = (soldNFTs*100) / totalNFTs;
    let soldPercentage = document.querySelector(".analysisTitle .analysisPercentage")
    soldPercentage.textContent = "("+progress.toFixed(2)+"%)";
    for(let i = 0; i < progress; i++) {
        progressBar.style.width = i + '%';
    }
}