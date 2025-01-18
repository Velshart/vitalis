document.addEventListener('DOMContentLoaded', (event) => {
    var modal = document.getElementById("deleteModal");
    var btns = document.querySelectorAll(".delete-clinic");
    var span = document.getElementsByClassName("close")[0];
    var cancelButton = document.getElementById("cancelButton");

    btns.forEach(btn => {
        btn.onclick = function(event) {
            event.preventDefault();
            var clinicId = this.getAttribute("data-id");
            document.getElementById("clinicId").value = clinicId;
            modal.style.display = "block";
        }
    });

    span.onclick = function() {
        modal.style.display = "none";
    }

    cancelButton.onclick = function() {
        modal.style.display = "none";
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
});