#!/usr/bin/env python3
"""Generates the KeepOn launcher icons (legacy mipmaps + adaptive foreground).

Pure stdlib: supersampled SDF rasterisation of the power glyph, written as
RGBA PNGs by hand. One design: the same amber power symbol the in-app button
draws. Run from keepon/: python3 tools/genicon.py
"""
import math
import os
import struct
import zlib

AMBER = (255, 177, 61)
BG = (20, 20, 24)
SIZES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
SS = 3  # supersampling factor per axis


def write_png(path, w, h, get):
    rows = bytearray()
    for y in range(h):
        rows.append(0)  # filter type 0 per scanline
        for x in range(w):
            rows += bytes(get(x, y))

    def chunk(tag, data):
        return (struct.pack(">I", len(data)) + tag + data
                + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF))

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(bytes(rows), 9))
    png += chunk(b"IEND", b"")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(png)
    print(f"  {path}  {w}x{h}")


def glyph(c, size, x, y):
    """True if (x, y) is inside the power symbol inside a glyph box of edge
    `size` centred at c."""
    cx, cy = c
    box = size * 0.62          # glyph drawing box within the canvas
    r_arc = box * 0.27
    stroke = box * 0.075
    gap = math.radians(34)     # the ring's gap is centred on the top

    dx, dy = x - cx, y - cy
    d = math.hypot(dx, dy)
    top = -math.pi / 2

    # ring, minus the top gap
    if abs(d - r_arc) <= stroke / 2:
        ang = math.atan2(dy, dx)
        off = abs((ang - top + math.pi) % (2 * math.pi) - math.pi)
        if off >= gap:
            return True

    # round caps at the two ends of the arc
    for a in (top - gap, top + gap):
        px, py = cx + r_arc * math.cos(a), cy + r_arc * math.sin(a)
        if math.hypot(x - px, y - py) <= stroke / 2:
            return True

    # the vertical bar, a capsule from just above the ring to the centre
    bw = stroke * 0.47
    y_top = cy - r_arc - stroke * 0.42
    y_bot = cy
    t = max(0.0, min(1.0, (y - y_top) / (y_bot - y_top)))
    dist = math.hypot(x - cx, y - (y_top + t * (y_bot - y_top)))
    if dist <= bw:
        return True
    return False


def legacy(path, size):
    def px(x, y):
        r = size * 0.22  # rounded-corner radius of the plate
        cover = 0
        for sy in range(SS):
            for sx in range(SS):
                fx, fy = x + (sx + 0.5) / SS, y + (sy + 0.5) / SS
                rr = size / 2
                dx, dy = abs(fx - rr), abs(fy - rr)
                # rounded rect: inside the straight edges, or inside the corner circles
                if math.hypot(max(dx - (rr - r), 0), max(dy - (rr - r), 0)) <= r:
                    cover += 1
        a = round(255 * cover / (SS * SS))
        g = 0
        for sy in range(SS):
            for sx in range(SS):
                fx, fy = x + (sx + 0.5) / SS, y + (sy + 0.5) / SS
                if glyph((size / 2, size / 2), size * 1.5, fx, fy):
                    g += 1
        if g:
            return (*AMBER, round(255 * g / (SS * SS)))
        return (*BG, a)

    write_png(path, size, size, px)


def foreground(path, size):
    def px(x, y):
        g = 0
        for sy in range(SS):
            for sx in range(SS):
                fx, fy = x + (sx + 0.5) / SS, y + (sy + 0.5) / SS
                if glyph((size / 2, size / 2), size * 1.25, fx, fy):
                    g += 1
        return (*AMBER, round(255 * g / (SS * SS))) if g else (0, 0, 0, 0)

    write_png(path, size, size, px)


if __name__ == "__main__":
    os.chdir(os.path.join(os.path.dirname(__file__), ".."))
    print("KeepOn icons:")
    for dpi, base in SIZES.items():
        legacy(f"res/mipmap-{dpi}/ic_launcher.png", base)
        fg = round(base * 108 / 48)  # adaptive foreground is 108dp
        foreground(f"res/mipmap-{dpi}/ic_launcher_foreground.png", fg)
