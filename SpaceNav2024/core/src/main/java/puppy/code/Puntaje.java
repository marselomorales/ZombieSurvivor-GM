package puppy.code;

public class Puntaje {
    private static Puntaje instancia;
    private int score;
    private int highScore;
    
    private Puntaje() {
        score = 0;
        highScore = 0;
    }
    
    public static Puntaje get() {
        if (instancia == null) {
            instancia = new Puntaje();
        }
        return instancia;
    }
    
    public void sumar(int puntos) {
        score += puntos;
        if (score > highScore) {
            highScore = score;
        }
    }
    
    public void reset() {
        score = 0;
    }
    
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    
    // Para compatibilidad con la plantilla existente
    public void setHighScore(int highScore) { this.highScore = highScore; }
}