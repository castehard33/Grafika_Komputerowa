import pygame

pygame.init()

SCREEN_WIDTH = 500
SCREEN_HEIGHT = 500
win = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
pygame.display.set_caption("Zielona Flaga z Dostosowanym Wycięciem")

ZIELONY_JASNY = (0, 255, 0)
BIALY = (255, 255, 255)

flag_x = 100
flag_y = 100

flag_width = 300
flag_height = 250

cutout_depth = 120

P1 = (flag_x, flag_y)
P2 = (flag_x + flag_width, flag_y)
P3 = (flag_x + flag_width, flag_y + flag_height)
P4 = (flag_x + flag_width / 2, flag_y + flag_height - cutout_depth)
P5 = (flag_x, flag_y + flag_height)

flag_vertices = [P1, P2, P3, P4, P5]

run = True
clock = pygame.time.Clock()

while run:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            run = False
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_ESCAPE:
                run = False

    win.fill(BIALY)

    pygame.draw.polygon(win, ZIELONY_JASNY, flag_vertices, 0)

    pygame.display.flip()

    clock.tick(60)

pygame.quit()
print("Pygame zamknięte.")