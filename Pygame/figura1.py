import pygame
import math

# --- Stałe ---
SCREEN_WIDTH = 600
SCREEN_HEIGHT = 600
WINDOW_TITLE = "Zadanie 1: Transformacje Wielokata (wg Fig. 1)"

# Kolory
ZOLTY = (255, 255, 0)  # Tło jak na Fig. 1
NIEBIESKI = (0, 0, 255) # Kolor wielokąta
CZARNY = (0, 0, 0)

# Ustawienia wielokąta
POLY_SIDES = 10  # Dziesięciokąt
POLY_RADIUS = 150
POLY_CENTER_X = SCREEN_WIDTH // 2
POLY_CENTER_Y = SCREEN_HEIGHT // 2

# --- Funkcja pomocnicza ---
def calculate_polygon_vertices(n_sides, radius, center):
    """Oblicza wierzchołki regularnego wielokąta."""
    vertices = []
    cx, cy = center
    angle_step = 2 * math.pi / n_sides
    start_angle = -math.pi / 2 # Startuj z wierzchołkiem na górze

    for i in range(n_sides):
        angle = start_angle + i * angle_step
        x = cx + radius * math.cos(angle)
        y = cy + radius * math.sin(angle)
        vertices.append((int(x), int(y)))
    return vertices

# --- Inicjalizacja Pygame ---
pygame.init()
win = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
pygame.display.set_caption(WINDOW_TITLE)
clock = pygame.time.Clock()

# --- Tworzenie oryginalnej powierzchni wielokąta ---
# Powierzchnia musi być nieco większa niż sam wielokąt, aby zmieścić obroty
surface_size = int(POLY_RADIUS * 2 * 1.5) # Dodatkowy margines na obroty/skalowanie
original_polygon_center_on_surface = (surface_size // 2, surface_size // 2)

# Oblicz wierzchołki względem środka tej nowej powierzchni
original_vertices_rel = [
    (v[0] - POLY_CENTER_X + original_polygon_center_on_surface[0],
     v[1] - POLY_CENTER_Y + original_polygon_center_on_surface[1])
    for v in calculate_polygon_vertices(POLY_SIDES, POLY_RADIUS, (POLY_CENTER_X, POLY_CENTER_Y))
]

# Stwórz przezroczystą powierzchnię dla wielokąta
original_polygon_surface = pygame.Surface((surface_size, surface_size), pygame.SRCALPHA)
# Narysuj na niej wielokąt
pygame.draw.polygon(original_polygon_surface, NIEBIESKI, original_vertices_rel, 0) # 0 oznacza wypełnienie

# --- Zmienne stanu transformacji ---
current_angle = 0.0
current_scale = 1.0
flip_x = False
flip_y = False

# --- Funkcja resetująca transformacje ---
def reset_transform():
    global current_angle, current_scale, flip_x, flip_y
    current_angle = 0.0
    current_scale = 1.0
    flip_x = False
    flip_y = False
    print("Reset Transform")

# --- Główna pętla gry ---
run = True
while run:
    # --- Obsługa zdarzeń ---
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            run = False
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_ESCAPE:
                 run = False

            # Reset przed ustawieniem nowej transformacji (dla klawiszy 1-9)
            if event.key in [pygame.K_1, pygame.K_2, pygame.K_3, pygame.K_4,
                             pygame.K_5, pygame.K_6, pygame.K_7, pygame.K_8,
                             pygame.K_9]:
                 reset_transform() # Zaczynamy od stanu neutralnego dla każdego klawisza

            # --- Mapowanie klawiszy 1-9 do transformacji z Fig. 1 ---
            if event.key == pygame.K_1: # Skalowanie w dół
                current_scale = 0.5
                print("Transformacja 1: Skalowanie w dół (0.5x)")
            elif event.key == pygame.K_2: # Skalowanie w górę i obrót w lewo (CCW)
                current_scale = 1.1
                current_angle = 45
                print("Transformacja 2: Skala 1.1x, Obrót 45 stopni")
            elif event.key == pygame.K_3: # Przybliżenie spłaszczenia pionowego -> zrobimy małe skalowanie
                current_scale = 0.6 # Zamiast niesymetrycznego skalowania
                print("Transformacja 3: Skalowanie w dół (0.6x) - przybliżenie")
            elif event.key == pygame.K_4: # Obrót w lewo (CCW)
                current_angle = 30
                print("Transformacja 4: Obrót 30 stopni")
            elif event.key == pygame.K_5: # Przybliżenie spłaszczenia poziomego -> zrobimy odbicie poziome
                flip_x = True
                print("Transformacja 5: Odbicie poziome - przybliżenie")
            elif event.key == pygame.K_6: # Duży obrót w prawo (CW)
                current_angle = -70 # Kąty ujemne to obrót w prawo
                print("Transformacja 6: Obrót -70 stopni")
            elif event.key == pygame.K_7: # Odbicie pionowe
                flip_y = True
                print("Transformacja 7: Odbicie pionowe")
            elif event.key == pygame.K_8: # Skalowanie w dół i obrót w prawo (CW)
                current_scale = 0.7
                current_angle = -30
                print("Transformacja 8: Skala 0.7x, Obrót -30 stopni")
            elif event.key == pygame.K_9: # Odbicie poziome i lekki obrót w prawo (CW)
                flip_x = True
                current_angle = -20
                print("Transformacja 9: Odbicie poziome, Obrót -20 stopni")

            # Klawisz 0 - ręczny reset
            elif event.key == pygame.K_0:
                reset_transform()

    # --- Transformacje ---
    # 1. Stwórz kopię roboczą (ważne, by nie modyfikować oryginału wielokrotnie)
    transformed_surface = original_polygon_surface.copy()

    # 2. Zastosuj odbicie (jeśli aktywne)
    if flip_x or flip_y:
        transformed_surface = pygame.transform.flip(transformed_surface, flip_x, flip_y)

    # 3. Zastosuj obrót i skalowanie (rotozoom robi obie rzeczy)
    # Uwaga: rotozoom zawsze odnosi się do *oryginalnych* wymiarów powierzchni przed flipem
    # Musimy przekazać powierzchnię *po* flipie, jeśli był.
    final_transformed_surface = pygame.transform.rotozoom(transformed_surface, current_angle, current_scale)

    # Pobierz prostokąt przekształconej powierzchni, aby ją wycentrować
    transformed_rect = final_transformed_surface.get_rect()
    transformed_rect.center = (POLY_CENTER_X, POLY_CENTER_Y) # Wycentruj na ekranie

    # --- Rysowanie ---
    # Wypełnij tło (Żółty jak na Fig. 1)
    win.fill(ZOLTY)

    # Narysuj przekształconą powierzchnię wielokąta na głównym oknie
    win.blit(final_transformed_surface, transformed_rect)

    # --- Aktualizacja wyświetlania ---
    pygame.display.flip() # Użyj flip dla pełnego odświeżenia

    # --- Kontrola klatek ---
    clock.tick(60) # Ogranicz do 60 FPS

# --- Zakończenie Pygame ---
pygame.quit()
print("Pygame zamknięte.")