function interactArticle(elem, interaction, body) {
    let xhr = new XMLHttpRequest();
    let article = elem.closest("article");
    let id = article.dataset.id;
    xhr.open("POST", "/post/" + id + "?interaction=" + interaction);
    xhr.onreadystatechange = function() {
        console.log(xhr.readyState);
        if (xhr.readyState === XMLHttpRequest.DONE) {
            article.outerHTML = xhr.responseText;
        }
    };
    xhr.send(body);
}

function likePost(event) {
    interactArticle(event.target, "like", null);
}

function unlikePost(event) {
    interactArticle(event.target, "unlike", null);
}

function commentKeyPress(event) {
    if (event.keyCode === 13) {
        event.preventDefault();
        let body = event.target.value.trim();
        if (body !== "") {
            event.target.disabled = true;
            interactArticle(event.target, "reply", body);
        }
    }
}


