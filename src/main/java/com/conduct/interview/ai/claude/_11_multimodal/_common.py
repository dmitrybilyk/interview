"""
Shared test-image generator for _10_multimodal.
Not runnable on its own — imported by the demo scripts in this directory.
"""

import struct, zlib

def make_test_png(width: int = 60, height: int = 40) -> bytes:
    """Creates a small PNG with a gradient — pure stdlib, no Pillow needed."""
    def chunk(tag: bytes, data: bytes) -> bytes:
        c = tag + data
        return struct.pack(">I", len(data)) + c + struct.pack(">I", zlib.crc32(c) & 0xFFFFFFFF)

    ihdr = struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0)
    raw = b""
    for y in range(height):
        raw += b"\x00"   # filter type: None
        for x in range(width):
            # red gradient left→right, green gradient top→bottom, blue=120
            raw += bytes([int(x / width * 255), int(y / height * 255), 120])

    return (
        b"\x89PNG\r\n\x1a\n"
        + chunk(b"IHDR", ihdr)
        + chunk(b"IDAT", zlib.compress(raw))
        + chunk(b"IEND", b"")
    )
