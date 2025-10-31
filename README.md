# 🧟‍♂️ **ZombieSurvivor — Arena Survival en Java / LibGDX**

## 🎮 Descripción general

**ZombieSurvivor** es una versión expandida y reimaginada del proyecto académico **Space Navigation**, transformada en un juego **arena-survival top-down**, donde el jugador debe resistir oleadas de zombis cada vez más difíciles.

El proyecto está desarrollado completamente en **Java** utilizando el framework **LibGDX** (*core + lwjgl3 + parent*), como parte del curso **Programación Avanzada** de la **Pontificia Universidad Católica de Valparaíso**.  
Cuenta con cámara ortográfica, disparo orientado, IA de persecución, colisiones precisas y progresión dinámica por oleadas.

---

## 🎯 Objetivo del proyecto

- Adaptar la plantilla *Space Navigation* a una jugabilidad tipo *arena top-down*.  
- Implementar una **arquitectura modular** con sistemas de cámara, colisiones e IA ajustados al nuevo entorno.  
- Mantener compatibilidad con la documentación y estructura del proyecto original.  
- Garantizar un rendimiento **delta-friendly**, estable en cualquier tasa de FPS.  
- Entregar una experiencia fluida, desafiante y visualmente clara al jugador.

---

## ⚙️ Características principales

- 🔭 Vista **top-down 2D** con cámara ortográfica y escala estable.  
- 🧍‍♂️ Movimiento libre del jugador (WASD o flechas) y rotación continua hacia el cursor.  
- 🔫 **Disparo orientado**: las balas se originan desde el arma y viajan en la dirección de apuntado.  
- 🧠 **IA tipo “seek”**: los zombis persiguen al jugador con velocidad independiente del framerate.  
- 🌊 **Oleadas progresivas**: dificultad y ritmo de aparición controlados dinámicamente.  
- 💥 Colisiones circulares precisas e invulnerabilidad temporal (i-frames) tras recibir daño.  
- 🧾 HUD con información de puntaje, vidas y número de ola actual.  
- 🧟‍♀️ Mini jefes que aparecen en rondas avanzadas y requieren estrategias específicas para derrotarlos.

---

## 🕹️ Controles del juego

| Acción | Control |
|--------|----------|
| 🧍‍♂️ Movimiento | W, A, S, D o flechas direccionales |
| 🎯 Apuntar | Movimiento del mouse |
| 🔫 Disparar | Clic izquierdo o barra espaciadora |
| 🔁 Reiniciar / Pausa | Según pantalla o tecla asignada |
| 🎯 Objetivo | Sobrevive el mayor tiempo posible y derrota a los mini jefes por ronda |

---

## 💾 Instrucciones de instalación y ejecución

### 🧱 Requisitos previos

- ☕ **Java JDK 11 o superior** (Oracle u OpenJDK)  
- 💻 **Eclipse IDE for Java Developers 21+** o **IntelliJ IDEA**  
- 🎮 **Librerías LibGDX 1.12.1** (ya incluidas en Gradle)  
- ⚙️ *(Opcional)* **Gradle** para configuración automática de dependencias  

---

### 🚀 Pasos para importar y ejecutar el proyecto

1. Descargar el proyecto desde GitHub:  
   👉 [ZombieSurvivor-GM](https://github.com/marselomorales/ZombieSurvivor-GM)
   y hacer clic en **Code → Download ZIP**.

3. Descomprimir el archivo ZIP.  
   La carpeta principal debe llamarse **SpaceNav2024**.

4. Abrir **Eclipse**.  
   Ir a:  
   `File → Import → Gradle → Existing Gradle Project`

5. Seleccionar la carpeta **SpaceNav2024** y hacer clic en **Finish**.  
   Eclipse detectará automáticamente los submódulos `core` y `desktop`.

6. Esperar a que **Gradle descargue las dependencias** (puede tardar la primera vez).

7. Verificar que el árbol de paquetes incluya:
   core/src/...
desktop/src/...

8. En el panel **Package Explorer**, abrir:  
`desktop/src/puppy/code/DesktopLauncher.java`

9. Hacer clic derecho → **Run As → Java Application**  
(o presionar el botón verde ▶ en la barra superior).

💡 *El juego se abrirá en una ventana de 1200×800 px (ajustable).*  
¡Comienza a sobrevivir a las oleadas de zombis! 🧟🔥 
---

---

## 🧠 Solución de problemas comunes

- ⚙️ Si Eclipse muestra errores en Gradle →  
Clic derecho sobre el proyecto → `Gradle → Refresh Gradle Project`.

- ☕ Si Eclipse usa solo el **JRE** y no el **JDK** →  
`Window → Preferences → Java → Installed JREs → Add → Standard VM → Ruta del JDK`.

- 🖼️ Si el juego corre pero **no se ven las texturas** →  
Verifica que la carpeta `assets/` esté ubicada dentro de `core/assets/`.

- 🔊 Si hay errores de sonido →  
Asegúrate de que los archivos `.ogg` y `.wav` estén presentes en los recursos.

---

## 🚀 Próximas mejoras

- 🧩 Nuevos escenarios y ambientaciones.  
- 🧟‍♂️ Enemigos con distintos patrones de ataque.  
- 🏃‍♂️ Animaciones para el jugador y los zombis.  
- 💎 Implementación de power-ups y objetos temporales.  
- 🌍 Expansión de contenido y nuevos modos de juego.

---

## 👥 Integrantes del equipo

- **Marcelo Morales**  
- **Eliana García**  
- **Joaquín Palta**

📘 *Pontificia Universidad Católica de Valparaíso – Escuela de Ingeniería Informática*  
