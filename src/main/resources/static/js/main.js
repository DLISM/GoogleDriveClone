const createFolder = document.querySelector("#create__folder");

createFolder.addEventListener("click", ()=>{
    let form = document.querySelector("#from__create__folder");
    if(form.classList.contains("open")){
        form.classList.remove("open")
    }else {
        form.classList.add("open")
    }
})

const deleteForm =document.querySelector("#from__delete");
deleteForm.addEventListener("submit", (evt)=>{
    evt.preventDefault();
    let deleteItems= document.querySelectorAll(".deleteItem")
    let userFilesContainer = document.querySelector(".user-files")
    let filesList="";
    let deleteFormInput = document.querySelector("#from__delete_input");

    if(deleteItems.length==0){
        userFilesContainer.classList.add("active")
    }else {
        userFilesContainer.classList.remove("active")

        deleteItems.forEach(item => {
            filesList += item.querySelector("p").textContent + ", "
            item.classList.remove("deleteItem")
        })

        deleteFormInput.value = filesList
        deleteForm.submit()
    }

})


const items = document.querySelectorAll(".file-item");
items.forEach(item=>{
    item.addEventListener("click",()=>{
        if(document.querySelector(".user-files").classList.contains("active")) {
            if (item.classList.contains("deleteItem")) {
                item.classList.remove("deleteItem")
            } else {
                item.classList.add("deleteItem")
            }
        }
    })
})