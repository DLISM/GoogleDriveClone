const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});


const createFolder = document.querySelector("#create__folder");

createFolder.addEventListener("click", () => {
    const form = document.querySelector("#from__create__folder");
    form.classList.toggle("open");
    form.querySelector("input[name='subdirectory']").value = params.subdirectory;
});


const deleteForm = document.querySelector("#from__delete");
deleteForm.addEventListener("submit", (evt) => {
    evt.preventDefault();
    let deleteItems = document.querySelectorAll(".deleteItem")
    let userFilesContainer = document.querySelector(".user-files")
    let filesList = "";
    let deleteFormInput = document.querySelector("#from__delete_input");
    let deleteFormSubdirectory = document.querySelector("#from__delete_subcategory");

    if (deleteItems.length == 0) {
        userFilesContainer.classList.add("active")
    } else {
        userFilesContainer.classList.remove("active")

        deleteItems.forEach(item => {
            filesList += item.dataset.path + ", "
            item.classList.remove("deleteItem")
        })

        const urlParams = new URLSearchParams(window.location.search);
        const product = urlParams.get('subdirectory')
        console.log(product);

        deleteFormSubdirectory.value = product;

        deleteFormInput.value = filesList
        deleteForm.submit()
    }

})


const items = document.querySelectorAll(".file-item");
items.forEach(item => {
    item.addEventListener("click", () => {
        if (document.querySelector(".user-files").classList.contains("active")) {
            if (item.classList.contains("deleteItem")) {
                item.classList.remove("deleteItem")
            } else {
                item.classList.add("deleteItem")
            }
        }
    })
})

const directory = document.querySelectorAll(".dir");
directory.forEach(dir => {
    dir.addEventListener("dblclick", (evt) => {
        window.location = window.location.pathname + "?subdirectory=" + dir.dataset.path
    })
})

const uploadFilesBtn = document.querySelector("#uploadFilesBtn");
uploadFilesBtn.addEventListener("click", () => {
    let form__upload__files = document.querySelector("#form__upload__files");

    if (form__upload__files.querySelector("input[type='file']").value !== "") {

        form__upload__files.querySelector("input[name='subdirectory']").value = params.subdirectory
        form__upload__files.submit()

    } else {
        document.querySelector("#form__upload__files").style.display = "block"
    }

})

const uploadFolderBtn = document.querySelector("#uploadFolderBtn");


uploadFolderBtn.addEventListener("click", () => {
    let form__upload__directory = document.querySelector("#form__upload__directory");

    if (form__upload__directory.querySelector("input[type='file']").value !== "") {


        form__upload__directory.querySelector("input[name='subdirectory']").value = params.subdirectory
        form__upload__directory.submit()

    } else {
        document.querySelector("#form__upload__directory").style.display = "block"
    }

})

const editBtn = document.querySelectorAll(".edit-btn");

editBtn.forEach(btn => {
    btn.addEventListener("click", () => {
        let fileName = btn.parentElement.querySelector('p').textContent
        let path = btn.parentElement.dataset.path

        document.querySelector('#from__rename_path_input').value = path;
        document.querySelector('#from__rename_input').value = fileName;
        document.querySelector('#from__rename_subdirectory').value = params.subdirectory;

        document.querySelector("#popUp").style.display = "flex"

    })
})