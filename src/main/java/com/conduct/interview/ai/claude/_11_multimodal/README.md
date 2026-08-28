# _11 — Multimodal (Images & PDFs)

Images and PDFs are content blocks alongside text. Same API call, extra block in `messages[].content`.

```python
# Image (base64 or URL)
{"type": "image", "source": {"type": "base64", "media_type": "image/png", "data": b64_str}}
{"type": "image", "source": {"type": "url",    "url": "https://..."}}

# PDF
{"type": "document", "source": {"type": "base64", "media_type": "application/pdf", "data": b64_str}}
```

**Resolve ambiguity in the prompt.** Don't say "What is this?" — say "This is a screenshot of our CI/CD dashboard. Which stage is failing?"  
Claude can't infer context it doesn't have; you can.

Limits: max 5MB per image, max 20 images per request. Supported: PNG, JPEG, GIF, WEBP.

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: `make_test_png()` — generates PNG programmatically — not runnable |
| `image_base64.py` | Send image via base64 |
| `multiple_images.py` | Multiple images in one message |
| `image_from_disk.py` | Load image from disk |
| `vision_prompting_ambiguity.py` | Resolve visual ambiguity in the prompt |
| `pdf_pattern.py` | PDF pattern (needs a real PDF to actually run) |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _11_multimodal && ../venv/bin/python image_base64.py
```
