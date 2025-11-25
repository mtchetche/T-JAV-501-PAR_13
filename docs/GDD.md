# Game Design Document (GDD) — SYNTAX-ERROR 2D

Version courte et pratique pour développement et démo.

## 1. Pitch

SYNTAX-ERROR 2D est un prototype de jeu d'action en 2D centré sur des vagues d'ennemis. Le joueur doit survivre, collecter des bonus (santé, bouclier, timers) et éventuellement récupérer une AK47 pour tirer à distance.

## 2. Public cible

Prototype académique / démo ; public : enseignants, étudiants et juges de projet.

## 3. Plateformes

- Desktop Java (Windows/macOS/Linux) via JVM

## 4. Gameplay & Mécaniques

- Mode de jeu : vagues successives d'ennemis. Chaque vague a une durée et un nombre d'ennemis variable.
- Le joueur peut se déplacer, sauter, attaquer au corps-à-corps ou avec l'AK47 s'il la récupère.
- Items : Timer (freeze), Health (soins), Shield (réduction dommages), AK47 (arme secondaire).
- HUD : affiche la vague, temps total, temps restant, compteur de kills et items (icônes + quantité). AK47 n’apparaît que si récupérée.

## 5. Contrôles

- Gauche / Droite : déplacement
- Espace : saut / mêlée
- T : activer Timer
- P / ESC : pause
- ENTER : depuis le menu, lancer la partie

## 6. Entités

- Player : position, santé, inventaire, arme (AK47 booléen), méthodes de déplacement et de rendu.
- Enemy (abstract) : état, IA de base, collisions, attaques. Sous-classes : `Enemy01` (melee), `Enemy02` (ranged), `Enemy03` (hybrid).
- Bullet / Ak47Bullet : projectiles avec collision et dommage.
- Pickup / ItemPickup : objets ramassables.

## 7. IA & comportements

- Ennemis ont détection du joueur, zone d'attaque et cooldown d'attaque.
- Les déplacements sont lissés et évitent la superposition (push léger entre ennemis).
- Les collisions sol/plafond/murs sont traitées par `Platform`.

## 8. UI / HUD

- HUD en haut : Vague + Temps total + Temps restant (à gauche), icônes d'items + compteurs (à droite).
- Menu principal : image plein écran (sans texte si demandé).

## 9. Audio

- WAV natifs joués via `AudioSystem`.
- MP3 lus via lecteur système (`afplay` sur macOS) quand nécessaire.
- Sons assignés : jump, pickup, punch (player), punch-ennemi (enemy hit), shot (projectile), dead + game-over, loop music.

## 10. Level design

- `Level` initialise les plateformes statiques (sol, colonnes, plateformes centrales). Les coordonnées se trouvent dans `src/world/Level.java`.

## 11. Assets

- Emplacement : `assets/`.
- Types : sprites (PNG), sons (WAV/MP3).

## 12. Tests

- Tests unitaires fournis (JUnit) pour inventory, freeze time, vagues, collisions, projectiles.

## 13. Future / Roadmap

- CI / tests automatisés (GitHub Actions) — à ajouter
- Couverture tests (JaCoCo) — à ajouter
- Ajout d'un menu d'options (volume, résolution)
- Amélioration design (animations, particules, transitions)

---

Fichier généré automatiquement pour le dépôt. Complète les sections avec captures, diagrammes et art quand disponibles.
