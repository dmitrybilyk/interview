# _10 — Multimodal (Images & PDFs)

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

## What the script demonstrates
1. Generate a PNG programmatically (no external files needed)
2. Send image via base64
3. Multiple images in one message
4. Load image from disk and send
5. Resolve visual ambiguity in the text prompt
6. PDF pattern (code shown, commented out — needs a real PDF)

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _10_multimodal && python script.py
# Creates test_image.png in this directory
```
