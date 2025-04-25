import pygame

# --- Inicjalizacja Pygame ---
pygame.init()

# --- Ustawienia okna ---
SCREEN_WIDTH = 500
SCREEN_HEIGHT = 500
win = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
pygame.display.set_caption("Zielona Flaga z Dostosowanym Wycięciem")

# --- Kolory ---
ZIELONY_JASNY = (0, 255, 0)
BIALY = (255, 255, 255)

# --- Definicja wierzchołków flagi ---

# Położenie górnego lewego rogu
flag_x = 100
flag_y = 100

# Wymiary flagi
flag_width = 300
flag_height = 250

# Głębokość wycięcia 'V' - ZMNIEJSZONA WARTOŚĆ, ABY OBNIŻYĆ WCIĘCIE
cutout_depth = 120 # Poprzednio było 150, oryginał 100

# Obliczanie współrzędnych wierzchołków
# P1 --- P2
# |       |
# |       |
# P5 \ P4 / P3  <-- P4 jest teraz niżej niż poprzednio

P1 = (flag_x, flag_y)
P2 = (flag_x + flag_width, flag_y)
P3 = (flag_x + flag_width, flag_y + flag_height)
# Współrzędna Y punktu P4 będzie teraz większa (niżej na ekranie) niż przy 150
P4 = (flag_x + flag_width / 2, flag_y + flag_height - cutout_depth)
P5 = (flag_x, flag_y + flag_height)

# Lista wierzchołków dla funkcji polygon
flag_vertices = [P1, P2, P3, P4, P5]

# --- Główna pętla gry ---
run = True
clock = pygame.time.Clock()

while run:
    # --- Obsługa zdarzeń ---
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            run = False
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_ESCAPE:
                run = False

    # --- Rysowanie ---
    # Wypełnij tło na biało
    win.fill(BIALY)

    # Narysuj wielokąt (flagę)
    pygame.draw.polygon(win, ZIELONY_JASNY, flag_vertices, 0)

    # --- Aktualizacja wyświetlania ---
    pygame.display.flip()

    # --- Kontrola klatek ---
    clock.tick(60)

# --- Zakończenie Pygame ---
pygame.quit()
print("Pygame zamknięte.")