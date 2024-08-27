$('#file-upload').on('change', function (e) {
    let file = e.target.files[0];
    let fileName = file.name
    $('#file-name').text('已选择文件' + fileName)

    let reader = new FileReader()

    reader.onload = function (event) {
        // $('#uploaded-image').attr('src', event.target.result)
        // $('#results').show()
        if (typeof event.target.result === 'string') {
            processImage(event.target.result, file)
        } else {
            console.log("FileReader result is not a string");
        }
    }
    reader.readAsDataURL(e.target.files[0])

    function processImage(imgSrc, file) {
        // Create FormData object
        let formData = new FormData()
        formData.append('image', file)

        // Send the image file to the server
        $.ajax({
            url: '/api/yolo/recognize',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: (response) => handelResponse(response, imgSrc),
            error: handleError
        })
    }


    function handelResponse(response, imgSrc) {
        console.log(response)

        let img = new Image()
        img.src = imgSrc

        img.onload = function () {
            let canvas = document.createElement('canvas');
            let ctx = canvas.getContext('2d');

            // Set canvas size to match imgae size
            canvas.width = img.width;
            canvas.height = img.height;

            // Draw the image on the canvas
            ctx.drawImage(img, 0, 0);

            // Draw bounding boxes

            let detections = response['detections']
            detections.forEach(detection => {
                let bbox = detection['bbox']
                let label = detection['label']
                let confidence = detection['confidence']

                // Draw bounding box
                ctx.beginPath()
                ctx.rect(bbox[0], bbox[1], bbox[2] - bbox[0], bbox[3] - bbox[1]);
                ctx.lineWidth = 2
                ctx.strokeStyle = 'red'
                ctx.stroke()
                ctx.closePath();

                // Draw label and confidence above the bounding box
                ctx.font = '16px Arial'
                ctx.fillStyle = 'red'
                ctx.textAlign = 'left'
                ctx.textBaseline = 'bottom'

                // Draw label
                let labelText = label
                let confidenceText = (confidence * 100).toFixed(1) + "%"
                let labelWidth = ctx.measureText(labelText).width
                let confidenceWidth = ctx.measureText(confidenceText).width

                // Position for label and confidence
                let x = bbox[0]
                let y = bbox[1] - 5

                // Draw label
                ctx.fillText(labelText, x, y)

                // Draw confidence percentage
                ctx.fillText(confidenceText, x + labelWidth + 10, y)

                $('.detections-list').append(`<div class="detection-item">
                    <span class="detection-label">${label}</span>
                    <br>
                    <span class="detection-confidence">置信度: ${confidence}</span>
                    <br>
                    <span>边界框: ${bbox}</span>
                </div>`)
            })

            // Convert canvas to Data URL
            let dataUrl = canvas.toDataURL('image/png');

            // Set the data URL as the src of the image
            $('#uploaded-image').attr('src', dataUrl)
            $('#results').show()
        }

    }

    function handleError(xhr, status, error) {
        let errorMessage = xhr["responseJSON"]["error"]
        // console.log(errorMessage)
        let errorView = $('#error')
        errorView.find('.error-msg').html(`
            <div class="error-msg-t">
               <span>错误: ${errorMessage}</span>
            </div> 
        `)
        errorView.show()
    }
})