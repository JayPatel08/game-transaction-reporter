document.addEventListener('DOMContentLoaded', function () {
    const searchForm = document.getElementById('searchForm');
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');
    const jsErrorMessage = document.getElementById('jsErrorMessage');

    if (searchForm && startDate && endDate) {
        searchForm.addEventListener('submit', function (event) {
            if (startDate.value && endDate.value && new Date(startDate.value) > new Date(endDate.value)) {
                event.preventDefault(); // Stop sending request to backend
                if (jsErrorMessage) {
                    jsErrorMessage.textContent = "Start date must be less than or equal to End date.";
                    jsErrorMessage.style.display = "block";
                }
                startDate.focus();
            } else if (jsErrorMessage) {
                jsErrorMessage.style.display = "none";
            }
        });
    }
});
