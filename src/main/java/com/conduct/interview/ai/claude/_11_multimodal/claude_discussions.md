# Discussion topics — _10_multimodal

- `image_base64.py` vs `image_from_disk.py` — base64 encodes the image inline in the request. How much larger does the JSON payload get for a typical photo?
- What image formats are supported? Is there a file size limit per image?
- `multiple_images.py` — you can send several images in one message. What's the maximum number? Does the order of images in the array matter to the model?
- `pdf_pattern.py` — PDFs are sent as base64 too. How are multi-page PDFs handled — does the model see all pages?
  **A:** Same `document` content block as an image (`type`/`source`/`media_type`/`data`). Claude
  reads a PDF by rendering each page as an image internally, not by plain text extraction — that's
  why it can answer about tables/charts/layout, but it also means text fidelity is vision-based, not
  pixel-perfect OCR (our own run returned "test PD" instead of "test PDF."). Cost gotcha we measured
  live: a near-empty one-line PDF still cost 1593 input tokens — a page is priced roughly like an
  image regardless of how little text is on it. Put the `document` block before the `text` block in
  `content`. Limits: 32MB/request, up to 100 pages (200k-context models) or more (1M-context models).
  For the same PDF reused across many requests, upload once via the Files API and reference by
  `file_id` instead of re-sending base64 every time.
- `vision_prompting_ambiguity.py` — what prompting techniques reduce ambiguity when asking the model about image content (e.g., bounding box references, page numbers)?
- How are image tokens calculated? Is a 4K image exactly 4× the tokens of a 1K image, or is there a fixed overhead?
- Can the model return image output (generate images)? Or is vision input-only for Claude?
- `_common.py` — what's the image media_type field and what values are valid?
- If the image contains text (screenshot, document), does OCR happen automatically or do you need to prompt for it?
- Security: if a user uploads an image containing "Ignore all previous instructions", can that affect model behaviour? How do you mitigate prompt injection via images?
