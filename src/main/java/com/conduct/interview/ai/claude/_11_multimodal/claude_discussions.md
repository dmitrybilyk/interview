# Discussion topics — _10_multimodal

- `image_base64.py` vs `image_from_disk.py` — base64 encodes the image inline in the request. How much larger does the JSON payload get for a typical photo?
- What image formats are supported? Is there a file size limit per image?
- `multiple_images.py` — you can send several images in one message. What's the maximum number? Does the order of images in the array matter to the model?
- `pdf_pattern.py` — PDFs are sent as base64 too. How are multi-page PDFs handled — does the model see all pages?
- `vision_prompting_ambiguity.py` — what prompting techniques reduce ambiguity when asking the model about image content (e.g., bounding box references, page numbers)?
- How are image tokens calculated? Is a 4K image exactly 4× the tokens of a 1K image, or is there a fixed overhead?
- Can the model return image output (generate images)? Or is vision input-only for Claude?
- `_common.py` — what's the image media_type field and what values are valid?
- If the image contains text (screenshot, document), does OCR happen automatically or do you need to prompt for it?
- Security: if a user uploads an image containing "Ignore all previous instructions", can that affect model behaviour? How do you mitigate prompt injection via images?
