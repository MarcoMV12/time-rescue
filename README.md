# Time Rescue

Juego de plataformas 2D hecho en Java, con sprites, animaciones y música acordes. El jugador viaja por distintas épocas (prehistoria, Egipto, época medieval y un nivel futurista) resolviendo retos de cada nivel para avanzar usando una máquina del tiempo.

## Capturas



## Tecnologías

- Java 21
- Java2D / Swing para gráficos
- `javax.sound.sampled` para audio
- Sin dependencias externas

## Niveles

1. **Nivel 0** — Mecánica de catapulta y resortera
2. **Nivel 1** — Egipto, con enemigos como momias, escorpiones y arqueros
3. **Nivel 2** — Época medieval, con mecánica de escudo
4. **Nivel 3** — Nivel futurista, con drones, torretas y trampas

## Controles

| Tecla | Acción |
|---|---|
| `W A S D` | Moverse |
| `Espacio` | Saltar |
| `Shift` | Escudo (nivel medieval) |
| `E` | Interactuar / recoger objetos |
| `Q` | Soltar objeto |
| `R` | Activar modo resortera (nivel 0) |
| `I` | Ver información de la catapulta (nivel 0) |
| `T` | Activar máquina del tiempo / avanzar de nivel |
| `Backspace` | Pausa |
| `Enter` / `Escape` | Confirmar / cerrar diálogos |

## Cómo ejecutar el proyecto

### Con VS Code (recomendado)

1. Instala la extensión **Extension Pack for Java**
2. Abre la carpeta del proyecto en VS Code
3. Abre `src/App.java` y presiona **Run** (▶) arriba de `main`

### Desde terminal

```bash
cd src
javac -d ../bin $(find . -name "*.java")
cd ../bin
java App
```

> Requiere **JDK 21** o superior instalado.

## Estructura del proyecto

```
src/
├── App.java          # Punto de entrada
├── Elementos/        # Jugadores, enemigos y elementos del juego
├── Eventos/          # Manejo de teclado y mouse
├── GameStates/        # Estados del juego (menú, jugando, pausa, etc.)
├── Juegos/           # Configuración principal de la ventana/juego
├── Niveles/          # Lógica de niveles
├── Objects/          # Objetos interactuables
├── ui/               # Overlays de interfaz (pausa, game over, etc.)
├── utilz/            # Utilidades (carga de assets, audio, puntajes)
└── res/              # Sprites, fondos, música y efectos de sonido
```

## Estado del proyecto

Proyecto escolar en desarrollo. Si quieres contribuir, los pull requests son bienvenidos.

## Créditos

Desarrollado  como proyecto escolar.
