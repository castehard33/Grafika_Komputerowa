import pygame
import math

# --- Stałe ---
SCREEN_WIDTH = 600
SCREEN_HEIGHT = 600
WINDOW_TITLE = "Zadanie 1: Transformacje Wielokata (wg Fig. 1 v16)"

# Kolory
ZOLTY = (255, 255, 0)
NIEBIESKI = (0, 0, 255)
CZARNY = (0, 0, 0)

# Ustawienia wielokąta
POLY_SIDES = 10
POLY_RADIUS = 150
POLY_CENTER_X = SCREEN_WIDTH // 2
POLY_CENTER_Y = SCREEN_HEIGHT // 2

# --- Funkcja pomocnicza ---
def calculate_polygon_vertices(n_sides, radius, center):
    vertices = []
    cx, cy = center
    angle_step = 2 * math.pi / n_sides
    start_angle = -math.pi / 2

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

# --- Tworzenie oryginalnej powierzchni wielokąta (baza dla wszystkich transformacji) ---
base_surface_size = int(POLY_RADIUS * 2 * 2.0)
base_surface_center = (base_surface_size // 2, base_surface_size // 2)
original_vertices_rel_base = [
    (v[0] - POLY_CENTER_X + base_surface_center[0],
     v[1] - POLY_CENTER_Y + base_surface_center[1])
    for v in calculate_polygon_vertices(POLY_SIDES, POLY_RADIUS, (POLY_CENTER_X, POLY_CENTER_Y))
]
original_polygon_surface_base = pygame.Surface((base_surface_size, base_surface_size), pygame.SRCALPHA)
pygame.draw.polygon(original_polygon_surface_base, NIEBIESKI, original_vertices_rel_base, 0)
original_width, original_height = original_polygon_surface_base.get_size()

top_vertex_y_on_original_surface = base_surface_center[1] - POLY_RADIUS

# --- Zmienne stanu transformacji ---
active_transform_mode = 'rotozoom'
current_angle = 0.0
current_scale = 1.0
flip_x = False
flip_y = False
shear_factor = 0.0
scale_top_factors = (1.0, 1.0)
scale_center_factors = (1.0, 1.0)
scale_rotate_bottom_factors = (1.0, 1.0)
scale_rotate_bottom_angle = 0.0
rotation_angle_for_shear = 0.0

# --- Funkcja resetująca transformacje ---
def reset_transform():
    global active_transform_mode, current_angle, current_scale, flip_x, flip_y
    global shear_factor, scale_top_factors, scale_center_factors, rotation_angle_for_shear
    global scale_rotate_bottom_factors, scale_rotate_bottom_angle
    active_transform_mode = 'rotozoom'
    current_angle = 0.0
    current_scale = 1.0
    flip_x = False
    flip_y = False
    shear_factor = 0.0
    scale_top_factors = (1.0, 1.0)
    scale_center_factors = (1.0, 1.0)
    scale_rotate_bottom_factors = (1.0, 1.0)
    scale_rotate_bottom_angle = 0.0
    rotation_angle_for_shear = 0.0
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

            if event.key in [pygame.K_1, pygame.K_2, pygame.K_3, pygame.K_4,
                             pygame.K_5, pygame.K_6, pygame.K_7, pygame.K_8,
                             pygame.K_9, pygame.K_0]:
                 reset_transform()

            # --- Mapowanie klawiszy 1-9 do transformacji ---
            if event.key == pygame.K_1:
                active_transform_mode = 'rotozoom'; current_scale = 0.5
                print("Transformacja 1: Skalowanie w dół (0.5x)")
            elif event.key == pygame.K_2:
                active_transform_mode = 'rotozoom'; current_scale = 1.1; current_angle = 45
                print("Transformacja 2: Skala 1.1x, Obrót 45 stopni")
            elif event.key == pygame.K_3:
                active_transform_mode = 'scale_center'; scale_center_factors = (0.5, 1.0)
                print("Transformacja 3: Rozciągnięcie pionowe (H:0.5, V:1.0), wycentrowane")
            elif event.key == pygame.K_4:
                active_transform_mode = 'shear_only'; shear_factor = 0.3
                print("Transformacja 4: Pochylenie (Shear)")
            elif event.key == pygame.K_5:
                active_transform_mode = 'scale_top'; scale_top_factors = (1.5, 0.5)
                print("Transformacja 5: Rozciągnięcie poziome (H:1.5, V:0.5), góra okna")
            elif event.key == pygame.K_6:
                active_transform_mode = 'shear_rotated'; shear_factor = 0.3; rotation_angle_for_shear = -90
                print("Transformacja 6: Pochylenie obrócone o -90 stopni")
            elif event.key == pygame.K_7:
                active_transform_mode = 'scale_center'; scale_center_factors = (0.5, 1.0); flip_y = True
                print("Transformacja 7: Odbicie pionowe transformacji 3")
            elif event.key == pygame.K_8:
                active_transform_mode = 'scale_rotate_bottom'
                scale_rotate_bottom_factors = (1.5, 0.5); scale_rotate_bottom_angle = -30
                print("Transformacja 8: T5 obr.-30 st., offset 200 od dołu, przesunięta w lewo (center method)")
            elif event.key == pygame.K_9:
                active_transform_mode = 'rotozoom'; current_angle = 210; current_scale = 1.0
                print("Transformacja 9: Obrót 210 st., odsunięte od lewej, wycentrowane pionowo")


    # --- Rysowanie ---
    win.fill(ZOLTY)

    # --- Wybór metody rysowania na podstawie trybu ---
    if active_transform_mode in ['shear_only', 'shear_rotated']:
        original_vertices_rel_shear = [
            (v[0] - POLY_CENTER_X + base_surface_center[0], v[1] - POLY_CENTER_Y + base_surface_center[1])
            for v in calculate_polygon_vertices(POLY_SIDES, POLY_RADIUS, (POLY_CENTER_X, POLY_CENTER_Y))
        ]
        sheared_vertices_rel = []; cx, cy = base_surface_center
        for vx, vy in original_vertices_rel_shear:
            rel_x = vx - cx; rel_y = vy - cy; sheared_rel_x = rel_x + shear_factor * rel_y
            screen_x = sheared_rel_x + cx; screen_y = rel_y + cy; sheared_vertices_rel.append((int(screen_x), int(screen_y)))
        temp_shear_surface = pygame.Surface((base_surface_size, base_surface_size), pygame.SRCALPHA)
        pygame.draw.polygon(temp_shear_surface, NIEBIESKI, sheared_vertices_rel, 0)
        if active_transform_mode == 'shear_rotated': final_surface_to_blit = pygame.transform.rotate(temp_shear_surface, rotation_angle_for_shear)
        else: final_surface_to_blit = temp_shear_surface
        transformed_rect = final_surface_to_blit.get_rect(center=(POLY_CENTER_X, POLY_CENTER_Y))
        win.blit(final_surface_to_blit, transformed_rect)

    elif active_transform_mode == 'scale_top':
        h_scale, v_scale = scale_top_factors
        new_width = int(original_width * h_scale); new_height = int(original_height * v_scale)
        if new_width > 0 and new_height > 0:
            scaled_surface = pygame.transform.scale(original_polygon_surface_base, (new_width, new_height))
            scaled_top_vertex_y = top_vertex_y_on_original_surface * v_scale
            pos_y = int(-scaled_top_vertex_y); pos_x = (SCREEN_WIDTH - new_width) // 2
            win.blit(scaled_surface, (pos_x, pos_y))
        else: print("Ostrzeżenie: Próba skalowania do zerowych lub ujemnych wymiarów.")

    elif active_transform_mode == 'scale_center':
        h_scale, v_scale = scale_center_factors
        new_width = int(original_width * h_scale); new_height = int(original_height * v_scale)
        if new_width > 0 and new_height > 0:
            scaled_surface = pygame.transform.scale(original_polygon_surface_base, (new_width, new_height))
            if flip_x or flip_y: final_surface = pygame.transform.flip(scaled_surface, flip_x, flip_y)
            else: final_surface = scaled_surface
            transformed_rect = final_surface.get_rect(center=(POLY_CENTER_X, POLY_CENTER_Y))
            win.blit(final_surface, transformed_rect)
        else: print("Ostrzeżenie: Próba skalowania do zerowych lub ujemnych wymiarów.")

    elif active_transform_mode == 'scale_rotate_bottom':
        h_scale, v_scale = scale_rotate_bottom_factors; angle = scale_rotate_bottom_angle
        new_width = int(original_width * h_scale); new_height = int(original_height * v_scale)
        if new_width > 0 and new_height > 0:
            scaled_surface = pygame.transform.scale(original_polygon_surface_base, (new_width, new_height))
            rotated_surface = pygame.transform.rotate(scaled_surface, angle)
            final_rect = rotated_surface.get_rect(); horizontal_offset = 50
            center_offset_from_bottom = 200
            target_center_x = SCREEN_WIDTH // 2 - horizontal_offset
            target_center_y = SCREEN_HEIGHT - center_offset_from_bottom
            final_rect.center = (target_center_x, target_center_y)
            win.blit(rotated_surface, final_rect)
        else: print("Ostrzeżenie: Próba skalowania do zerowych lub ujemnych wymiarów dla T8.")

    else:
        transformed_surface = original_polygon_surface_base.copy()
        if current_scale > 0:
             final_transformed_surface = pygame.transform.rotozoom(transformed_surface, current_angle, current_scale)
             transformed_rect = final_transformed_surface.get_rect()

             is_transform_9 = (active_transform_mode == 'rotozoom' and current_angle == 210 and current_scale == 1.0 and not flip_x and not flip_y)

             if is_transform_9:
                 t9_horizontal_offset = 44
                 transformed_rect.left = t9_horizontal_offset
                 transformed_rect.centery = SCREEN_HEIGHT // 2
             else:
                 transformed_rect.center = (POLY_CENTER_X, POLY_CENTER_Y)

             win.blit(final_transformed_surface, transformed_rect)
        else:
             print("Ostrzeżenie: Próba skalowania rotozoom do zera lub ujemnej wartości.")


    # --- Aktualizacja wyświetlania ---
    pygame.display.flip()

    # --- Kontrola klatek ---
    clock.tick(60)

# --- Zakończenie Pygame ---
pygame.quit()
print("Pygame zamknięte.")