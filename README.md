# SYNTAX-ERROR 2D

Un petit jeu 2D Java (prototype vagues / survie) développé dans le cadre d'un projet scolaire.

Ce dépôt contient le code source, les assets (sprites, sons) et des tests unitaires pour les composants core.

---

## Contenu

- `src/` : code source Java (jeu, entités, UI, managers)
- `assets/` : images et sons utilisés par le jeu
- `test/` : tests unitaires JUnit
- `lib/` : dépendances (JUnit standalone, éventuellement JDK local)
- `readme` : instructions rapides (Windows / mac)
- `SOUND_IMPLEMENTATION.md` : détails sur l'implémentation audio

---

## Prérequis

- JDK 17+ (OpenJDK / Temurin / Oracle)
- (Optionnel) `lib/junit-platform-console-standalone-1.10.2.jar` pour exécuter les tests localement

Sur macOS, la commande `afplay` est utilisée pour lire les MP3 si Java AudioSystem ne supporte pas le format.

---

## Compilation et exécution (rapide)

Ouvrir un terminal à la racine du projet et exécuter :

```bash
# Compiler les sources
rm -rf out
javac -d out $(find src -name "*.java")

# Lancer le jeu
java -cp out src.core.Main
```

Remarque : selon votre IDE (VSCode/IntelliJ/Eclipse) le classpath et le répertoire de travail peuvent différer.

---

## Tests unitaires

Le projet inclut plusieurs tests JUnit dans le dossier `test/`. Pour les exécuter manuellement :

```bash
# Compiler les sources et les tests
rm -rf out
cp="lib/junit-platform-console-standalone-1.10.2.jar"
javac -d out -cp "$cp:src" $(find src -name "*.java") $(find test -name "*.java")

# Lancer les tests
java -jar "$cp" -cp "out:src" --scan-class-path
```

Les tests présents vérifient des comportements clés (inventory, vagues, projectiles, time freeze, etc.).

---

## Contrôles

- Flèches gauche/droite : déplacement
- Espace : saut / attaque (selon arme)
- T : utiliser un Timer (gel)
- P / ESC : pause
- ENTER : démarrer depuis le menu

---

## Structure clé du code

- `src/core/Game.java` : boucle principale, états écran (menu / running / pause / game over)
- `src/core/GamePanel.java` : panneau Swing qui délègue le rendu à `Game`
- `src/entities/` : entités du jeu (Player, Enemy*, Bullet, Pickup, ...)
- `src/world/Level.java` et `Platform.java` : définition des plateformes et décor
- `src/core/SoundManager.java` : gestion centralisée des sons (WAV natif, MP3 via lecteur système)
- `src/ui/HUD.java` : affichage du HUD (kills, timers, AK47, etc.)

---

## Assets

Les images et sons sont dans `assets/`. Les noms importants :

- `assets/sound/` : `bonus.wav`, `dead.wav`, `game-music-loop.mp3`, `game-over.mp3`, `jump.mp3`, `punch.mp3`, `punch-ennemi.mp3`, `shot.mp3`
- `assets/design/` et `assets/items/` : sprites et icônes pour HUD / menu

---

## Contribuer / améliorer

- Ajouter un workflow CI (ex : GitHub Actions) pour compiler et exécuter les tests automatiquement
- Ajouter un `README.md` amélioré (ce fichier), GDD et slides de présentation
- Polir l'expérience : volumes audio, lissage animations, HUD responsive

---

## Crédits

Auteur: (ton nom)

Licence: voir la licence choisie (aucune fournie par défaut)
