# -*- coding: utf-8 -*-
"""
生成小程序图标（纯 Python 标准库，无需 Pillow）
用法：python tools/gen_icons.py

产出：
1. tabBar 图标 81x81（灰色 / 主题绿 active）
2. 首页入口大卡图标 96x96（白色线性：租房 / 招聘）
3. 便民服务宫格图标 96x96（主题绿线性：二手物品 / 失物招领 / 拼车互助 / 本地咨询）
4. 搜索放大镜图标 48x48（深灰）
5. 列表/详情占位图 400x300
"""
import os
import struct
import zlib

SIZE = 81          # tabBar 图标尺寸
SERVICE_SIZE = 96  # 首页入口 / 服务宫格图标尺寸

GRAY = (154, 160, 166, 255)          # tabBar 未选中
THEME = (43, 125, 233, 255)          # 主题色（选中态 / 服务图标）#2b7de9
WHITE = (255, 255, 255, 255)         # 入口大卡白色图标
INK = (70, 74, 78, 255)              # 搜索图标深灰


def new_canvas(size=SIZE):
    return [[(0, 0, 0, 0) for _ in range(size)] for _ in range(size)]


def put(px, x, y, color, alpha=1.0):
    h = len(px)
    w = len(px[0])
    if x < 0 or y < 0 or x >= w or y >= h:
        return
    if alpha >= 1.0:
        px[y][x] = color
        return
    r0, g0, b0, a0 = px[y][x]
    r1, g1, b1, a1 = color
    a = a1 * alpha
    out_a = a + a0 * (1 - alpha)
    if out_a == 0:
        px[y][x] = (0, 0, 0, 0)
        return
    r = int((r1 * a + r0 * a0 * (1 - alpha)) / out_a)
    g = int((g1 * a + g0 * a0 * (1 - alpha)) / out_a)
    b = int((b1 * a + b0 * a0 * (1 - alpha)) / out_a)
    px[y][x] = (r, g, b, int(out_a))


def rect(px, x0, y0, x1, y1, color):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            put(px, x, y, color)


def rect_border(px, x0, y0, x1, y1, color, t=3):
    rect(px, x0, y0, x1, y0 + t - 1, color)
    rect(px, x0, y1 - t + 1, x1, y1, color)
    rect(px, x0, y0, x0 + t - 1, y1, color)
    rect(px, x1 - t + 1, y0, x1, y1, color)


def disc(px, cx, cy, r, color):
    for y in range(cy - r, cy + r + 1):
        for x in range(cx - r, cx + r + 1):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            if d <= r:
                put(px, x, y, color)
            elif d <= r + 1:
                put(px, x, y, color, alpha=(r + 1 - d))


def disc_ring(px, cx, cy, r, t, color):
    for y in range(cy - r - 2, cy + r + 3):
        for x in range(cx - r - 2, cx + r + 3):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            if r - t / 2 <= d <= r + t / 2:
                put(px, x, y, color)


def line(px, x0, y0, x1, y1, color, t=3):
    """两点连线（步进 + 方形笔刷），用于线性图标"""
    steps = max(abs(x1 - x0), abs(y1 - y0), 1)
    half = t // 2
    for i in range(steps + 1):
        x = x0 + (x1 - x0) * i / steps
        y = y0 + (y1 - y0) * i / steps
        for dx in range(-half, half + 1):
            for dy in range(-half, half + 1):
                put(px, int(round(x)) + dx, int(round(y)) + dy, color)


def triangle(px, cx, top, half, height, color):
    """实心等腰三角形：顶点 (cx, top)，底边在 top+height"""
    for i in range(height):
        y = top + i
        w = int(half * (i + 1) / height)
        for x in range(cx - w, cx + w + 1):
            put(px, x, y, color)


# ---------------- tabBar 图标（81x81） ----------------

def draw_home(px, color):
    x0, y0, x1, y1 = 18, 18, 63, 63
    gap = 6
    w = (x1 - x0 - gap) // 2
    rect(px, x0, y0, x0 + w, y0 + w, color)
    rect(px, x1 - w, y0, x1, y0 + w, color)
    rect(px, x0, y1 - w, x0 + w, y1, color)
    rect(px, x1 - w, y1 - w, x1, y1, color)


def draw_house(px, color):
    triangle(px, 40, 16, 25, 22, color)
    rect(px, 22, 36, 58, 64, color)
    rect(px, 34, 48, 46, 64, (0, 0, 0, 0))


def draw_job(px, color):
    rect(px, 16, 32, 65, 64, color)
    rect_border(px, 31, 18, 50, 34, color, t=4)
    rect(px, 35, 30, 46, 34, (0, 0, 0, 0))
    rect(px, 37, 44, 44, 52, (0, 0, 0, 0))


def draw_mine(px, color):
    disc(px, 40, 30, 13, color)
    for y in range(48, 70):
        for x in range(40 - 26, 40 + 27):
            d = ((x - 40) ** 2 + ((y - 48) * 1.15) ** 2) ** 0.5
            if d <= 26:
                put(px, x, y, color)


# ---------------- 首页入口大卡图标（96x96 白色） ----------------

def draw_house_big(px, color):
    """租房：实心房子 + 门（白色，与大卡背景区分靠挖空）"""
    triangle(px, 48, 12, 32, 26, color)
    rect(px, 24, 34, 72, 80, color)
    rect(px, 40, 52, 56, 80, (0, 0, 0, 0))  # 门挖空


def draw_job_big(px, color):
    """招聘：文档 + 对勾（白色线性）"""
    rect_border(px, 26, 14, 68, 78, color, t=5)
    line(px, 37, 32, 57, 32, color, t=5)
    line(px, 37, 45, 57, 45, color, t=5)
    # 对勾压在右下角（超出文档框，贴合设计图）
    line(px, 46, 62, 56, 72, color, t=6)
    line(px, 56, 72, 74, 50, color, t=6)


# ---------------- 便民服务宫格图标（96x96 绿色线性） ----------------

def draw_doc(px, color):
    """二手物品：文档"""
    rect_border(px, 28, 16, 68, 80, color, t=4)
    line(px, 38, 32, 58, 32, color, t=4)
    line(px, 38, 44, 58, 44, color, t=4)
    line(px, 38, 56, 50, 56, color, t=4)


def draw_money(px, color):
    """失物招领：钱币（圆环 + ¥）"""
    disc_ring(px, 48, 48, 30, 4, color)
    line(px, 38, 30, 48, 43, color, t=4)
    line(px, 58, 30, 48, 43, color, t=4)
    line(px, 48, 34, 48, 64, color, t=4)
    line(px, 41, 50, 55, 50, color, t=4)
    line(px, 41, 58, 55, 58, color, t=4)


def draw_bus(px, color):
    """拼车互助：巴士（车身 + 车头分隔 + 双轮）"""
    rect_border(px, 14, 34, 82, 64, color, t=4)
    line(px, 36, 34, 36, 64, color, t=4)
    line(px, 44, 45, 74, 45, color, t=4)
    disc(px, 30, 68, 6, color)
    disc(px, 66, 68, 6, color)


def draw_check(px, color):
    """本地咨询：圆环 + 对勾"""
    disc_ring(px, 48, 48, 30, 4, color)
    line(px, 34, 50, 44, 60, color, t=5)
    line(px, 44, 60, 63, 37, color, t=5)


def draw_search(px, color):
    """放大镜 48x48"""
    disc_ring(px, 19, 19, 12, 3, color)
    line(px, 28, 28, 40, 40, color, t=4)


# ---------------- 输出 ----------------

def write_png(path, px, w=None, h=None):
    h = h or len(px)
    w = w or len(px[0])
    raw = bytearray()
    for row in px:
        raw.append(0)
        for r, g, b, a in row:
            raw += bytes((r, g, b, a))

    def chunk(tag, data):
        c = struct.pack('>I', len(data)) + tag + data
        c += struct.pack('>I', zlib.crc32(tag + data) & 0xFFFFFFFF)
        return c

    header = struct.pack('>IIBBBBB', w, h, 8, 6, 0, 0, 0)
    png_bytes = b'\x89PNG\r\n\x1a\n'
    png_bytes += chunk(b'IHDR', header)
    png_bytes += chunk(b'IDAT', zlib.compress(bytes(raw), 9))
    png_bytes += chunk(b'IEND', b'')
    with open(path, 'wb') as f:
        f.write(png_bytes)


def main():
    root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    icons = os.path.join(root, 'miniprogram', 'assets', 'icons')
    images = os.path.join(root, 'miniprogram', 'assets', 'images')
    os.makedirs(icons, exist_ok=True)
    os.makedirs(images, exist_ok=True)

    # 1. tabBar 图标：灰色 + 主题绿
    tab = {'home': draw_home, 'house': draw_house, 'job': draw_job, 'mine': draw_mine}
    for name, drawer in tab.items():
        px = new_canvas()
        drawer(px, GRAY)
        write_png(os.path.join(icons, name + '.png'), px)
        px = new_canvas()
        drawer(px, THEME)
        write_png(os.path.join(icons, name + '-active.png'), px)

    # 2. 首页入口大卡：白色图标
    px = new_canvas(SERVICE_SIZE)
    draw_house_big(px, WHITE)
    write_png(os.path.join(icons, 'entry-house.png'), px)
    px = new_canvas(SERVICE_SIZE)
    draw_job_big(px, WHITE)
    write_png(os.path.join(icons, 'entry-job.png'), px)

    # 3. 便民服务宫格：绿色线性图标
    services = {
        'service-second': draw_doc,
        'service-lost': draw_money,
        'service-carpool': draw_bus,
        'service-consult': draw_check
    }
    for name, drawer in services.items():
        px = new_canvas(SERVICE_SIZE)
        drawer(px, THEME)
        write_png(os.path.join(icons, name + '.png'), px)

    # 4. 搜索放大镜（深灰）
    px = new_canvas(48)
    draw_search(px, INK)
    write_png(os.path.join(icons, 'search.png'), px)

    # 5. 占位图（400x300 浅灰 + 山形）
    w, h = 400, 300
    pph = [[(236, 239, 243, 255) for _ in range(w)] for _ in range(h)]
    line_c = (205, 211, 219, 255)
    for i in range(70):
        for x in range(150 - i, 150 + i + 1):
            if 0 <= 200 - i < h and 0 <= x < w:
                pph[200 - i][x] = line_c
    rect(pph, 0, 200, 399, 203, line_c)
    disc(pph, 300, 90, 32, (219, 226, 234, 255))
    write_png(os.path.join(images, 'placeholder.png'), pph, w, h)

    print('图标已生成到：' + icons)
    print('占位图已生成到：' + images)


if __name__ == '__main__':
    main()
