ZombieSurvivor-GM — Proyecto académico PUCV. LibGDX (core + lwjgl3 + parent).

# 🧟‍♂️ Zombie Survivor (GM)

Proyecto desarrollado en **Java** utilizando el framework **libGDX**, como parte del curso *Programación Avanzada*.  
El jugador controla a un superviviente en un mundo invadido por zombis, enfrentando oleadas cada vez más difíciles hasta ser derrotado.

---

## ⚙️ Instrucciones de instalación y ejecución del proyecto

### 🔸 Requisitos previos
Antes de ejecutar el juego, asegúrate de tener instalado:

- ☕ **Java JDK 8 o superior**  
- 💻 **Eclipse IDE for Java Developers**  
- 🎮 **Librerías de libGDX**  
- ⚙️ *(Opcional)* **Gradle** para configurar dependencias automáticamente  

---

### 🔸 Instalación

1. **Descargar el proyecto desde GitHub:**  
   Ve al repositorio 👉 [ZombieSurvivor-GM](https://github.com/marselomorales/ZombieSurvivor-GM)  
   y haz clic en **Code → Download ZIP**.

2. **Extraer el archivo ZIP** en tu computadora.

3. Dentro de la carpeta extraída, busca y **entra en la carpeta `SpaceNav2024`**.

4. **Abrir el proyecto en Eclipse:**
   - En Eclipse, selecciona **File → Import → Gradle → Existing Gradle Project**
   - Busca la carpeta `SpaceNav2024`
   - Haz clic en **Finish**

5. Espera a que Eclipse sincronice el proyecto y descargue las dependencias.

---

### 🔸 Ejecución del juego

1. En Eclipse, abre la clase principal:  
desktop/src/puppy/code/DesktopLauncher.java
2. Clic derecho sobre el archivo → **Run As → Java Application**

3. Espera a que se cargue la ventana del juego.

4. ¡Empieza a sobrevivir a las oleadas de zombis! 🧟‍♀️🔥

---

### 🔸 Controles del juego

| Acción | Control |
|--------|----------|
| 🕹️ Movimiento | **W, A, S, D** o **Flechas direccionales** |
| 🎯 Apuntar | Movimiento del **mouse** |
| 🔫 Disparar | **Clic izquierdo** o **Barra espaciadora** |
| 🔁 Reiniciar / Pausa | **Tecla o clic** según la pantalla |
| 🎯 Objetivo | Sobrevive el mayor tiempo posible y derrota a los **MiniJefes** por ronda |

---
### 🔸 Estructura del proyecto
ZombieSurvivor-GM/
├── core/ → Lógica principal del juego/
├── desktop/ → Lanzador del juego en PC/
├── android/ → (opcional) versión móvil/
├── assets/ → Recursos: sprites, sonidos, fuentes/
└── build.gradle → Archivo de configuración (si usa Gradle)/

💀 *Proyecto realizado por Marcelo Morales, Eliana García y Joaquín Palta*  
📘 Pontificia Universidad Católica de Valparaíso – Escuela de Ingeniería Informática
