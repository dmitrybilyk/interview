# _11 — Multimodal (Images & PDFs)

**Cert notes section:** "Images, PDFs and high-volume processing"  
"PDF is a document, treated the same as image file"  
"Multimodal prompting: same as text formatting, just resolve visual ambiguity"

## What this covers
| Concept | Detail |
|---|---|
| Image via base64 | Encode raw bytes, set `media_type` |
| Image via URL | Point to a public URL (Claude fetches it) |
| Multiple images | Multiple content blocks in one message |
| PDF via base64 | Same pattern, `type: "document"`, `media_type: "application/pdf"` |
| Visual ambiguity | Tell Claude WHAT the image is — don't make it guess |

## Image content block structure
```python
{
    "type": "image",
    "source": {
        "type": "base64",            # or "url"
        "media_type": "image/png",   # image/jpeg, image/gif, image/webp
        "data": base64_string,       # base64.standard_b64encode(bytes).decode()
    }
}
```

## PDF content block structure
```python
{
    "type": "document",
    "source": {
        "type": "base64",
        "media_type": "application/pdf",
        "data": base64_string,
    }
}
```

## Visual ambiguity rule (cert)
Bad prompt: `"What is this?"` — Claude has to guess context  
Good prompt: `"This is a screenshot of our CI/CD pipeline dashboard. Which stage is failing?"` — resolves ambiguity upfront

## Limits
- Max 5MB per image
- Max 20 images per request  
- Supported formats: PNG, JPEG, GIF, WEBP
- Token cost scales with image dimensions

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: `make_test_png()` — generates a PNG programmatically, no external files needed — not runnable on its own |
| `image_base64.py` | Send image via base64 |
| `multiple_images.py` | Multiple images in one message |
| `image_from_disk.py` | Load image from disk and send (creates `test_image.png` in this directory) |
| `vision_prompting_ambiguity.py` | Resolve visual ambiguity in the text prompt |
| `pdf_pattern.py` | PDF pattern (code shown, printed — needs a real PDF to actually run) |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _11_multimodal && python image_base64.py   # or any other file in this directory
```
