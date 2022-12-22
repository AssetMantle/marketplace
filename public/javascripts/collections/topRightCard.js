function setCounter(inputEpoch){
    const second = 1000,
        minute = second * 60,
        hour = minute * 60,
        day = hour * 24;

    const countDown = new Date(inputEpoch*1000).getTime(),
        x = setInterval(function() {

            const now = new Date().getTime(),
                distance = countDown - now;

            document.getElementById("days").innerText = Math.floor(distance / (day)),
                document.getElementById("hours").innerText = Math.floor((distance % (day)) / (hour)),
                document.getElementById("minutes").innerText = Math.floor((distance % (hour)) / (minute)),
                document.getElementById("seconds").innerText = Math.floor((distance % (minute)) / second);

            if (distance < 0) {
                clearInterval(x);
            }
        }, 1000);
}