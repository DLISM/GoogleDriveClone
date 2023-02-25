const createFolder = document.querySelector("#create__folder");

createFolder.addEventListener("click", ()=>{
    let form = document.querySelector("#from__create__folder");
    if(form.classList.contains("open")){
        form.classList.remove("open")
    }else {
        form.classList.add("open")
    }
})