from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse

from PIL import Image
import pytesseract
import io

import cv2
import numpy as np

app = FastAPI()


def preprocess_image(pil_image):

    image = np.array(pil_image)

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    gray = cv2.GaussianBlur(gray, (5, 5), 0)

    thresh = cv2.adaptiveThreshold(
        gray,
        255,
        cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
        cv2.THRESH_BINARY,
        11,
        2
    )

    return thresh


@app.post("/ocr/extract")
async def extract_text(image: UploadFile = File(...)):
    # Validate content type
    if image.content_type not in ("image/png", "image/jpeg"):
        raise HTTPException(status_code=400, detail="Invalid image type")

    try:
        image_bytes = await image.read()

        pil_image = Image.open(io.BytesIO(image_bytes))

        processed_image = preprocess_image(pil_image)

        extracted_text = pytesseract.image_to_string(
            processed_image,
            config="--psm 6"
        )

        return JSONResponse(
            content={
                "text": extracted_text.strip()
            }
        )

    except Exception as e:
        return JSONResponse(
            status_code=500,
            content={
                "error": "OCR_FAILED",
                "message": str(e)
            }
        )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=1000)
