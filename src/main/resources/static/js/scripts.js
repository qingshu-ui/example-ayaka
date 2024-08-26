document.getElementById('file-upload').addEventListener('change', function (e) {
    let fileName = e.target.files[0].name;
    document.getElementById('file-name').textContent = '已选择文件: ' + fileName;

    let reader = new FileReader();
    reader.onload = function (event) {
        document.getElementById('uploaded-image').src = event.target.result;
        document.getElementById('results').style.display = 'block';
    };
    reader.readAsDataURL(e.target.files[0]);
});